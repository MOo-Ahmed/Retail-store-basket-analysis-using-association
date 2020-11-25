/*
- Made by Mohamed Ahmed Abd El-Nabey
- Last modification : 25/11/2020 at 8:45 pm
- This project applies Vertical data format
  algorithm on retail store dataset to generate association rules
*/
import java.io.IOException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        App app = new App();
        app.start();
    }

    public void start() throws IOException {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter minimum support : ");
        double minSupCount = Double.parseDouble(sc.nextLine());
        System.out.print("Enter minimum confidence : ");
        double minConf = Double.parseDouble(sc.nextLine());
        String fileName = "RetailDataSet.txt" ;
        AssociationRulesGenerator.perfromVerticalDataFormatAlgorithm(fileName, minSupCount, minConf); 
    } 
}
