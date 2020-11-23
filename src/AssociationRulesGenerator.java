import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class AssociationRulesGenerator {

    public static ArrayList<ArrayList<Integer>> generateLHS(ArrayList<Integer> elements, int N) {
        //This function generates all possible permuations without repetions
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        long limit = 1 << elements.size(); 
        
        // count from 1 to n^2 - 1
        for (long i = 1; i < limit; ++i) {
            ArrayList<Integer> seq = new ArrayList<Integer>();
    
        // for each position (character) decide, whether to include it based on the state of the bit.
        for (int pos = 0; pos < elements.size(); ++pos) {
            boolean include = ((i >> pos) % 2) == 1; // this line will give you true, if the in 'i' the bit at 'pos' (from behind) is 1, and false otherwise.
            if (include) {
            seq.add(elements.get(pos));
            }
        }
        if(seq.size() != N)
            // add to the final result the newly generated String.
            result.add(seq);
    }
  
    return result;
  }   

    public static ArrayList<Integer> getItemsetComplement(ArrayList<Integer> list, ArrayList<Integer> Original){
        //Get the RHS items of the rules
        ArrayList<Integer> copyOfOriginal = new ArrayList<Integer>();
        copyOfOriginal.addAll(Original);
        copyOfOriginal.removeAll(list);
        return copyOfOriginal ;
    }

    public static void perfromVerticalDataFormatAlgorithm(String datasetFileName, int minSupportCount, double minConf)
            throws IOException {
        /*
        1- prepare C1
        2- From C1, prepare all combinations
        3- These combinations are to be stored in C2
        */
        
        RandomAccessFile file = new RandomAccessFile(datasetFileName, "r");
        int N = CandidatesGenerator.getNumberOfTransactions(file);
        int lastCandidateNumber = CandidatesGenerator.generateFrequentItemsets(N, minSupportCount, file);
        file.close();
        System.out.println("Finished generating frequent itemsets");
        
        // Till here you have generated all frequent itemsets.
        // Next step is to generate all possible association rules from the last table of frequent itemsets
        // Then exclude whatever rules that don't satisfy the minConf
         
        generateAllAssociationRules(lastCandidateNumber, minConf);
        System.out.println("Finished generating association rules .. \nHave a look on the file C0.txt");
        
    }

    public static void generateAllAssociationRules(int lastCandidateNumber, double minConf) throws IOException {
        /*
        1- Pick each frequent itemset from the last candidate file
        2- From the itemset, generate all different association rules
        3- For each rule, go get its support counts
        4- If the rule satisfies the minConf then write it to the file 
        */
        BufferedWriter bw = new BufferedWriter(new FileWriter("candidates/C0.txt")) ;
        RandomAccessFile file = new RandomAccessFile("candidates/C" + lastCandidateNumber + ".txt", "rw");
        file.seek(0);
        ArrayList<Record> resultAssociationRules = new ArrayList<Record>(); // The final ones
        ArrayList<Record> allAssociationRules = new ArrayList<Record>(); // But not all satisfactory
        Record r = readRecord(file , false);
        // From this record you should get all the association rules
        for(; r != null ; ){
            //This loop stops when we have read all frequent itemsets
            // Check the minConf threshold .. if yes -> write to a file
            
            allAssociationRules = generateAllAssociationRuleCombinations(r.Items);
            for(int j = 0 ; j < allAssociationRules.size() ; j++){
                //This loop stops when we have checked all association rules generated from the the record r
                ArrayList<Integer> union = new ArrayList<>();
                Record temp = allAssociationRules.get(j) ;
                union.addAll(temp.Items);
                union.addAll(temp.Transactions);                
                int LHSSupportCount = getItemsetSupportCount(temp.Items);
                int UnionSupportCount = getItemsetSupportCount(union);
                double confidence = UnionSupportCount * 1.0 / LHSSupportCount ;
                if(confidence >= minConf){
                    bw.write(temp.toString() + "\n");
                }
                union.clear();
                
            }
            r = readRecord(file , false);
            
        }
        bw.close();
        file.close();
        //CandidatesGenerator.writeCandidate(resultAssociationRules, 0);
        // We shall have C0.txt file where we have association rules
    }

    public static int getItemsetSupportCount(ArrayList<Integer> itemset) throws IOException {
        ArrayList<Integer> temp = new ArrayList<Integer>();
        temp.add(48); temp.add(32); temp.add(41);
        
        int supCount = 0 ;
        int itemsetSize = itemset.size();
        String candidateFileName = "candidates/F" + itemsetSize + ".txt" ;
        RandomAccessFile file = new RandomAccessFile(candidateFileName, "r");
        file.seek(0);
        boolean notFound = true ;
        while(notFound && file.getFilePointer() < file.length()){
            Record r = readRecord(file , true);
            if(r!= null && r.Items.containsAll(itemset) && itemsetSize == r.Items.size()){
                // Then this is the intended record
                supCount = r.Transactions.get(0);
                notFound = false ;
            }
        }
        file.close();
        return supCount;
    }

    public static Record readRecord(RandomAccessFile file, boolean isFrequencyFile) throws IOException {
        if(file.getFilePointer() == file.length()){
            return null ;
        }
        String RECORD = file.readLine();
        String itemLine = RECORD.split(">")[0];
        String transactionsLine = RECORD.split(">")[1];
        ArrayList<Integer> Items = CandidatesGenerator.parseStringToIntegerArray(itemLine, ","),
                            Transactions = new ArrayList<>() ;
        if(isFrequencyFile == true){
            Transactions.add(Integer.parseInt(transactionsLine));
        }
        else Transactions = CandidatesGenerator.parseStringToIntegerArray(transactionsLine, ",");
        Record r = new Record(Items, Transactions);
        return r ;
    }

    public static ArrayList<Record> generateAllAssociationRuleCombinations(ArrayList<Integer> items){
        ArrayList<ArrayList<Integer>> LHS = generateLHS(items, items.size());
        ArrayList<Record> Associations = new ArrayList<Record>();
        for(int i = 0 ; i < LHS.size() ; i++){
            Associations.add(new Record(LHS.get(i), getItemsetComplement(LHS.get(i), items)));
        }
        return Associations ;
    }   
}
