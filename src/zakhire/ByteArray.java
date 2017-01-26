package zakhire;

/**
 *
 * @author user
 */
public class ByteArray implements Comparable<ByteArray>{
    byte[] b;
    int length;

    public ByteArray(byte b[]) {
        this.b = b;
        length = b.length;
    }

    public int compareTo(ByteArray o) {
        int l = Math.min(length, o.length);

        for (int i=0;i<l;i++)
        {
            if (b[i] < o.b[i])
                return -1;
            else if (b[i] > o.b[i])
                return 1;
        }

        if (length == o.length)
            return 0;
        else if(length < o.length)
            return -1;
        else
            return 1;
    }

    @Override
    public String toString() {
        String res = "";
        for (int i=0;i<b.length;i++)
            res = res + (char)b[i];
        return res;
    }

}
