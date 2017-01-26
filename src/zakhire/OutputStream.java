/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package zakhire;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class OutputStream {
    byte Buffer[];
    int Bsize;
    public RandomAccessFile raf;

    OutputStream(boolean clear,File f) throws FileNotFoundException
    {
        Buffer = new byte[Configs.getWriteBufferSize()];
        Bsize = 0;

        if (clear && f.exists())
            f.delete();

        raf = new RandomAccessFile(f, "rw");
    }
   
    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        close();
    }

    void clearBuffer()
    {
        Bsize = 0;
    }

    void flush() throws IOException
    {

        raf.write(Buffer,0,Bsize);
        clearBuffer();
    }

    public void write(byte b) throws IOException
    {
        if (Bsize == Buffer.length)
            flush();
        
        Buffer[Bsize++] = b;
    }

    public void write(byte b[]) throws IOException
    {
         for (int i=0;i<b.length;i++)
             write(b[i]);
    }

    public void write(byte b[],int justify) throws IOException
    {
         for (int i=0;i<b.length;i++)
             write(b[i]);
         for (int i=0;i<justify - b.length;i++)
             write(' ');
    }

    public void Println(byte[] b)
    {
        for (int i=0;i<b.length && b[i] != 0;i++)
            System.out.print(b[i]);
        System.out.println();
    }

    public void close() throws IOException
    {
        flush();
        raf.close();
    }

    void write(char c) throws IOException {
        write((byte)c);
    }

    /*
     * Be dard nakhord baba!
     */
    void write(long num) throws IOException
    {
        long temp = num;
        int l = 0;

        while(temp > 0)
        {
            temp /= 27;
            l++;
        }
        
        byte b[] = new byte[l];
        for (int i=l-1;i>=0;i--)
        {
            b[i] = (byte)('a' + (num % 27) - 1);
            num /= 27;
        }

        write(b);
    }

    void write(String str) throws IOException
    {
        write(str.getBytes());
    }

    void write(ByteArray byteArray,int justify) throws IOException {
        write(byteArray.b, justify);
    }

    void write(ByteArray byteArray) throws IOException {
        write(byteArray.b);
    }

    void write(int count, int just) throws IOException {
        String str = String.valueOf(count);
        while(str.length() < just)
            str += ' ';
        write(str);
    }
    
    void seek(long place) throws IOException{
    	flush();
    	raf.seek(place);
    }
}
