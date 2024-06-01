package splore;

import org.junit.Assert;
import org.junit.Test;


public class TravelLogTest {

  @Test
  public void testSample() throws Throwable {
    String value = "/home/ubuntu/vscode/coding-challenge/"
        + "src/main/resources/splore/travel-log.csv";
    System.setProperty("splore.TravelLog", value);
    TravelLog travelLog = TravelLog.newInstance();
    String totalFare = travelLog.getTotalFare();
    Assert.assertEquals("$8.00", totalFare);
  }

  @Test
  public void testDailyCap() throws Throwable {
    String value = "/home/ubuntu/vscode/coding-challenge/"
        + "src/test/resources/splore/daily-cap.csv";
    System.setProperty("splore.TravelLog", value);
    TravelLog travelLog = TravelLog.newInstance();
    String totalFare = travelLog.getTotalFare();
    Assert.assertEquals("$15.00", totalFare);
  }

  @Test
  public void testWeeklyCap() throws Throwable {
    String value = "/home/ubuntu/vscode/coding-challenge/"
        + "src/test/resources/splore/weekly-cap.csv";
    System.setProperty("splore.TravelLog", value);
    TravelLog travelLog = TravelLog.newInstance();
    String totalFare = travelLog.getTotalFare();
    Assert.assertEquals("$90.00", totalFare);
  }

}
