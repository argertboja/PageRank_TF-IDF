import java.util.*;
import java.io.*;

public class PageRank{


	private final static double CONST_NUM_FILES = 397466 ; 
	private final static double BETA = 0.85;
    private final static double EPSILON = 0.0001;
    private final static int 	MAX_ITER = 1000;
	private String filename;
 	private double numOfFiles;

    public PageRank( String f , double n) throws IOException {
	filename   = f;
	numOfFiles = CONST_NUM_FILES;
    }
	public PageRank( String filename ) throws IOException { this( filename , CONST_NUM_FILES ); }

    double[] computePagerank() throws IOException {
    	double[] rold = new double[(int) numOfFiles];
    	double[] rnew = new double[(int) numOfFiles];
    	for(int i = 0; i < numOfFiles; i++) {
    		rold[i] = 1/numOfFiles;
    		rnew[i] = 0;
    	}
    	int numIter = 0;
    	double distance;
    	
    	do {
    		distance = 0;
    		BufferedReader br = new BufferedReader(new FileReader(filename));
	    	String currentLine;
	    	while ((currentLine = br.readLine()) != null) {
	    		int lastChar = currentLine.indexOf( ";" );
	    		int pageIndex = Integer.parseInt(currentLine.substring( 0, lastChar ));
	    		StringTokenizer tok = new StringTokenizer( currentLine.substring(lastChar+1), "," );
	    		LinkedList<Integer> ll = new LinkedList<>();
	    		int size = 0;
	    		while ( tok.hasMoreTokens() ) {
	    			String s = tok.nextToken();
	    			ll.add(Integer.parseInt(s));
	    			size++;
	    		}
	    		for(Integer i:ll)
	    			rnew[i-1] += BETA*rold[pageIndex-1]/size;
	        }
	    	
	    	double S = 0;
	    	for(int i = 0; i < numOfFiles; i++)
	    		S += rnew[i];
	    	
	    	double toAdd = (1-S)/numOfFiles;
	    	for(int i = 0; i < numOfFiles; i++) {
	    		rnew[i] += toAdd;
	    		distance += Math.abs(rnew[i]-rold[i]);
	    		rold[i] = rnew[i];
	    		rnew[i] = 0;
	    	}
	    	numIter++;
	    	br.close();
    	} while( distance > EPSILON && numIter < MAX_ITER);
    	return rold;
    }
    
    private void printPr(double[] pr) {
		for(int i = 0; i < pr.length; i++)
			System.out.println(String.format("%d\t%f",i+1,pr[i]));
	}

}
