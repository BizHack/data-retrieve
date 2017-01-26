/****************************************************
 *
 *  # Informtion Retreival Systems Project
 *      - Author: Manoochehr Assa
 *      
 ****************************************************
 */

package zakhire;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
	public static zPreprocessingFrame zppf;
    public static zQueryFrame zqf;
    public static zResultFrame zrf;
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
		zqf = new zQueryFrame();
		zppf = new zPreprocessingFrame(Configs.getStageTexts());
		zppf.setVisible(true);
		
        System.out.println("Start of Initialization: ");

        System.out.print("Parsing <p> tags (original file): ");
        zppf.started(0);
        DocTermsAndTermsDoc.parsePTags(Configs.getDataFile(), Configs.getOriginalByteFile());
        zppf.done(0);
        System.out.println("Done");
        System.gc();

        System.out.print("Removing Punctuation Marks And Converting All Letters to Lowercase: ");
        zppf.started(1);
        DocTermsAndTermsDoc.removePunctuationMarksAndAllToLowerCase(Configs.getDataFile(), Configs.getStep1File());
        zppf.done(1);
        System.out.println("Done");
        System.gc();

        System.out.print("Removing StopWords and Stemming All Words And Find maxWordLenth: ");
        zppf.started(2);
        DocTermsAndTermsDoc.removeStopWordsAndStem(Configs.getStep1File(), Configs.getStep2File());
        Configs.getStep1File().delete();
        zppf.done(2);
        System.out.println("Done");
        System.gc();

        System.out.print("Removing < and > Devil Characters: ");
        zppf.started(3);
        DocTermsAndTermsDoc.removeSpecialCharacters(Configs.getStep2File(), Configs.getStep3File());
        Configs.getStep2File().delete();
        zppf.done(3);
        System.out.println("Done");
        System.gc();

        System.out.print("Parsing @ tags (processed file): ");
        zppf.started(4);
        DocTermsAndTermsDoc.parseAtTags(Configs.getStep3File(), Configs.getProcessedByteFile());
        zppf.done(4);
        System.out.println("Done");
        System.gc();

        System.out.print("Splitting DataFile to Some Sorted Files with Heap: ");
        zppf.started(5);
        DocTermsAndTermsDoc.splitWithHeap(Configs.getStep3File());
        zppf.done(5);
        System.out.println("Done");
        System.gc();

        System.out.print("Merging Files To One Unique And Justifed File: ");
        zppf.started(6);
        DocTermsAndTermsDoc.mergeAndUniqueAndJustify(Configs.getStep4File(),Configs.getCountFile(),false);
        zppf.done(6);
        System.out.println("Done");
        System.gc();
        
        //DocTermsAndTermsDoc.ArticleCounter = 41983;
        //Configs.setUniqueWordCount(1031503);
        //Configs.setMaxWordLenght(72);
        
        System.out.print("Reading Unique Words And Unique Counts: ");
        zppf.started(7);
        DocTermsAndTermsDoc.readUniqueWordsFile();
        zppf.done(7);
        System.out.println("Done");
        System.gc();

        System.out.print("Sort, Unique, Count Words in Each Article and Create Doc-Terms Index");
        zppf.started(8);
        DocTermsAndTermsDoc.sortArticleWords(Configs.getStep3File());
        Configs.getStep3File().delete();
        zppf.done(8);
        System.out.println("Done");
        System.gc();

        DocTermsAndTermsDoc.map.clear(); // Sarfe jooyee dar Heap!

        System.out.print("Reading Doc-Terms Index And Create Term-Docs");
        zppf.started(9);
        DocTermsAndTermsDoc.ReadOffsetFile();
        zppf.done(9);
        System.out.println("Done");
        System.gc();

        System.out.print("Reading Term-Docs and Find the Number of Docs for each Word");
        zppf.started(10);
        DocTermsAndTermsDoc.FindNmaxAndCountArticlesContainEachTerm();
        zppf.done(10);
        System.out.println("Done");
        System.gc();

        zppf.started(11);
        DocTermsAndTermsDoc.BucketSize = (int)Math.pow(DocTermsAndTermsDoc.ArticleCounter, 0.50);
        Clustering cs = new Clustering(DocTermsAndTermsDoc.ArticleCounter - 1, DocTermsAndTermsDoc.BucketSize, Configs.getUniqueWordCount());
		cs.doClustering();
		cs.saveResults();
		zppf.done(12);
		
		zrf = new zResultFrame();
		zResultFrame.eq = new ExecuteQuery(100);
		
		System.gc();
		
    }
}
