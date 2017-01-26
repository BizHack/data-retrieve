package zakhire;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.Vector;

/**
 *
 * @author mohammadrdeh
 * 
 */
@SuppressWarnings("unchecked")
public class DocTermsAndTermsDoc
{
    static PriorityQueue heaps[] = new PriorityQueue[2];
    static int createdFileCounter;
    static HashMap<String, Integer> map = new HashMap();
    static int TermDocCnt[]; // felan word too chand ta maghale oomade?
    static int nmax[];
    static int ArticleCounter = 0;
    static int BucketSize = 0;
    static int lineCounter = 0;
    static int[] articleOffset;
    static String query;
    static int queryVectorDat[];
    static int queryVectorCnt[];
    static Vector< Vector<String> > searchResults;

    
    private static char invertCase(char c)
    {
        if (c <= 'z' && c >= 'a')
            return (char) (c - 'a' + 'A');
        else if (c <= 'Z' && c >= 'A')
            return (char) (c - 'A' + 'a');
        return c;
    }

    public static byte toLower(byte c)
    {
        if (c <= 'Z' && c >= 'A')
            return (byte) (c - (byte) 'A' + (byte) 'a');
        else
            return c;
    }

    public static void removePunctuationMarksAndAllToLowerCase(File input,File output) throws FileNotFoundException, IOException
    {
        InputStream in = new InputStream(input);
        OutputStream out = new OutputStream(true, output);
        byte b;
        boolean removingDigitsIsValid = Configs.removingDigitsIsValid();
        while(in.hasNext())
        {
            b = in.nextByte();
            if (!(b != ')' && b != '(' && b != ':' && b != '_' && b != '{' && b != '}' && b != '`' && b != '=' && b != '+' && b != '&' && b != '|' && b != '@' && b != '#' && b != '$' && b != '%' && b != '\"' && b != '\'' && b != '~' && b != '-' && b != '^' && b != '.' && b != ',' && b != '?' && b != '!' && b != '/' && b != '[' && b != ']' && b != ';' && b != '*' && b != '\\' ))
                continue;
            if (removingDigitsIsValid && b <= '9' && b >= '0')
                continue;

            out.write(toLower(b));
        }
        out.close();
        in.close();
    }

    public static long getHash(String str)
    {
        long res = 0;
        long base = ((long)1<<35)-1;
        //System.out.println(base);
        for (int i=0;i<10;i++)
        {
            if (i < str.length())
                res = (res * 291711 + (str.charAt(i))) & base ;
            else
                res = (res * 291711) & base;
        }
        return res;
    }

    public static long getHash(byte[] str)
    {
        long res = 0;
        long base = ((long)1<<35)-1;
        //System.out.println(base);
        for (int i=0;i<10;i++)
        {
            if (i < str.length)
                res = (res * 291711 + (str[i])) & base ;
            else
                res = (res * 291711) & base;
        }
        return res;
    }

   public static void removeStopWordsAndStem(File input, File output) throws FileNotFoundException, IOException
    {
        InputStream in = new InputStream(input);
        OutputStream out = new OutputStream(true, output);
        Scanner sc = new Scanner(Configs.getStopWordsFile());

        String list[] = new String[2*Configs.getMaxCountOfStopWords()];
        int stopWordsCounter = 0;
        String word;
        while(sc.hasNext())
        {
            word = sc.next();
            list[stopWordsCounter++] = word;
            list[stopWordsCounter++] = invertCase(word.charAt(0)) + word.substring(1);
        }

        TreeSet set = new TreeSet();
        for (int i=0;i<stopWordsCounter;i++)
            set.add(getHash(list[i]));
        boolean rd = Configs.removingDigitsIsValid();
        boolean rn = Configs.removingNumbersIsValid();

        boolean isNumeric;
        long hash,tagHash = getHash("<p>");
        byte currentWord[],temp[];
        int maxWordLength = 0;

        while(in.hasNext())
        {
            currentWord = in.nextWord();
            if (rn && !rd)
            {
                isNumeric = true;
                for (int i=0;i<currentWord.length && isNumeric;i++)
                    if (!(currentWord[i] <= '9' && currentWord[i] >= '0'))
                        isNumeric = false;
                if (isNumeric)
                    continue;
            }
            hash = getHash(currentWord);
            if (hash == tagHash)
            {
                out.write('@');
                out.write(' ');
                continue;
            }
            boolean eq = set.contains(hash);
            if (!eq)
            {
                temp = stem(currentWord);
                if (temp.length > maxWordLength)
                    maxWordLength = temp.length;

                out.write(temp);
                out.write((byte)(32));
            }
        }
        if (!Configs.iKnowMaxWordLengh)//important :-s
        	Configs.setMaxWordLenght(maxWordLength);
        out.close(); // important
        in.close();
    }

