package splore;

import org.junit.Assert;
import org.junit.Test;

public class FareCapTest {
  
  @Test
  public void testStaticAccessor() throws Throwable {
    FareCap theInstance = FareCap.theInstance();
    Assert.assertNotNull(theInstance);
    String value = "/home/ubuntu/vscode/coding-challenge/"
        + "src/main/resources/splore/fare-cap.csv";
    System.setProperty("splore.FareCap", value);
    theInstance = FareCap.theInstance(true);
    Assert.assertNotNull(theInstance);
    return;
  }

  @Test
  public void testLookupDailyCap() throws Throwable {
    FareCap theInstance = FareCap.theInstance();
    Double dailyCap = 0D;
    dailyCap = theInstance.lookupDailyCap("red", "green");
    Assert.assertEquals(Double.valueOf(15D), dailyCap);
    dailyCap = theInstance.lookupDailyCap("red", "red");
    Assert.assertEquals(Double.valueOf(12D), dailyCap);
    return;
  }  

  @Test
  public void testLookupWeeklyCap() throws Throwable {
    FareCap theInstance = FareCap.theInstance();
    Double dailyCap = 0D;
    dailyCap = theInstance.lookupWeeklyCap("red", "green");
    Assert.assertEquals(Double.valueOf(90D), dailyCap);
    dailyCap = theInstance.lookupWeeklyCap("red", "red");
    Assert.assertEquals(Double.valueOf(70D), dailyCap);
    return;
  }  

}
