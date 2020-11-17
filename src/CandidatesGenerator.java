import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class CandidatesGenerator {
    public static int generateFrequentItemsets(int N, int minSupportCount, RandomAccessFile file) throws IOException {
        ArrayList<Record> records = generateTransactionSetsFile(file, N, minSupportCount); // This file represents C1
        //String candidate_1_filename = "candidates/C1.txt" ;
        int itemsetSize = 2 ; // We have already prepared 1-itemsets
        boolean moreFrequentSupersets = true ; // If false, stop and generate the association rules
        ArrayList<Record> nextCandidate = new ArrayList();
        for(; moreFrequentSupersets ;){
            nextCandidate = getAllItemsetCombinations(records, minSupportCount);
            if(nextCandidate.isEmpty()){
                //System.out.println("I got here");
                break ;
            }
            else{
                writeCandidate(nextCandidate, itemsetSize++);
                records.clear();
                records.addAll(nextCandidate);
                nextCandidate.clear();
            }
        } 
        return --itemsetSize;
    }

    public static ArrayList<Record> generateTransactionSetsFile(RandomAccessFile file, int N, int minSupportCount)
     throws IOException {
        /*
         * 1- Traverse the dataset file, transaction by transaction 2- Get the itemset
         * of the cusrrent transaction, iterate through each item 3- If the list of
         * records contains the current item 3.1- Add the index of the current
         * transaction to the item Tr. set 3.2- Else -> Add new record with the item and
         * add the current Tr to its Tr. set
         */
        ArrayList<Record> records = new ArrayList<>();
        file.seek(0);
        for (int i = 0; i < N; i++) {
            String line = readTransaction(file);
            ArrayList<Integer> items = parseStringToIntegerArray(line, " ");
            for (int j = 0; j < items.size(); j++) {
                int indexOfItemInRecords = getIndexWhereItemRecorded(items.get(j), records);
                if (indexOfItemInRecords == -1) {
                    // It should be inserted
                    ArrayList<Integer> tempItems = new ArrayList(), tempTransaction = new ArrayList();
                    tempItems.add(items.get(j));
                    tempTransaction.add(i + 1);
                    Record r = new Record(tempItems, tempTransaction);
                    records.add(r);
                } else {
                    // We just need to add T number
                    records.get(indexOfItemInRecords).Transactions.add(i + 1);
                }
            }
        }
        filterCandidate(records, minSupportCount);
        writeCandidate(records, 1);
        return records ;
    }

    public static ArrayList<Record> getAllItemsetCombinations(ArrayList<Record> records, int minSupportCount) throws IOException {
        ArrayList<Record> combinations = new ArrayList();
        for(int i = 0 ; i < records.size() ; i++){
            Record r1 = records.get(i);
            for(int j = i + 1 ; j < records.size(); j++){
                Record r2 = records.get(j);
                ArrayList<Integer> mixedItems = addOnlyUniqueItems(r1.Items, r2.Items);
                ArrayList<Integer> commmonTransactions = getCommonTransactions(r1.Transactions, r2.Transactions);
                if(commmonTransactions.size() >= minSupportCount && !isItemsetRedundant(combinations, mixedItems)){
                    Record newRecord = new Record(mixedItems, commmonTransactions);
                    combinations.add(newRecord);
                }
                mixedItems.clear();
                commmonTransactions.clear();
            }
        }
        return combinations ;
    }

    public static boolean isItemsetRedundant(ArrayList<Record> combinations, ArrayList<Integer> Items){
        boolean b = false ;
        for(int i = 0 ; i < combinations.size() ; i++){
            ArrayList<Integer> tempItems = combinations.get(i).Items;
            if(tempItems.containsAll(Items)){
                return true;
            }
        }
        return b ;
    }

    public static ArrayList<Integer> getCommonTransactions(ArrayList<Integer> A1, ArrayList<Integer> A2){
        ArrayList<Integer> A = new ArrayList();
        int n1 = A1.size(), n2 = A2.size();
        if(n1 < n2){
            A.addAll(A1);
            for(int i = 0 ; i < n1; i++){
                if(A2.indexOf(A.get(i)) == -1){
                    A.remove(i);
                    i-- ;
                    n1-- ;
                }
            }
        }
        else{
            A.addAll(A2);
            for(int i = 0 ; i < n2; i++){
                if(A1.indexOf(A.get(i)) == -1){
                    A.remove(i);
                    i-- ;
                    n2-- ;
                }
            }
        }
        return A;
    }

    private static void filterCandidate(ArrayList<Record> records, int minSupportCount) {
        int N = records.size();
        for(int i = 0 ; i < N ; i++){
            int n = records.get(i).Transactions.size();
            if(n < minSupportCount){
                records.remove(i);
                i-- ;
                N-- ;
            }
        }
    }

    public static void writeCandidate(ArrayList<Record> records, int candidateNumber) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("candidates/C" + candidateNumber + ".txt"));
        for (int i = 0; i < records.size(); i++) {
            bw.write(records.get(i).toString() + "\n");
        }
        bw.close();
    }

    public static String readTransaction(RandomAccessFile file) throws IOException {
        return file.readLine();
    }

    public static int getIndexWhereItemRecorded(int item, ArrayList<Record> records){
        // This function checks if we recorded this item before. If true -> returns the index
        // Else -> returns -1
        for(int i = 0 ; i < records.size(); i++){
            if(records.get(i).Items.get(0) == item){
                return i ;
            }
        }
        return -1 ;
    }

    public static int getNumberOfTransactions(RandomAccessFile file) throws IOException {
        int count = 0 ;
        for(int i = 0 ; file.readLine() != null ;){
            count++;
        }
        return count;
    }

    public static ArrayList<Integer> parseStringToIntegerArray(String s, String delimiter){
        // This function reads a line from the data set file, then it returns the unique integers
        // in it as a list
        ArrayList<Integer> items = new ArrayList<>();
        String stringParts[] = s.split(delimiter);
        for(int i = 0 ; i < stringParts.length ; i++){
            int num = Integer.parseInt(stringParts[i]);
            if(items.indexOf(num) == -1)
                items.add(num);
        }
        return items;
    }

    public static ArrayList<Integer> addOnlyUniqueItems(ArrayList<Integer> A1, ArrayList<Integer> A2){
        // Add only the items existing only in A1 to A2, return new list
        ArrayList<Integer> A = new ArrayList();
        A.addAll(A2);
        for(int i = 0 ; i < A1.size() ; i++){
            int x = A1.get(i);
            if(A.indexOf(x) == -1){
                A.add(x);
            }
        }
        return A ;
    }

}