    /**
     * use nashod!
     */
    public static byte[] toByteArray(int number)
    {
        if (number == 0)
        {
            byte res[] = new byte[1];
            res[0] = '0';
            return res;
        }
        else
        {
            int len = 0,temp = number;
            while(number > 0)
            {
                number/=10;
                len++;
            }
            byte res[] = new byte[len];
            while(temp > 0)
            {
                res[len-1] = (byte)('0' + temp % 10);
                len--;
                temp/=10;
            }
            return res;
        }
    }

    public static void parsePTags(File input,File output) throws FileNotFoundException, IOException
    {
        InputStream in = new InputStream(input);
        OutputStream out = new OutputStream(true, output);

        byte buffer[] = new byte[10];
        int articleCounter=0,c = 0;
        int byteCounter = 0;
        while(in.hasNext())
        {
            buffer[c] = in.nextByte();
            if (buffer[c] == '>' && (buffer[(c+9)%10] == 'p' || buffer[(c+9)%10] == 'P') && buffer[(c+8)%10] == '<')
            {
                articleCounter++;
                out.write(toByteArray(byteCounter + 1));
                out.write('\n');
            }
            byteCounter++;
            c = (c + 1) % 10;
        }
        in.close();
        out.close(); // important
    }

    public static void parseAtTags(File input,File output) throws FileNotFoundException, IOException
    {
        InputStream in = new InputStream(input);
        OutputStream out = new OutputStream(true, output);

        int articleCounter=0,c = 0;
        int byteCounter = 0;
        byte b;
        while(in.hasNext())
        {
            b = in.nextByte();
            if (b == '@')
            {
                articleCounter++;
                out.write(toByteArray(byteCounter));
                out.write('\n');
            }
            byteCounter++;
            c = (c + 1) % 10;
        }
        in.close();
        out.close(); // important
    }


    public static byte[] stem(byte[] b) throws FileNotFoundException, IOException
    {
        Stemmer stemmer = new Stemmer();
        
        stemmer.add(b,b.length);
        stemmer.stem();
        char temp[] = stemmer.getResultBuffer();
        int length = stemmer.getResultLength();
        
        byte res[] = new byte[length];
        for (int i=0;i<length;i++)
            res[i] = (byte)temp[i];
        return res;
    }

   static void splitWithHeap(File input) throws FileNotFoundException, IOException {
        InputStream in = new InputStream(input);
        File f = Configs.getSortedFilesDirectory();
        f.mkdir();
        heaps[0] = new PriorityQueue();

        int maxHeapSize = Configs.getMaxHeapSize();
        createdFileCounter = 0;
        byte b[];
        while(in.hasNext())
        {
            if (heaps[0].size() >= maxHeapSize)
            {
                writeHeapAndClear(0,createdFileCounter);
                createdFileCounter++;
            }
            b = in.nextWord();
            if (b.length == 0)
                continue;
            if (b[0] == '@') // ignoring <p> tags
                continue;
            heaps[0].add(new ByteArray(b));
        }

        writeHeapAndClear(0,createdFileCounter);   // important
        createdFileCounter++; // !!
        
        in.close();
        return;
    }

    private static void writeHeapAndClear(int idx,int fileNumber) throws FileNotFoundException, IOException
    {
    	File fileName =  new File(Configs.getSortedFilesDirectory() + "/" + String.valueOf(fileNumber) + ".dat");
        OutputStream out = new OutputStream(true,fileName);

        while(heaps[idx].size() > 0)
        {
            out.write((ByteArray)heaps[idx].poll());
            out.write(' ');
        }
        out.close();
    }

