package splore;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FareCap {

  HashMap<String, Double> dailyCap; /*String: "from-to", Double: dailyCap*/

  HashMap<String, Double> weeklyCap; /*String: "from-to", Double: weekly*/

  private static FareCap theInstance;

  public FareCap() {
    super();
    InputStream input = this.getClass().getResourceAsStream("/splore/fare-cap.csv");
    Scanner scanner = new Scanner(input);
    this.readFareCap(scanner);
  }

  public FareCap(String filePath) throws Throwable {
    super();
    File file = new File(filePath);
    Scanner scanner = new Scanner(file);
    this.readFareCap(scanner);
  }

  public Double lookupDailyCap(String from, String to) {
    String key = from + "-" + to;
    Double dailyCap = this.dailyCap.get(key);
    return dailyCap;
  }

  public Double lookupWeeklyCap(String from, String to) {
    String key = from + "-" + to;
    Double weeklyCap = this.weeklyCap.get(key);
    return weeklyCap;
  }

  void readFareCap(Scanner scanner) {
    this.dailyCap = new HashMap<String, Double>();
    this.weeklyCap = new HashMap<String, Double>();
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
        Double dailyCap = Double.valueOf(list.get(2));
        Double weeklyCap = Double.valueOf(list.get(3));
        this.dailyCap.put(key, dailyCap);
        this.weeklyCap.put(key, weeklyCap);
      }
      //System.out.println(textLine);
    }
    scanner.close();
    return;
  }

  public static FareCap theInstance() throws Throwable {
    if (FareCap.theInstance == null) {
      String filePath = System.getProperty("splore.FareCap");
      if (filePath == null) {
        FareCap.theInstance = new FareCap();
      } else {
        FareCap.theInstance = new FareCap(filePath);
      }
    }
    return FareCap.theInstance;
  }

  public static FareCap theInstance(Boolean reload) throws Throwable { 
    if (reload.booleanValue()) {
      FareCap.theInstance = null;
    }
    return FareCap.theInstance();
  }

}
