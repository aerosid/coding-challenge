package splore;

public class Calculator {
  public static void main(String[] args) throws Throwable {    
    TravelLog travelLog = TravelLog.newInstance();
    System.out.println("total: " + travelLog.getTotalFare());
    return;
  }
}