    static void mergeAndUniqueAndJustify(File output,File countFile,boolean ReplaceIndex) throws FileNotFoundException, IOException
    {
        //System.out.println(output + " " + createdFileCounter);

        OutputStream out = new OutputStream(true, output);
        OutputStream countOut = new OutputStream(true, countFile);
        InputStream in[] = new InputStream[createdFileCounter];
        int Total = 0;
        for (int i=0;i<createdFileCounter;i++)
        {
            File fileName = new File(Configs.getSortedFilesDirectory() + "/" + i + ".dat");
            in[i] = new InputStream(fileName);
        }

        PriorityQueue pr = new PriorityQueue();

        for (int i=0;i<createdFileCounter;i++)
        {
            if (in[i].hasNext())
                pr.add(new Pair(new ByteArray(in[i].nextWord()), i));
        }
        //System.out.println(pr.size());
        Pair top;
        byte b[],t[] = new byte[1];
        t[0] = '\0';
        ByteArray last = new ByteArray(t);
        int maxWordLength = Configs.getMaxWordLenght();
        int count = 0;
        boolean first = true;
        boolean hasLast = false;
        while(pr.size() > 0)
        {
            top = (Pair)pr.poll();
            //System.out.println(top.ba + " " + top.idx);
            
            if (top.ba.compareTo(last) != 0)
            {
                last = top.ba;
                if (!first)
                {
                    countOut.write(count+1,Configs.getJustifyLineLength());
                    countOut.write('\n');
                }
                if (!ReplaceIndex)
                {
                    out.write(top.ba, maxWordLength);
                    out.write('\n');
                }
                else
                {
                    if (!map.containsKey(top.ba.toString()))
                        continue;
                    lineCounter++;
                    out.write(map.get(top.ba.toString()), Configs.getJustifyLineLength());
                    out.write('\n');
                }
                first = false;
                hasLast = true;
                Total++;
                count = 0;
            }
            else
            {
                count++;
            }

            if (in[top.idx].hasNext())
            {
               b = in[top.idx].nextWord();
               if (b.length == 0)   // ignore last EOF
                   continue;
               pr.add(new Pair(new ByteArray(b), top.idx));
            }
        }

        if (hasLast)
        {
            // important for last Word
            countOut.write(count+1,Configs.getJustifyLineLength());
            countOut.write('\n');
        }

        if (!ReplaceIndex)  // age sort e kalame ha bood na maghale ha
            Configs.setUniqueWordCount(Total);

        countOut.close();
        out.close();
        for (int i=0;i<createdFileCounter;i++)
            in[i].close();
    }

    static void mergeAndUniqueAndJustifyForArticles(OutputStream out,OutputStream countOut,boolean ReplaceIndex) throws FileNotFoundException, IOException
    {
        //System.out.println(output + " " + createdFileCounter);

        InputStream in[] = new InputStream[createdFileCounter];
        int Total = 0;
        for (int i=0;i<createdFileCounter;i++)
        {
            File fileName = new File(Configs.getSortedFilesDirectory() + "/" + i + ".dat");
            in[i] = new InputStream(fileName);
        }

        PriorityQueue pr = new PriorityQueue();

        for (int i=0;i<createdFileCounter;i++)
        {
            if (in[i].hasNext())
                pr.add(new Pair(new ByteArray(in[i].nextWord()), i));
        }
        //System.out.println(pr.size());
        Pair top;
        byte b[],t[] = new byte[1];
        t[0] = '\0';
        ByteArray last = new ByteArray(t);
        int maxWordLength = Configs.getMaxWordLenght();
        int count = 0;
        boolean first = true;
        boolean hasLast = false;
        while(pr.size() > 0)
        {
            top = (Pair)pr.poll();
            //System.out.println(top.ba + " " + top.idx);

            if (top.ba.compareTo(last) != 0)
            {
                last = top.ba;
                if (!first)
                {
                    countOut.write(count+1,Configs.getJustifyLineLength());
                    countOut.write('\n');
                }
                if (!ReplaceIndex)
                {
                    out.write(top.ba, maxWordLength);
                    out.write('\n');
                }
                else
                {
                    if (!map.containsKey(top.ba.toString()))
                        continue;
                    lineCounter++;
                    out.write(map.get(top.ba.toString()), Configs.getJustifyLineLength());
                    out.write('\n');
                }
                first = false;
                hasLast = true;
                Total++;
                count = 0;
            }
            else
            {
                count++;
            }

            if (in[top.idx].hasNext())
            {
               b = in[top.idx].nextWord();
               if (b.length == 0)   // ignore last EOF
                   continue;
               pr.add(new Pair(new ByteArray(b), top.idx));
            }
        }

        if (hasLast)
        {
            // important for last Word
            countOut.write(count+1,Configs.getJustifyLineLength());
            countOut.write('\n');
        }

        if (!ReplaceIndex)  // age sort e kalame ha bood na maghale ha
            Configs.setUniqueWordCount(Total);

        for (int i=0;i<createdFileCounter;i++)
            in[i].close();
    }


