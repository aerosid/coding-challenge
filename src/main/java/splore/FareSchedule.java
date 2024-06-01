package splore;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FareSchedule {

  HashMap<String, Double> offPeakHourFare; /*String: "from-to", Double: fare*/

  HashMap<String, Double> peakHourFare; /*String: "from-to", Double: fare*/

  private static FareSchedule theInstance;

  public FareSchedule() {
    super();
    InputStream input = this.getClass().getResourceAsStream("/splore/fare-schedule.csv");
    Scanner scanner = new Scanner(input);
    this.readFareSchedule(scanner);
  }

  public FareSchedule(String filePath) throws Throwable {
    super();
    File file = new File(filePath);
    Scanner scanner = new Scanner(file);
    this.readFareSchedule(scanner);
  }

  Boolean isPeak(Integer dayOfWeek, Integer hourOfDay, Integer minuteOfHour) {
    //Monday to Friday - 8:00 to 10:00, 16:30 to 19:00
    //Saturday - 10:00 to 14:00, 18:00 to 23:00
    //Sunday - 18:00 to 23:00
    boolean isPeak = false;
    if (dayOfWeek.intValue() < 6) {  //Mon-Fri
      if (hourOfDay.intValue() >= 8 && hourOfDay.intValue() < 10) {
        isPeak = true;
      }
      if (hourOfDay.intValue() == 16 && minuteOfHour.intValue() >= 30) {
        isPeak = true;
      }
      if (hourOfDay.intValue() >= 17 && hourOfDay.intValue() < 19) {
        isPeak = true;
      }
    } else if (dayOfWeek.intValue() == 6) { //Sat
      if (hourOfDay.intValue() >= 10 && hourOfDay.intValue() < 14) {
        isPeak = true;
      }
      if (hourOfDay.intValue() >= 18 && hourOfDay.intValue() < 23) {
        isPeak = true;
      }
    } else { //Sun
      if (hourOfDay.intValue() >= 18 && hourOfDay.intValue() < 23) {
        isPeak = true;
      }
    }
    return Boolean.valueOf(isPeak);
  }

  public Double lookupFare(
      String from,
      String to,
      Integer dayOfWeek,
      Integer hourOfDay,
      Integer minuteOfHour) {
    String key = from + "-" + to;
    boolean isPeak = this.isPeak(dayOfWeek, hourOfDay, minuteOfHour);
    Double fare = Double.valueOf(0D);
    if (isPeak) {
      fare = this.peakHourFare.get(key);
    } else {
      fare = this.offPeakHourFare.get(key);
    }
    return fare;
  }

  void readFareSchedule(Scanner scanner) {
    this.peakHourFare = new HashMap<String, Double>();
    this.offPeakHourFare = new HashMap<String, Double>();
    while (scanner.hasNextLine()) {
      String textLine = scanner.nextLine();
      if (!textLine.startsWith("#")) {
        Scanner tokenizer = new Scanner(textLine.trim());
        List<String> list = tokenizer
            .useDelimiter(",\\s*")
            .tokens()
            .collect(Collectors.toList());
        tokenizer.close();
        String from = list.get(0).toLowerCase();
        String to = list.get(1).toLowerCase();
        String key = from + "-" + to;
        Double peakFare = Double.valueOf(list.get(2).substring(1));
        Double offPeakFare = Double.valueOf(list.get(3).substring(1));
        this.peakHourFare.put(key, peakFare);
        this.offPeakHourFare.put(key, offPeakFare);
      }
      //System.out.println(textLine);
    }
    scanner.close();
    return;
  }

  public static FareSchedule theInstance() throws Throwable {
    if (FareSchedule.theInstance == null) {
      String filePath = System.getProperty("splore.FareSchedule");
      if (filePath == null) {
        FareSchedule.theInstance = new FareSchedule();
      } else {
        FareSchedule.theInstance = new FareSchedule(filePath);
      }

    }
    return FareSchedule.theInstance;
  }

  public static FareSchedule theInstance(Boolean reload) throws Throwable { 
    if (reload.booleanValue()) {
      FareSchedule.theInstance = null;
    }
    return FareSchedule.theInstance();
  }

}
