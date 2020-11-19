import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Date;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        App app = new App();
        app.start();
    }

    public void start() throws IOException {
        
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter minimum support count : ");
        int minSupCount = Integer.parseInt(sc.nextLine());
        System.out.print("Enter minimum confidence : ");
        double minConf = Double.parseDouble(sc.nextLine());
        RandomAccessFile file = new RandomAccessFile("RetailDataSet.txt", "rw");
        AssociationRulesGenerator.perfromVerticalDataFormatAlgorithm(file, minSupCount, minConf);
        file.close(); 
    } 
}