    static void readUniqueWordsFile() throws FileNotFoundException
    {
        Scanner sc = new Scanner(Configs.getStep4File());
        String s;
        int u = 1;
        while(sc.hasNext())
        {
            s = sc.next();
            map.put(s, u);
            u++;
        }
       // System.out.println(map.size());
    }

    static void sortArticleWords(File input) throws FileNotFoundException, IOException {
        InputStream in = new InputStream(input);
        OutputStream outoff = new OutputStream(true, Configs.getDocTermsOffsetFile());
        OutputStream outdat = new OutputStream(true, Configs.getDocTermsFile());
        OutputStream outcnt = new OutputStream(true, Configs.getDocTermsCountFile());
        int maxHeapSize = Configs.getMaxHeapSize();
        heaps[0] = new PriorityQueue();
        createdFileCounter  = 0;
        byte b[];
        while(in.hasNext())
        {
            b = in.nextWord();
            if (b == null)
                continue;
            if (b.length == 0)
                continue;
            if (b[0] == '@')
            {
                writeHeapAndClear(0, createdFileCounter);
                createdFileCounter++; // important
                mergeAndUniqueAndJustifyForArticles(outdat, outcnt,true);
                outoff.write(lineCounter+1, Configs.getJustifyLineLength());
                outoff.write('\n');
                ArticleCounter++;
                
                createdFileCounter = 0;
                continue;
            }
            if (heaps[0].size() >= maxHeapSize)
            {
                writeHeapAndClear(0, createdFileCounter);
                createdFileCounter++;
            }
            heaps[0].add(new ByteArray(b));
        }

        writeHeapAndClear(0, createdFileCounter);
        mergeAndUniqueAndJustifyForArticles(outdat, outcnt,true);
        outoff.close();
        outcnt.close();
        outdat.close();
        in.close();
    }

    static void removeSpecialCharacters(File input, File output) throws FileNotFoundException, IOException {
        InputStream in = new InputStream(input);
        OutputStream out = new OutputStream(true, output);
        byte b;
        while(in.hasNext())
        {
            b = in.nextByte();
            if (!(b != '>' && b != '<'))
                continue;

            out.write(toLower(b));
        }
        out.close();
        in.close();
    }

    static void FindNmaxAndCountArticlesContainEachTerm() throws IOException {
        TermDocCnt = new int[Configs.getUniqueWordCount() + 10];
        nmax = new int[ArticleCounter+2];

        int word,count;
        long res;
        SeekerReader sr = new SeekerReader();
        for (int i=0;i<ArticleCounter;i++)
        {
            sr.seekOnArticle(i);
            while(sr.hasNext())
            {
                res = sr.nextWord();
                word = (int) (res >> 30) - 1;
                count = (int) (res & ((1 << 30) - 1));

                nmax[i] = Math.max(nmax[i],count);
                TermDocCnt[word]++;
            }
        }
        sr.close();
        /*for (int i=0;i<TermDocCnt.length;i++)
            System.out.println(TermDocCnt[i]);
        System.out.println();
        for (int i=0;i<nmax.length;i++)
            System.out.println(nmax[i]);*/

    }

    static void ReadOffsetFile() throws FileNotFoundException {
        articleOffset = new int[ArticleCounter+3];
        Scanner sc = new Scanner(Configs.getDocTermsOffsetFile());
        int i = 0;
        while(sc.hasNext())
        {
            articleOffset[i] = sc.nextInt();
            i++;
        }
    }

    public void print(String s)
    {
        System.out.println(s);
    }

    /**
     * start of ith Word: i*L+i
     *
     */
    public static String fetchWord(RandomAccessFile raf,int idx) throws IOException
    {
        int lineLength = Configs.getMaxWordLenght();
        int fileWordCount = (int) (raf.length() / lineLength - 1);
        if (idx >= fileWordCount)
            return null;
        raf.seek(idx * lineLength + idx);
        return raf.readLine();
    }

    public static String fetchWord(File file,int line,int lineLength) throws IOException
    {
        RandomAccessFile raf = new RandomAccessFile(file,"rw");
        int fileWordCount = (int) (raf.length() / lineLength - 1);
        if (line >= fileWordCount)
            return null;
        raf.seek(line * lineLength + line);
        String res = raf.readLine();
        raf.close();
        return res;
    }

