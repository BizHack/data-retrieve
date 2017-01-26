package zakhire;

/**
 *
 * @author user
 */
public class Pair implements Comparable<Pair> {

    ByteArray ba;
    int idx;

    public Pair(ByteArray ba, int idx) {
        this.ba = ba;
        this.idx = idx;
    }

    public int compareTo(Pair o) {
        return ba.compareTo(o.ba);
    }
}
