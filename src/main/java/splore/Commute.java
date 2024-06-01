package splore;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Commute {

  Double appliedFare;

  LocalDateTime dateTime;

  Double fare;

  String from;

  String timestamp;

  String to;

  Commute(String textLine) throws Throwable {
    super();
    //e.g.: Green, Red, 2021-03-24T09:58:30
    Scanner scanner = new Scanner(textLine.trim());
    List<String> list = scanner
        .useDelimiter(",\\s*")
        .tokens()
        .collect(Collectors.toList());
    scanner.close();
    this.from = list.get(0).toLowerCase();
    this.to = list.get(1).toLowerCase();
    this.timestamp = list.get(2);
    this.dateTime = LocalDateTime.parse(this.timestamp);
    this.fare = FareSchedule.theInstance().lookupFare(
      this.from, 
      this.to, 
      this.getDayOfWeek(), 
      this.getHourOfDay(), 
      this.getMinuteOfHour());
    this.appliedFare = this.fare;
  }

  public Integer getDayOfWeek() {
    //Monday = 1, Saturday = 7
    return this.dateTime.get(ChronoField.DAY_OF_WEEK);
  }

  public Integer getDayOfYear() {
    return this.dateTime.get(ChronoField.DAY_OF_YEAR); 
  }

  public Integer getHourOfDay() {
    return this.dateTime.get(ChronoField.HOUR_OF_DAY);
  }

  public Integer getMinuteOfHour() {
    return this.dateTime.get(ChronoField.MINUTE_OF_HOUR);
  }  
  
  public Integer getWeekOfYear() {
    return this.dateTime.get(WeekFields.ISO.weekOfWeekBasedYear());
  }

  public Integer getYear() {
    return this.dateTime.getYear();
  }

}