    /**
     * Get a word and an input file, search in it, return result
     *
     * Algo: Binary Search
     *
     * File Specification:
     *      1- Sorted
     *      2- Unique
     *      3- Left Justified
     *
     * Return:
     *      if found: byte offset of start of word
     *      else    : -1
     *
     *      I used simple RandomAccessFile Instead of my InputStream.
     *
     *      Test Time: searched 1000000 random words have upto 10 characters in
     */
    public static int search(RandomAccessFile raf,byte[] b) throws FileNotFoundException, IOException
    {
        int lineLength = Configs.getMaxWordLenght();
        //System.err.println(lineLength);
        int left  = 0;
        int right = (int) (raf.length() / (lineLength+1));  // 1 for \n
        int mid;
        String m,q = new String(b);

        // a a b b b c c d f f g h
       // n n n n n y y y y y y y

        while(left < right)
        {
            //System.out.println(left + " " + right);
            mid = (left + right) / 2;
            m = fetchWord(raf, mid);
            if (m.compareTo(q) < 0)
            {
                left = mid + 1;
            }
            else
            {
                right = mid;
            }
        }
        String candidate = fetchWord(raf, left);

        if (candidate == null)
            return -1;              // not found!
        String temp = "";
        for (int i=0;i<candidate.length() && candidate.charAt(i) != ' ';i++)
            temp += candidate.charAt(i);


        //System.out.println(fetchWord(raf, left));
        //System.out.println(q);
        if (temp.equals(q))
            return left;

        return -1; // Not Found
    }

    public static int search(RandomAccessFile raf,String s,int lineLength) throws FileNotFoundException, IOException
    {
        return search(raf,s.getBytes());
    }

    public static void testSearch() throws FileNotFoundException, IOException
    {
        Configs.iKnowMaxWordLengh = true;
        Configs.maxWordLength = 72;
        String str;
        Random r = new Random();
        RandomAccessFile raf = new RandomAccessFile(new File("/home/user/step4.dat"), "rw");
        int n = 100000;
        int res,ok = 0;
        while(n > 0)
        {
            n--;
            int len = 1 + Math.abs(r.nextInt()%10);
            str = "";
            for (int i=0;i<len;i++)
            {
                str += (char)('a' + Math.abs(r.nextInt()%26));
            }
           // System.out.println(str);
            res = DocTermsAndTermsDoc.search(raf, str.getBytes());
            ok += (res != -1 ? 1 : 0);
           // System.out.println(res);
        }
        System.out.println(ok);
    }
    
    static void makeQueryVector() throws FileNotFoundException, IOException
    {
        OutputStream out = new OutputStream(true, Configs.getQueryDraftFile1());
        out.write(query);
        out.close();
        
        removePunctuationMarksAndAllToLowerCase(Configs.getQueryDraftFile1(), Configs.getQueryDraftFile2());
        removeStopWordsAndStem(Configs.getQueryDraftFile2(), Configs.getQueryDraftFile3());

        InputStream in = new InputStream(Configs.getQueryDraftFile3());
        String q = "";
        byte b[];
        while(in.hasNext())
        {
            b = in.nextWord();
            q += ' ' + new String(b);
        }
        in.close();
        
        //System.out.println(q);
        String arr[] = q.split(" ");
        String unique[] = new String[arr.length];
        int cnt[] = new int[arr.length];
        int dat[] = new int[arr.length];
        int uniqueLength = 0;
        Arrays.sort(arr);
        if (arr.length > 0)
        {
            unique[0] = arr[0];
            cnt[0] = 1;
            uniqueLength++;
        }
        for (int i=1;i<arr.length;i++)
        {
            if (!arr[i].equals(arr[i - 1]))
            {
                unique[uniqueLength] = arr[i];
                cnt[uniqueLength] = 1;
                uniqueLength++;
            }
            else
            {
                cnt[uniqueLength-1]++;
            }
        }

        int pos,newWords = 0;
        RandomAccessFile raf = new RandomAccessFile(Configs.getStep4File(), "rw");
        //System.out.println(fetchWord(raf, 1));
        for (int i=0;i<uniqueLength;i++)
        {
           //System.out.print("Searching:" + unique[i] + ": ");
           pos = search(raf, unique[i].getBytes());
           //System.out.println(pos);
           if (pos == -1)
               newWords++;
           dat[i] = pos;
        }

        queryVectorCnt = new int[uniqueLength - newWords];
        queryVectorDat = new int[uniqueLength - newWords];
        int counter  = 0;
        for (int i=0;i<uniqueLength;i++)
        {
            if (dat[i] == -1)
                continue;
            queryVectorDat[counter] = dat[i] + 1;
            queryVectorCnt[counter] = cnt[i];
            counter++;

            //System.out.println(queryVectorDat[counter-1] + " " + queryVectorCnt[counter-1]);
        }
        //System.out.println(queryVectorCnt.length);
    }
}
