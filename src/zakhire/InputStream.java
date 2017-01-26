package zakhire;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class InputStream
{
    byte Buffer[];
    int Bsize;
    RandomAccessFile raf;
    int current;
    boolean EOF;

    InputStream(File f) throws FileNotFoundException
    {
        current = 0;
        Bsize = 0;
        EOF = false;
        raf = new RandomAccessFile(f, "r");
        int BufferSize = Configs.getReadBufferSize();
        Buffer = new byte[BufferSize];
    }

    public boolean hasNext()
    {
        return !EOF;
    }

    public void readABuffer() throws IOException
    {
        Bsize = raf.read(Buffer);
        current = 0;
        if (Bsize == -1)
            EOF = true;
    }

    public byte nextByte() throws IOException
    {
        if (current >= Bsize)
            readABuffer();
        if (EOF)
            return 0;
        current++;
        return Buffer[current-1];
    }

    public boolean isAlphaNumeric(byte c)
    {
        return c > ' ';//(c <= 'Z' && c >= 'A') || (c <= 'z' && c >= 'a') || (c <= '9' &&  c>= '0');
    }

    public void ignoreSpaces() throws IOException
    {
        byte b;
        while(true)
        {
            b = nextByte();

            if (EOF)
                break;
            if (!isAlphaNumeric(b))
                continue;

            current--;
            break;
        }
    }

    public byte[] nextWord() throws IOException
    {
        byte temp[] = new byte[Configs.getMaxWordLenght() + 3];

        int s = 0;
        byte b;
        
        ignoreSpaces();

        while(true)
        {
            b = nextByte();
            if (EOF || !isAlphaNumeric(b))
                break;
            temp[s] = b;
            s++;
        }
        byte res[] = new byte[s];
        for (int i=0;i<s;i++)
            res[i] = temp[i];
        return res;
    }

    public char[] nextWordChar() throws IOException
    {
        char temp[] = new char[Configs.getMaxWordLenght()];

        int s = 0;
        byte b;

        ignoreSpaces();

        while(true)
        {
            b = nextByte();
            if (EOF || !isAlphaNumeric(b))
                break;
            temp[s] = (char)b;
            s++;
        }
        char res[] = new char[s];
        for (int i=0;i<s;i++)
            res[i] = temp[i];
        
        return res;
    }

    void close() throws IOException
    {
        raf.close();
    }

    public int nextInt() throws IOException
    {
        int res = 0;
        byte b[] = nextWord();
        for (int i=0;i<b.length;i++)
            res = res * 10 + (b[i] - '0');
        return res;
    }
    
    void seek(long place) throws IOException{
    	current = Bsize = 0;
    	EOF = false;
    	raf.seek(place);
    }
}
