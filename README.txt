How to run the program: (LINUX)

1.Open shell and run the following line to compile:

javac -cp "opennlp-tools-1.8.4.jar:." TFIDF.java PageRank.java Lemmatizer.java SimpleTokenizer.java


	
2.After compilation is done, run the following line to open the program:

java -cp "opennlp-tools-1.8.4.jar:." TFIDF


3.The program works based on user input. "Running the pipeline" would require the whole parsing to be done from the beginning. That would take approx 5 minutes for the small dataset and around 1 hour for the big dataset. For testing purposes, choose to go with precomputed results that we have computed ourselves.




Code Implementation and Authenticity:
All of the code is authentic and written by us, TF-IDF implementation, PageRank implementation and their combination. 
However, we made use of  a library for lemmatization of words (opennlp-tools-1.8.4.jar) which we changed to our needs in the class Lemmatizer.java. Also, for an efficient stop words removal we used a class from github, which corresponds to the class SimpleTokenizer.java


