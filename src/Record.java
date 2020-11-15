import java.util.ArrayList;

public class Record {
    public ArrayList<Integer> Items = new ArrayList();
    public ArrayList<Integer> Transactions = new ArrayList();

    public Record(ArrayList<Integer> items, ArrayList<Integer> Trans){
        Items.addAll(items);
        Transactions.addAll(Trans);
    }

    public Record(){}

    @Override
    public String toString(){
        String output = "" ;
        for(int i = 0 ; i < Items.size() ; i++){
            output = output.concat(String.valueOf(Items.get(i)));
            if(i+1 != Items.size()){
                output = output.concat(",");
            }
        }
        output = output.concat(">") ;
        for(int i = 0 ; i < Transactions.size() ; i++){
            output = output.concat(String.valueOf(Transactions.get(i)));
            if(i+1 != Transactions.size()){
                output = output.concat(",");
            }
        }
        return output;
    }


}
