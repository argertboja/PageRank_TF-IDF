import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Scanner;
import java.io.FileWriter;

/**
 *
 * @author TF-IDF
 */
public class TFIDF {

    // constants    
    final String BIG_DATA_SET_ARTICLES          = "files/big_data_set/articles";
    final String BIG_DATA_SET_ID_MAPPINGS       = "files/big_data_set/id_title_mapping.txt";
    final String BIG_DATA_SET_OUT_LINKS         = "files/big_data_set/out_links.txt";
    
    final String SMALL_DATA_SET_ARTICLES        = "files/small_data_set/articles";
    final String SMALL_DATA_SET_ID_MAPPINGS     = "files/small_data_set/id_title_mapping.txt";
    final String SMALL_DATA_SET_OUT_LINKS       = "files/small_data_set/out_links.txt";

    final String SMALL_DATA_SET_PAGE_RANK       = "files/small_data_set/pre_computed_result/page_rank.txt";
    final String BIG_DATA_SET_PAGE_RANK         = "files/big_data_set/pre_computed_result/page_rank.txt";

    final String SMALL_DATA_SET_HASHMAPS        = "files/small_data_set/pre_computed_result/res";
    final String BIG_DATA_SET_HASHMAPS          = "files/big_data_set/pre_computed_result/res";

    final String SMALL_DATA_SET_COMBINED_HMAPS  = "files/small_data_set/pre_computed_result/res_combined";
    final String BIG_DATA_SET_COMBINED_HMAPS    = "files/big_data_set/pre_computed_result/res_combined";

    final int BIG_DATA_SET_SELECTED             = 2 ;
    final int SMALL_DATA_SET_SELECTED           = 1 ;


    /**
     * @param args the command line arguments
     */
    HashMap<String, HashMap<Integer, Double>> termMapper = new HashMap<>();
    int numDocs ;

    private static final double TFIDF_WEIGHT    =   0.2;
    private static final double PR_WEIGHT       =   0.8;

    public void parseDocuments(String docLocation, String idMapping) throws FileNotFoundException, IOException{
        System.out.println("Starting parsing");
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(idMapping));
            numDocs = 0;
            int ID = -1;
            String fileToRead;
            String line = reader.readLine();
		    int nex = 0;
            Lemmatizer lema = new Lemmatizer();
            while (line != null) {
                ID = Integer.parseInt(line.substring(0,line.indexOf(";")));
                fileToRead = line.substring(line.indexOf(";") + 1 );
                line = reader.readLine();
                File f = new File(docLocation+File.separator+fileToRead+".f");

            
                if(!f.exists()){
                    continue;
                }
               Reader reader1 = new FileReader( f );

                SimpleTokenizer tok = new SimpleTokenizer(reader);
                
                while ( tok.hasMoreTokens() ) {
                    String token = tok.nextToken();

                    if(token == null){
                        break;
                    }

                    token = lema.returnRootWords(token);

                    if ( token == null){
                        continue;
                    }

                    HashMap<Integer, Double> temp = termMapper.get(token);
                    
                    if(temp != null){
                        if(temp.get(ID) != null){
                            temp.put(ID, temp.get(ID) + 1);
                            termMapper.put(token, temp);
                        }else{
                            temp.put(ID,  1.0);
                            termMapper.put(token, temp);
                        }
                    }else{
                        temp =  new HashMap<>();
                        temp.put(ID, 1.0);
                        termMapper.put(token,temp);
                    }
                }
                numDocs++;
            }
            reader.close();
            System.out.println("Parsing ended");
	        System.out.println("Skipped docs:" + nex);	
            System.out.println("Parsed docs:"+numDocs);
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
    public void calcTF_IDF() throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("Starting TF-IDF calculation.");
        Set<String> keys = termMapper.keySet();
        double max = -1;
        double min = numDocs;

