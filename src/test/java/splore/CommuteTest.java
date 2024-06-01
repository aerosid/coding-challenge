package splore;

import org.junit.Assert;
import org.junit.Test;

public class CommuteTest {

  @Test
  public void testConstructor() throws Throwable {
    String textLine = "Green, Red, 2024-01-01T12:15:00";
    Commute c = new Commute(textLine);
    Assert.assertEquals(1, c.getDayOfWeek().intValue());
    Assert.assertEquals(1, c.getDayOfYear().intValue());
    Assert.assertEquals(12, c.getHourOfDay().intValue());
    Assert.assertEquals(15, c.getMinuteOfHour().intValue());
    Assert.assertEquals(1, c.getWeekOfYear().intValue());
    Assert.assertEquals(2024, c.getYear().intValue());
    Assert.assertEquals(Double.valueOf(3D), c.fare);
    return;
  }
  
}
