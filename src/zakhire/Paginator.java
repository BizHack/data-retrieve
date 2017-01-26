/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package zakhire;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author user
 */
public final class Paginator {

    public int pageLength;
    public InputStream in;
    public int articleOffsets[];
    public Paginator() throws FileNotFoundException, IOException {
        pageLength = Configs.getPageLength();
        in = new InputStream(Configs.getDataFile());
        loadArticleOffsets();
    }

    public void loadArticleOffsets() throws FileNotFoundException, IOException
    {
        File offsetFile = Configs.getOriginalByteFile();
        articleOffsets = new int[DocTermsAndTermsDoc.ArticleCounter + 10];
        Scanner sc = new Scanner(offsetFile);
        int c = 0;
        while(sc.hasNext())
        {
            articleOffsets[c] = sc.nextInt();
            c++;
        }

        //System.out.println(c);

        sc.close();
    }

    /**
     * both is 1 Based!
     */
    String GetPage(int articleNumber,int pageNumber) throws IOException
    {
        articleNumber--;
        if (articleNumber < 0 || articleNumber >= DocTermsAndTermsDoc.ArticleCounter)
            return "<1. Page Not Found!>";

        int pageCount = countPages(articleNumber+1);
        if (pageNumber <= 0 || pageNumber > pageCount)
            return "<2. Page Not Found!>";

        int articleStart = articleOffsets[articleNumber];
        int articleLength = articleOffsets[articleNumber+1] - articleOffsets[articleNumber] - 3;
        int startByte = articleStart + (pageNumber-1)*pageLength;
        int endByte = startByte + pageLength;

        if (endByte > articleStart + articleLength)
            endByte = articleStart + articleLength;
        //System.out.println(startByte + " " + endByte);
        in.raf.seek(startByte);
        byte b[] = new byte[pageLength+1];
        //System.out.println(startByte + " " + (endByte-startByte) + " " + b.length);
        int res = in.raf.read(b, 0, endByte-startByte);

        if (res == -1)
            return "<3. Page Not Found!>";
        
        return new String(b,0,res);
    }

    boolean hasNextPage(int articleNumber,int currentPage)
    {

        return currentPage < countPages(articleNumber);
    }

    boolean hasPrevPage(int articleNumber, int currentPage)
    {
        return currentPage >= 2;
    }

    int countPages(int articleNumber)
    {
        articleNumber--;
        int articleStart = articleOffsets[articleNumber];
        int articleLength = articleOffsets[articleNumber+1] - articleStart - 3;
        int articlePages = (articleLength + pageLength - 1) / pageLength;
        return articlePages;
    }
}