        for(String tmp : keys){
            HashMap<Integer, Double> a = termMapper.get(tmp);
            Set<Integer> ints = a.keySet();
            for(Integer i : ints){
                double tf =  a.get(i);
                double idf = Math.log10(numDocs/ints.size());
                double tf_idf = tf*idf;
                a.put(i, tf_idf);
                if( max < tf_idf){
                    max = tf_idf;
                }
                if (min > tf_idf){
                    min = tf_idf;
                }
            }
            for(Integer i : ints){
                double tf_idf =  a.get(i);             
                tf_idf = (tf_idf-min)/(max-min);
                a.put(i, tf_idf);
            }
        }
        System.out.println("Ending TF-IDF calculation.");
    }

    public void savePageRank(double[] rank, String filename) throws IOException {
        System.out.println("Storing PageRank results.");
        ArrayList<Integer> a =  new ArrayList<Integer>();
        ArrayList<Double>  b =  new ArrayList<Double>();
        for(int i = 0; i < rank.length; i++){
            a.add(i+1);
            b.add(rank[i]);
        }
        ArrayList<ArrayList<String>> print = returnSortedList(a,b);
        FileWriter f = new FileWriter(filename);

        for (int i = 0; i < print.size(); i++){
            f.write(print.get(i).get(0) + "\t" + print.get(i).get(1) + "\n" );
        }
        System.out.println("PageRank results stored.");
        f.close();
    }
    
   //return sorted array
    public ArrayList<ArrayList<String>> returnSortedList(ArrayList<Integer> a, ArrayList<Double> b){
        ArrayList<ArrayList<String>> temp = new ArrayList<>();
        ArrayList<Integer> keys  = a;
        ArrayList<Double> values = b;

        double max = values.get(0);
        int id = keys.get(0);
        int del = -1;
        int size = keys.size();

        for (int j = 0; j < size; j++){
            for (int i = 0; i < keys.size(); i++){
                if (values.get(i) > max) {
                    max = values.get(i);
                    id  = keys.get(i);
                    del = i;
                }
            }
            ArrayList<String> in = new ArrayList<>();
            in.add(id+"");
            in.add(max+"");
            temp.add(in);
            if(del != -1){
                keys.remove(del);
                values.remove(del);
                keys.trimToSize();
                values.trimToSize();
            }else{
                System.out.println("ERROR");
            }
            max = -1;
        }
        return temp;
    }

    public void search(String key, String location) throws FileNotFoundException, IOException, ClassNotFoundException{
        File hmap = new File( location +File.separator + key + ".hmap");
        if(!hmap.exists())
            return;
        FileInputStream fi = new FileInputStream(hmap);
        ObjectInputStream oi = new ObjectInputStream(fi);

        // Read objects
        HashMap<Integer, Double> pr1 = (HashMap<Integer, Double>) oi.readObject();
        
        Set<Integer> ints = pr1.keySet();
        ArrayList<Integer> keys = new ArrayList<>();
        ArrayList<Double> values = new ArrayList<>();
        for(Integer i : ints){    
            keys.add(i);
            values.add(pr1.get(i));
        }

        ArrayList<ArrayList<String>> print = returnSortedList(keys, values);
        int iterations = 10;
        if (print.size() < 10) {
            iterations = print.size();
        }
        for (int i = 0; i < iterations; i++){
             System.out.println(print.get(i).get(0) + "\t" + print.get(i).get(1));
        }

        oi.close();
        fi.close();
    }

    public void calcScore(double[] pr) {
        System.out.println("Calculating TF-IDF + PageRank.");
        Set<String> keys = termMapper.keySet();

        for(String tmp : keys){
            HashMap<Integer, Double> a = termMapper.get(tmp);
            Set<Integer> ints = a.keySet();
            for(Integer i : ints){
                double tf_idf =  a.get(i);
                tf_idf = (TFIDF_WEIGHT*tf_idf)+(PR_WEIGHT*pr[i-1]);
                a.put(i, tf_idf);
            }
        }
        System.out.println("TF-IDF + PageRank calculated.");
    }
    
    public void saveTF_IDF(String location){
        Set<String> keys = termMapper.keySet();
        try {
            File set = new File(location+File.separator + "my.set");
            if(!set.exists()){
                  set.createNewFile();
            }
            FileOutputStream f2 = new FileOutputStream(set);
            ObjectOutputStream o2 = new ObjectOutputStream(f2);

            // Write objects to file
            o2.writeObject(termMapper);
            o2.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error initializing stream");
        }

        for(String tmp : keys){
            HashMap<Integer, Double> a = termMapper.get(tmp);
            try {
                if(tmp.length() > 50 ){
                    continue;
                }
                File f1 = new File(location +File.separator + tmp + ".hmap");
                if( !f1.exists() ){
                    f1.createNewFile();
                }
                FileOutputStream f = new FileOutputStream(f1);
                ObjectOutputStream o = new ObjectOutputStream(f);

                // Write objects to file
                o.writeObject(a);
                o.close();

            } catch (FileNotFoundException e) {
                    System.out.println("File not found");
            }catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error initializing stream");
            }
        }
    }
    
    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException {
        TFIDF a = new TFIDF();
        a.run();
    }
    
    public void run () throws IOException, FileNotFoundException, ClassNotFoundException {
        String articlesFolder = "", pageRankFile = "", hashMaps = "", combinedHMaps = "", outlinks= "", idMap= "", keyword= "";

        Scanner scan = new Scanner(System.in);
        int selection;
        do {
            System.out.println("Select option:\n1. Run the pipeline\n2. Use precomputed results\n3.EXIT");
            selection = scan.nextInt();
            if (selection == 3){
                System.exit(0);
            }
            else if (selection > 3){
                continue;
            }else{
                break;
            }
        }while(true);

        do{
            System.out.println("Select Dataset:\n1.Small Dataset(17k docs)\n2.Large Dataset (70k docs)\n3.EXIT");

            int dataset = scan.nextInt();
            boolean rightChoice = false;

            switch(dataset){
                case SMALL_DATA_SET_SELECTED:
                    articlesFolder  = SMALL_DATA_SET_ARTICLES;
                    pageRankFile    = SMALL_DATA_SET_PAGE_RANK;
                    hashMaps        = SMALL_DATA_SET_HASHMAPS;
                    combinedHMaps   = SMALL_DATA_SET_COMBINED_HMAPS;
                    outlinks        = SMALL_DATA_SET_OUT_LINKS;
                    idMap           = SMALL_DATA_SET_ID_MAPPINGS;
                    rightChoice     = true;                    
                    break;

                case BIG_DATA_SET_SELECTED:
                    articlesFolder  = BIG_DATA_SET_ARTICLES;
                    pageRankFile    = BIG_DATA_SET_PAGE_RANK;
                    hashMaps        = BIG_DATA_SET_HASHMAPS;
                    combinedHMaps   = BIG_DATA_SET_COMBINED_HMAPS;
                    outlinks        = BIG_DATA_SET_OUT_LINKS;
                    idMap           = BIG_DATA_SET_ID_MAPPINGS;
                    rightChoice = true;
                    break;

                case 3:
                    System.out.println("BYE");
                     articlesFolder  = "";
                    System.exit(0);
                    break;

                default:
                    System.out.println("WRONG CHOICE, TRY AGAIN");
                    break;                
            }
            if(rightChoice)
                break;
        }while(true);

        // in case the user chose to run the algorithms, calculate all of 
        if ( selection == 1) {
            parseDocuments(articlesFolder, idMap);

            calcTF_IDF();
            saveTF_IDF(hashMaps);
            PageRank rank = new PageRank(outlinks);

            double[] pr_result = rank.computePagerank();
            calcScore(pr_result);

            saveTF_IDF(combinedHMaps);
        } 

        do{

            System.out.println("1.Show PageRank TOP 10 RESULTS");
            System.out.println("2.Show TF-IDF for keyword TOP 10 RESULTS");
            System.out.println("3.Show combined search keyword TOP 10 RESULTS");
            System.out.println("4.EXIT");
            int choice   = scan.nextInt();

            if ( choice == 4 ){
                System.out.println("BYE");
                System.exit(0);
            }

            switch (choice) {
                case 1 : 
                    BufferedReader br = new BufferedReader(new FileReader(pageRankFile));
                    String currentLine;
                    int count = 0;
                    while ((currentLine = br.readLine()) != null && count < 10)  {
                        System.out.println(currentLine);
                        count++;
                    }
                    break;

                case 2 :
                    System.out.println("Enter search keyword:");
                    keyword  = scan.next();
                    search(keyword, hashMaps);
                    break;

                case 3 :
                    System.out.println("Enter search keyword:");
                    keyword  = scan.next();
                    search(keyword, combinedHMaps);
                    break;

                default:
                    System.out.println("WRONG CHOICE, TRY AGAIN");
                    break;
                
            }
        } while(true);
    }
    
}
