import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class AssociationRulesGenerator {

    public static ArrayList<ArrayList<Integer>> generateLHS(ArrayList<Integer> elements, int N) {
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
        ArrayList<Integer> result = new ArrayList<Integer>() ;
        ArrayList<Integer> copyOfOriginal = new ArrayList<Integer>();
        copyOfOriginal.addAll(Original);
        copyOfOriginal.removeAll(list);
        result.addAll(copyOfOriginal);
        return result ;
    }

    public static void perfromVerticalDataFormatAlgorithm(RandomAccessFile file, double minSup, double minConf)
            throws IOException {
        /*
        1- prepare C1
        2- From C1, prepare all combinations
        3- These combinations are to be stored in C2
        */
        int N = CandidatesGenerator.getNumberOfTransactions(file);
        int minSupportCount = (int) Math.ceil(minSup * N);
        int lastCandidateNumber = CandidatesGenerator.generateFrequentItemsets(N, minSupportCount, file);
        generateAllAssociationRules(lastCandidateNumber, minConf);
        // Till here you have generated all frequent itemsets.
        // Next step is to generate all possible association rules from the last table of frequent itemsets
        // Then exclude whatever rules that don't satisfy the minConf
         

    }

    public static void generateAllAssociationRules(int lastCandidateNumber, double minConf) throws IOException {
        /*
        1- Pick each frequent itemset from the last candidate file
        2- From the itemset, generate all different association rules
        3- For each rule, go get its support counts
        4- If the rule satisfies the minConf then write it to the file 
        */

        RandomAccessFile file = new RandomAccessFile("candidates/C" + lastCandidateNumber + ".txt", "rw");
        file.seek(0);
        ArrayList<Record> resultAssociationRules = new ArrayList<Record>();
        ArrayList<Record> allAssociationRules = new ArrayList<Record>(); // But not all satisfactory
        Record r = readRecord(file);
        // From this record you should get all the association rules
        for(int i = 0 ; r != null ; i++){
            //This loop stops when we have read all frequent itemsets
            // Check the minConf threshold .. if yes -> write to a file
            allAssociationRules = generateAllAssociationRuleCombinations(r);
            for(int j = 0 ; j < allAssociationRules.size() ; j++){
                //This loop stops when we have checked all association rules generated from the the record r
                ArrayList<Integer> union = new ArrayList<>();
                union.addAll(allAssociationRules.get(j).Items);
                union.addAll(allAssociationRules.get(j).Transactions);

                int LHSSupportCount = getItemsetSupportCount(allAssociationRules.get(j).Items);
                int UnionSupportCount = getItemsetSupportCount(union);
                double confidence = UnionSupportCount / LHSSupportCount ;
                if(confidence >= minConf){
                    resultAssociationRules.add(allAssociationRules.get(j));
                }
            }
            r = readRecord(file);
        }
        file.close();
        CandidatesGenerator.writeCandidate(resultAssociationRules, 0);
        // We should have C0.txt file where we have association rules
    }

    public static int getItemsetSupportCount(ArrayList<Integer> itemset) throws IOException {
        int supCount = 0 ;
        int itemsetSize = itemset.size();
        String candidateFileName = "candidates/C" + itemsetSize + ".txt" ;
        RandomAccessFile file = new RandomAccessFile(candidateFileName, "r");
        boolean notFound = true ;
        while(notFound){
            Record r = readRecord(file);
            if(r.Items.containsAll(itemset) && itemsetSize == r.Items.size()){
                // Then this is the intended record
                supCount = r.Transactions.size();
                notFound = false ;
            }
        }
        file.close();
        return supCount;
    }

    public void writeAssociationRecord(ArrayList<Record> records) throws IOException {
        String associationFilename = "rules.txt";
        BufferedWriter bw = new BufferedWriter(new FileWriter("candidates/" + associationFilename));
        for (int i = 0; i < records.size(); i++) {
            bw.write(records.get(i).toString() + "\n");
        }
        bw.close();
    }

    public static Record readRecord(RandomAccessFile file) throws IOException {
        if(file.getFilePointer() == file.length()){
            return null ;
        }
        String RECORD = file.readLine();
        String itemLine = RECORD.split(">")[0];
        String transactionsLine = RECORD.split(">")[1];
        ArrayList<Integer> Items = CandidatesGenerator.parseStringToIntegerArray(itemLine, ","),
                            Transactions = CandidatesGenerator.parseStringToIntegerArray(transactionsLine, ",");
        Record r = new Record(Items, Transactions);
        return r ;
    }

    public static ArrayList<Record> generateAllAssociationRuleCombinations(Record r){
        //{1,2,4,7} 
        ArrayList<ArrayList<Integer>> LHS = generateLHS(r.Items, r.Items.size());
        ArrayList<Record> Associations = new ArrayList<Record>();
        for(int i = 0 ; i < LHS.size() ; i++){
            Associations.add(new Record(LHS.get(i), getItemsetComplement(LHS.get(i), r.Items)));
        }
        return Associations ;
    }   

}
