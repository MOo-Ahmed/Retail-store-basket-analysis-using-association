import java.io.IOException;
import java.io.RandomAccessFile;

public class App {
    public static void main(String[] args) throws Exception {
        App app = new App();
        app.start();
    }

    public void start() throws IOException {
        /* 
        RandomAccessFile file2 = new RandomAccessFile("C1.txt", "rw");
        generateTransactionSetsFile(file, 88162);
        ArrayList<Integer> a = new ArrayList() , b = new ArrayList(), c = new ArrayList();
        System.out.println(c);
        ArrayList<Integer> elements = new ArrayList<Integer>(), elements2 = new ArrayList<Integer>();
        elements.add(1);
        elements2.add(2);
        elements.add(2);
        elements.add(5);
        elements.add(4);
        Record r = new Record(elements, elements2);
        System.out.println(AssociationRulesGenerator.generateAllAssociationRuleCombinations(r));

        */
        
        //System.out.println(getItemsetComplement(elements2, elements));
        //elements.add("4");
        RandomAccessFile file = new RandomAccessFile("SampleDataSet.txt", "rw");
        AssociationRulesGenerator.perfromVerticalDataFormatAlgorithm(file, 0.222, 0.7);
        file.close();
    }
    
    /* public ArrayList<Integer> getAllUniqueItems(RandomAccessFile file) throws IOException {
        ArrayList<Integer> records = new ArrayList<>();
        file.seek(0);
        for(int i = 0 ; i < 88162 ; i++){
            String line = file.readLine();
            ArrayList<Integer> tempItems = parseStringToIntegerArray(line, " ");
            addOnlyUniqueItems(tempItems, records);
        }
        return records;
    }
 */
    
}
