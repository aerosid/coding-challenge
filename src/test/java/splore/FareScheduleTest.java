package splore;

import org.junit.Assert;
import org.junit.Test;

public class FareScheduleTest {

  @Test
  public void testStaticAccessor() throws Throwable {
    FareSchedule theInstance = FareSchedule.theInstance();
    Assert.assertNotNull(theInstance);
    String value = "/home/ubuntu/vscode/coding-challenge/"
        + "src/main/resources/splore/fare-schedule.csv";
    System.setProperty("splore.FareSchedule", value);
    theInstance = FareSchedule.theInstance(true);
    Assert.assertNotNull(theInstance);
  }

  @Test
  public void testIsPeak() throws Throwable {
    FareSchedule theInstance = FareSchedule.theInstance();
    Assert.assertTrue(theInstance.isPeak(1, 8, 0));
    Assert.assertFalse(theInstance.isPeak(1, 10, 0));
    Assert.assertTrue(theInstance.isPeak(2, 16, 30));
    Assert.assertTrue(theInstance.isPeak(2, 17, 00));
    Assert.assertFalse(theInstance.isPeak(3, 20, 5));
    Assert.assertTrue(theInstance.isPeak(6, 10, 00));
    Assert.assertFalse(theInstance.isPeak(6, 15, 00));
    Assert.assertTrue(theInstance.isPeak(7, 18, 00));
    Assert.assertFalse(theInstance.isPeak(7, 23, 00));
    return;
  }

  @Test
  public void testLookupFare() throws Throwable {
    FareSchedule theInstance = FareSchedule.theInstance();
    Double fare = 0D;
    fare = theInstance.lookupFare("red", "green", 1, 8, 30);
    Assert.assertEquals(Double.valueOf(3D), fare);
    fare = theInstance.lookupFare("red", "red", 3, 15, 30);
    Assert.assertEquals(Double.valueOf(2D), fare);
    return;
  }

}
