package zakhire;

import java.io.FileNotFoundException;
import java.io.IOException;
 
public class SeekerReader {
    InputStream datReader;
    InputStream cntReader;
    
    long counter;
    long startOffset, endOffset;

    /*
     * Important: Zero Based
     */
    public SeekerReader() throws FileNotFoundException, IOException {
        datReader = new InputStream(Configs.getDocTermsFile());
        cntReader = new InputStream(Configs.getDocTermsCountFile());
    }
    
    public void seekOnArticle(int articleNumber) throws IOException{
    	
    	int lineLength = Configs.getJustifyLineLength();
        startOffset = DocTermsAndTermsDoc.articleOffset[articleNumber] - 1;
        endOffset = DocTermsAndTermsDoc.articleOffset[articleNumber + 1];
        //System.err.println(articleNumber + " ??????? " + startOffset + " ???????? " + endOffset);
        counter = startOffset;
        datReader.seek(startOffset * lineLength + startOffset);
        cntReader.seek(startOffset * lineLength + startOffset);
        
    }
    
    public boolean hasNext()
    {
        return counter + 1 < endOffset;
    }

    /**
     * Important!:
     * To Read Result:
     *      data = res >> 30
     *      cnt  = res & ((1<<30)-1)
     *
     * - Priority of Operators Is Important!
     */

    public long nextWord() throws IOException
    {
        long data = datReader.nextInt();
        long cnt = cntReader.nextInt();
        //System.err.println(data + " !!!!!!!!!! " + cnt);
        counter++;

        return (data<<30) + cnt;
    }

    public void close() throws IOException
    {
        datReader.close();
        cntReader.close();
    }
}

