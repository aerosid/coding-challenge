package splore;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TravelLog {

  HashMap<String, Double> dayOfYearTotal; /*String: "from-to-year-dayOfYear"*/

  Double totalFare;

  ArrayList<Commute> travelLog;

  HashMap<String, Double> weekOfYearTotal; /*String: "from-to-year-weekOfYear"*/

  private TravelLog() throws Throwable {
    super();
    InputStream input = this.getClass().getResourceAsStream("/splore/travel-log.csv");
    Scanner scanner = new Scanner(input);
    this.readTravelLog(scanner);
  }

  private TravelLog(String filePath) throws Throwable {
    super();
    File file = new File(filePath);
    Scanner scanner = new Scanner(file);
    this.readTravelLog(scanner);
  }

  void addCommute(String line) throws Throwable {
    Commute c = new Commute(line);
    this.addCommute(c);
    return;
  }

  void addCommute(Commute c) throws Throwable {
    /* update dayOfYear/weekOfYear/totalFare */
    this.travelLog.add(c);
    Double appliedDailyFare = this.getAppliedDailyFare(c);
    Double appliedWeeklyFare = this.getAppliedWeeklyFare(c);
    Double appliedFare = Double.min(
        appliedDailyFare.doubleValue(),
        appliedWeeklyFare.doubleValue());
    c.appliedFare = appliedFare;
    this.addFare(c);
    return;
  }

  void addFare(Commute c) throws Throwable {
    Double weeklyCap = FareCap.theInstance().lookupWeeklyCap(c.from, c.to);
    String weekOfYearKey = c.from + "-" + c.to + "-" + c.getYear() + "-" + c.getWeekOfYear();
    if (this.weekOfYearTotal.containsKey(weekOfYearKey)) {
      Double weekOfYearTotal = this.dayOfYearTotal.get(weekOfYearKey);
      weekOfYearTotal = Double.valueOf(weekOfYearTotal.doubleValue() + c.appliedFare.doubleValue());
      weekOfYearTotal = Double.min(weekOfYearTotal.doubleValue(), weeklyCap.doubleValue());
      this.dayOfYearTotal.put(weekOfYearKey, weekOfYearTotal);
    } else {
      this.dayOfYearTotal.put(weekOfYearKey, c.appliedFare);
    }

    String dayOfYearKey = c.from + "-" + c.to + "-" + c.getYear() + "-" + c.getDayOfYear();
    if (this.dayOfYearTotal.containsKey(dayOfYearKey)) {
      Double dayOfYearTotal = this.dayOfYearTotal.get(dayOfYearKey);
      dayOfYearTotal = Double.valueOf(dayOfYearTotal.doubleValue() + c.appliedFare.doubleValue());
      this.dayOfYearTotal.put(dayOfYearKey, dayOfYearTotal);
    } else {
      this.dayOfYearTotal.put(dayOfYearKey, c.appliedFare);
    }

    this.totalFare = Double.valueOf(this.totalFare.doubleValue() + c.appliedFare.doubleValue());

    return;
  }

  Double getAppliedDailyFare(Commute c) throws Throwable {
    Double appliedDailyFare = c.fare;
    Double dailyCap = FareCap.theInstance().lookupDailyCap(c.from, c.to);
    String dayOfYearKey = c.from + "-" + c.to + "-" + c.getYear() + "-" + c.getDayOfYear();
    if (this.dayOfYearTotal.containsKey(dayOfYearKey)) {
      Double dayOfYearTotal = this.dayOfYearTotal.get(dayOfYearKey);
      if ((dayOfYearTotal.doubleValue() + c.fare.doubleValue()) > dailyCap.doubleValue()) {
        appliedDailyFare = Double.valueOf(dailyCap.doubleValue() - dayOfYearTotal.doubleValue());
      }
    }   
    return appliedDailyFare;
  }

  Double getAppliedWeeklyFare(Commute c) throws Throwable {
    Double appliedWeeklyFare = c.fare;
    Double weeklyCap = FareCap.theInstance().lookupWeeklyCap(c.from, c.to);
    String weekOfYearKey = c.from + "-" + c.to + "-" + c.getYear() + "-" + c.getWeekOfYear();
    if (this.weekOfYearTotal.containsKey(weekOfYearKey)) {
      Double weekOfYearTotal = this.dayOfYearTotal.get(weekOfYearKey);
      if ((weekOfYearTotal.doubleValue() + c.fare.doubleValue()) > weeklyCap.doubleValue()) {
        appliedWeeklyFare = Double.valueOf(weeklyCap.doubleValue() - weekOfYearTotal.doubleValue());
      }
    }   
    return appliedWeeklyFare;
  }  

  public String getTotalFare() {
    /* formated as $#,###.00 */
    DecimalFormat format = new DecimalFormat("#,##0.00;(#,##0.00)");
    String totalFare = "$" + format.format(this.totalFare.doubleValue());
    return totalFare;
  }

  public static TravelLog newInstance() throws Throwable {
    TravelLog newInstance = null;
    String filePath = System.getProperty("splore.TravelLog");
    if (filePath == null) {
      newInstance = new TravelLog();
    } else {
      newInstance = new TravelLog(filePath);
    }
    return newInstance;
  }

  void readTravelLog(Scanner scanner) throws Throwable {
    this.dayOfYearTotal = new HashMap<String, Double>();
    this.totalFare = 0D;
    this.travelLog = new ArrayList<Commute>();
    this.weekOfYearTotal = new HashMap<String, Double>();
    while (scanner.hasNextLine()) {
      String textLine = scanner.nextLine();
      if (!textLine.startsWith("#")) {
        this.addCommute(textLine.trim());
      }
      //System.out.println(textLine);
    }
    scanner.close();
    return;
  }

}
