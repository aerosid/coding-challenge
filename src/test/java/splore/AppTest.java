package splore;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

import org.junit.Test;

public class AppTest {

  @Test
  public void shouldAnswerWithTrue() {
    assertTrue(true);
  }

  @Test
  public void testClasspathResource() {
    InputStream input = this.getClass().getResourceAsStream("/splore/travel-log.csv");
    Scanner scanner = new Scanner(input);
    while (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    scanner.close();
    return;
  }

  @Test
  public void testFile() throws Throwable {
    File file = new File("./src/main/resources/splore/travel-log.csv");
    Scanner scanner = new Scanner(file);
    while (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    scanner.close();
    return;
  }

  @Test
  public void testTokenizer() {
    String textLine = "Green,Red  , 2021-03-24T07:58:30";
    Scanner scanner = new Scanner(textLine).useDelimiter(",\\s*");
    while (scanner.hasNext()) {
      scanner.next();
    }
    scanner.close();
    return;
  }

}
