package zakhire;

import java.io.File;

/**
 *
 * @author user
 */
public class Configs {
    static boolean testMode = false;
    static boolean RandomInput = false;
    static int testFileWordCount = 100;
    static boolean iKnowMaxWordLengh = false;
    static int maxWordLength = 100;
    static int uniqueWordCount = 0;
    public static String ADDRESS = "C:/IRS/";
	
    static File getUniqueWordsFile()
    {
        return new File(Configs.ADDRESS + "unique.dat");
    }

    static File getUniqueCountFile()
    {
        return new File(Configs.ADDRESS + "ccc.dat");
    }

    public Configs()
    {
    }

    public static int getReadBufferSize()
    {
        return 300000;
    }

    public static int getWriteBufferSize()
    {
        return 300000;
    }

    public static int getMaxWordLenght()
    {
        if (iKnowMaxWordLengh)
            return maxWordLength;
        return 100;  // 100 is important
    }

    public static File getDataFile()
    {
        if (testMode)
            return new File(Configs.ADDRESS + "test2.dat");
        else
            return new File(Configs.ADDRESS + "Articles.dat");
    }

    public static File getStep1File()
    {
        return new File(Configs.ADDRESS + "step1.dat");
    }

    public static File getStep2File()
    {
        return new File(Configs.ADDRESS + "step2.dat");
    }

    public static File getStep3File()
    {
        return new File(Configs.ADDRESS + "step3.dat");
    }

    public static File getStep4File()
    {
        return new File(Configs.ADDRESS + "step4.dat");
    }

    public static File getOutputFile()
    {
        return new File(Configs.ADDRESS + "zout");
    }

    public static File getStopWordsFile()
    {
        return new File("stopwords.dat");
    }

    public static File getOriginalByteFile()
    {
        return new File(Configs.ADDRESS + "byteindexoriginal.dat");
    }

    public static File getProcessedByteFile()
    {
        return new File(Configs.ADDRESS + "byteindex.dat");
    }

    public static File getDraftFile()
    {
        return new File(Configs.ADDRESS + "draft.dat");
    }

    public static File getCountFile()
    {
        return new File(Configs.ADDRESS + "count.dat");
    }

    static int getMaxCountOfStopWords() {
        return 1000;
    }

    static int getMaxHeapSize()
    {
        return testMode ? 600000 : 600000; // IMPORTANT: change it carefully, it controls HeapSize, and it's found after some test.
    }

    static File getSortedFilesDirectory() {
        return new File(Configs.ADDRESS + "sortedFiles");
    }

    static File getSortedArticleDirectory()
    {
        return new File(Configs.ADDRESS + "sortedArticles");
    }

    static File getDocTermsOffsetFile()
    {
        return new File(Configs.ADDRESS + "DocTermsOffset.dat");
    }

    static  File getDocTermsFile()
    {
        return new File(Configs.ADDRESS + "DocTerms.dat");
    }

    static  File getDocTermsCountFile()
    {
        return new File(Configs.ADDRESS + "DocTerms.cnt");
    }

    static boolean removingDigitsIsValid()
    {
        return true;
    }

    static boolean removingNumbersIsValid()
    {
        return true;
    }
    
    static void setMaxWordLenght(int mx)
    {
        iKnowMaxWordLengh = true;
        maxWordLength = mx;
    }

    static int getTestFileWordCount()
    {
        return testFileWordCount;
    }

    /**
     * implemenet nashod
     */
    static boolean replacePunctiationsWithSpace()
    {
        return false;
    }

    static int getUniqueWordCount()
    {
        return uniqueWordCount;
    }

    static void setUniqueWordCount(int cnt)
    {
        uniqueWordCount = cnt;
    }

    static int getJustifyLineLength()
    {
        return 7;
    }

    static File getClusteringFile(){
    	return new File("Leaders.dat");
    }
    
    static int getPageLength() {
        return 2000; // bytes
    }
    
    static String[] getStageTexts()
    {
        String res[] = new String[13];
        res[0] = "Parsing <p> tags (original file).";
        res[1] = "Removing Punctuation Marks And Converting All Letters to Lowercase";
        res[2] = "Removing StopWords and Stemming All Words And Find maxWordLenth";
        res[3] = "Removing < and > Devil Characters";
        res[4] = "Parsing @ tags (processed file)";
        res[5] = "Splitting DataFile to Some Sorted Files with Heap";
        res[6] = "Merging Files To One Unique And Justifed File";
        res[7] = "Reading Unique Words And Unique Counts";
        res[8] = "Sort, Unique, Count Words in Each Article and Create Doc-Terms Index";
        res[9] = "Reading Doc-Terms Index And Create Term-Docs and Maximum Words of Each Article";
        res[10] = "Reading Term-Docs and Find the Number of Docs for each Word";
        res[11] = "Clustering Stage 1 : Choose Some Random Articles for Start Clustering";
        res[12] = "Clustering Stage 2 : Iterate " + Clustering.TRACES +" Times and Update each Cluster";
       
        return res;
    }
    
    static File getQueryDraftFile1() {
        return new File(Configs.ADDRESS + "queryDraftFile1.dat");
    }

    static File getQueryDraftFile2() {
        return new File(Configs.ADDRESS + "queryDraftFile2.dat");
    }

    static File getQueryDraftFile3() {
        return new File(Configs.ADDRESS + "queryDraftFile3.dat");
    }

    static File getQueryDraftFile4() {
        return new File(Configs.ADDRESS + "queryDraftFile4.dat");
    }

}
