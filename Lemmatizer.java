

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import java.util.Collections;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class Lemmatizer {

	final String LEMMATIZE_DICTIONARY		= "files/lemmatizing_data/en.txt";
	final String LEMMATIZE_MODEL 			= "files/lemmatizing_data/en-pos-maxent.bin";

	InputStream dictLemmatizer;
	DictionaryLemmatizer lemmatizer;
	InputStream posModelIn ;
	POSModel posModel;
	POSTaggerME posTagger;

	public Lemmatizer() throws IOException{
		// loading the dictionary to input stream
	   dictLemmatizer = new FileInputStream(LEMMATIZE_DICTIONARY);
		// loading the lemmatizer with dictionary
		 lemmatizer = new DictionaryLemmatizer(dictLemmatizer);
		// reading parts-of-speech model to a stream
		 posModelIn = new FileInputStream(LEMMATIZE_MODEL);
		// loading the parts-of-speech model from stream
		  posModel = new POSModel(posModelIn);
		// initializing the parts-of-speech tagger with model
		 posTagger = new POSTaggerME(posModel);
	}	


   // returns the lemma or list of lemmas for the given keyword/s
    public String returnRootWords(String keyword)  throws IOException {
      	String[] tokens = new String[1];
        tokens[0] = keyword;

        String tags[] = posTagger.tag(tokens);
        // finding the lemmas
        String[] lemmas = lemmatizer.lemmatize(tokens, tags);
        
        if(lemmas[0] != null){
            if(tags[0].equals("DT")){ // skip determiners
                return null;
            }else if(lemmas[0].equals("O") ){ // if keyword is special(not in dictionary) add the  keyword itself
          		return tokens[0];
            }else{ // add the generated lemma to the list
               return lemmas[0]; 
            }
        }else{ // no lemmas returned
            return null;
        }
    }
}