package zakhire;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

public class Draft {

    public Draft()
    {

    }

    void Go() throws FileNotFoundException, IOException
    {
        OutputStream out = new OutputStream(true, new File("/home/user/test.dat"));
        Random r = new Random();
        int n = Configs.getTestFileWordCount();
        
        while(n > 0)
        {
            n--;
            int len = 2 + Math.abs(r.nextInt()%1);

            for (int i=0;i<len;i++)
            {
                out.write((byte)('a' + Math.abs(r.nextInt()%26)));
            }

            out.write(' ');
        }        
        out.close();
    }
}
