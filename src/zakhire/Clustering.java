package zakhire;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Vector;

public class Clustering {
	static int BUCKETS_SIZE;
	static int ARTICLES_SIZE;
	static int TRACES = 50;
	static int WORDS;
	
	Time tm;
	Vector<Integer> bucket;
	int cluster[], number[];
	
	static int numWords[], tempNumWords[];
	static float leader[];
	static double leaderNorm;
	
	int index[][];
	float weight[][];
	double[] articleNorm;
	
	SeekerReader sr;
	
	InputStream datReader;
    InputStream cntReader;
    
    OutputStream leadWriter, tempWriter;
    InputStream tempReader;
    static InputStream leadReader;
	
	public Clustering(int art, int buck, int words) throws IOException{
		BUCKETS_SIZE = buck;
		ARTICLES_SIZE = art;
		WORDS = words;
		doInitial();
	}

	void doInitial() throws IOException{
		
		bucket = new Vector<Integer>();
		index = new int[ARTICLES_SIZE][];
		weight = new float[ARTICLES_SIZE][];
		articleNorm = new double[ARTICLES_SIZE];
		cluster = new int[ARTICLES_SIZE];
		number = new int[BUCKETS_SIZE];
		numWords = new int[BUCKETS_SIZE];
		tempNumWords = new int [BUCKETS_SIZE];
		
		datReader = new InputStream(Configs.getDocTermsFile());
        cntReader = new InputStream(Configs.getDocTermsCountFile());
		
        
        leadWriter = new OutputStream(true, new File(Configs.ADDRESS + "Leaders.dat"));
        leadReader = new InputStream(new File(Configs.ADDRESS + "Leaders.dat"));
        
        tempWriter = new OutputStream(true, new File(Configs.ADDRESS + "TempLeader.dat"));
        tempReader = new InputStream(new File(Configs.ADDRESS + "TempLeader.dat"));
        
        
        leader = new float[WORDS];
        
        sr = new SeekerReader();
		tm = new Time();
        
		System.out.println("Clustring has been started...");
	}
	
	private void getVectorSeq(int articleNumber) throws FileNotFoundException, IOException {
        
        int startOffset = DocTermsAndTermsDoc.articleOffset[articleNumber];
        int endOffset = DocTermsAndTermsDoc.articleOffset[articleNumber + 1];
		int N = ARTICLES_SIZE; 
		int wordsSize = endOffset - startOffset;
		
		index[articleNumber] = new int[wordsSize];
		weight[articleNumber] = new float[wordsSize];
		articleNorm[articleNumber] = 0.0;
		
		for (int i = 0;i < wordsSize;++i){
        	int dat = datReader.nextInt() - 1;
			int cnt = cntReader.nextInt();
			index[articleNumber][i] = dat;
			weight[articleNumber][i] = (float) ((cnt * 1.0 / DocTermsAndTermsDoc.nmax[articleNumber]) * Math.log10(N * 1.0 / DocTermsAndTermsDoc.TermDocCnt[dat]));
			articleNorm[articleNumber] += weight[articleNumber][i] * weight[articleNumber][i];
        }
		articleNorm[articleNumber] = Math.sqrt(articleNorm[articleNumber]);
    }
	
	private void getVectorRAF(int number) throws IOException{
		sr.seekOnArticle(number);
		int N = ARTICLES_SIZE ;
		while (sr.hasNext()){
			long res = sr.nextWord();
			int dat = (int) (res >> 30) - 1;
			int cnt = (int) (res & ((1l << 30) - 1));
			leader[dat] = (float) ((cnt * 1.0 / DocTermsAndTermsDoc.nmax[number]) * Math.log10(N * 1.0 / DocTermsAndTermsDoc.TermDocCnt[dat]));
		}
	}
	public double getSimilar(int a){
		double dot = 0.0;
		
		for (int i = 0;i < index[a].length;++i){
			dot += weight[a][i] * leader[index[a][i]];
		}
		if (leaderNorm == 0.0 || articleNorm[a] == 0.0) return 0.0;
		///System.out.println(dot + " " + leaderNorm + " " + articleNorm[a]);
		return dot / (leaderNorm * articleNorm[a]);
	}
	
	private void saveLeader(long l) throws IOException{
		/*leadWriter.seek(l * WORDS * 4 + l);
		for (int i = 0;i < WORDS;++i){
			byte[] bytes = ByteBuffer.allocate(4).putFloat(leader[i]).array();
			leadWriter.write(bytes);
		}
		leadWriter.write('\n');
		leadWriter.flush();*/
		
		//System.err.println("SAVE " + l);
		numWords[(int) l] = 0;
		for (int i = 0;i < WORDS;++i) if (leader[i] != 0.0) {
			++numWords[(int) l];
			byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();
			leadWriter.write(bytes);
			bytes = ByteBuffer.allocate(4).putFloat(leader[i]).array();
			leadWriter.write(bytes);
			
			//System.err.println(i + " " + leader[i]);
		}
		//System.err.println("END");
		leadWriter.flush();
	}
	
	public static void readLeader(long l) throws IOException{
		/*leadReader.seek(l * WORDS * 4 + l);
		leaderNorm = 0.0;
		for (int i = 0;i < WORDS;++i){
			byte[] bytes = {leadReader.nextByte(), leadReader.nextByte(), leadReader.nextByte(), leadReader.nextByte()};
			ByteBuffer buf = ByteBuffer.allocate(4);
			buf.put(bytes);
			leader[i] = buf.getFloat(0);
		    leaderNorm += leader[i] * leader[i];
		}
		leaderNorm = Math.sqrt(leaderNorm);*/
		long sp = 0;
		for (int i = 0;i < l;++i)
			sp += numWords[i] << 3;
		leadReader.seek(sp);
		
		Arrays.fill(leader, (float) 0.0);
		//System.err.println("READ " + l);
		leaderNorm = 0.0;
		byte bytes[] = new byte[4];
		for (int i = 0;i < numWords[(int) l];++i){
			for (int j = 0;j < 4;++j) bytes[j] = leadReader.nextByte();
			int idx = ByteBuffer.allocate(4).put(bytes).getInt(0);
			for (int j = 0;j < 4;++j) bytes[j] = leadReader.nextByte();
			leader[idx] = ByteBuffer.allocate(4).put(bytes).getFloat(0);
			//System.out.println(idx + " " + leader[idx]);
			leaderNorm += leader[idx] * leader[idx];
			
			//System.err.println(idx + " " + leader[idx]);
		}
		//System.err.println("END");
		leaderNorm = Math.sqrt(leaderNorm);
		
	}
	
	public void readTempLeader(long l) throws IOException{
		long sp = 0;
		for (int i = 0;i < l;++i)
			sp += tempNumWords[i] << 3;
		tempReader.seek(sp);
		
		Arrays.fill(leader, (float) 0.0);
		//System.err.println("READ TEMP" + l);
		leaderNorm = 0.0;
		byte bytes[] = new byte[4];
		for (int i = 0;i < tempNumWords[(int) l];++i){
			for (int j = 0;j < 4;++j) bytes[j] = tempReader.nextByte();
			int idx = ByteBuffer.allocate(4).put(bytes).getInt(0);
			for (int j = 0;j < 4;++j) bytes[j] = tempReader.nextByte();
			leader[idx] = ByteBuffer.allocate(4).put(bytes).getFloat(0);
			leaderNorm += leader[idx] * leader[idx];
			//System.err.println(idx + " " + leader[idx]);
		}
		//System.err.println("END");
		leaderNorm = Math.sqrt(leaderNorm);
	}
	
	public void doClustering() throws FileNotFoundException, IOException{
		Arrays.fill(numWords, 0);
		Arrays.fill(numWords, 0);
		leadWriter.seek(0);
		for (int i = 0;i < BUCKETS_SIZE;++i){
			int now = (int) (Math.random() * ARTICLES_SIZE);
			while (bucket.contains(now))
				now = (int) (Math.random() * ARTICLES_SIZE);
			bucket.add(now);
			Arrays.fill(leader, (float) 0.0);
			getVectorRAF(now);
			//System.err.println(now);
			saveLeader(i);
		}
		sr.close();
		System.out.println(BUCKETS_SIZE + " random articles for start clustering selected...");
		Main.zppf.done(11);
		
		tm.setTime();
		for (int i = 0;i < ARTICLES_SIZE;++i)
			getVectorSeq(i);
		
		double maxSimilar[] = new double[ARTICLES_SIZE];
		
		Main.zppf.started(12);
		
		for (;TRACES > 0;--TRACES){	
			Main.zppf.setString(12);
			System.out.println("Start iteration " + TRACES + "...");
			leadReader.seek(0);
			for (int i = 0;i < BUCKETS_SIZE;++i)
				number[i] = 0;
			Arrays.fill(maxSimilar, 0.0);
			
			for (int l = 0;l < BUCKETS_SIZE;++l){
				readLeader(l);
				for (int a = 0;a < ARTICLES_SIZE;++a){
					double temp = getSimilar(a);
					//System.out.println("led " + l + " art " + a + " "+ temp);
					if (temp > maxSimilar[a]){
						maxSimilar[a] = temp;
						cluster[a] = l;
					}
					/*if (temp >= 0.75){
						maxSimilar[a] = temp; 
						cluster[a] = l;
						break;
					}*/
				}
			}
			
			//calcNextLeaders:
			copyToTemp();
			
			System.out.println("Calcuting next leaders...");
			leadWriter.seek(0);
			for (int l = 0;l < BUCKETS_SIZE;++l){
				Arrays.fill(leader, (float)0.0);
				leaderNorm = 0.0;
				for (int a = 0;a < ARTICLES_SIZE;++a){
					if (cluster[a] == l){ 
						++number[l];
						for (int i = 0;i < index[a].length;++i)
							leader[index[a][i]] += weight[a][i];
					}	
				}
				if (number[l] != 0){//If number[l] == 0 lots of problems comes :-s
					for (int i = 0;i < WORDS;++i)
						leader[i] /= (number[l]);
				}
				else readTempLeader(l); 
				saveLeader(l);
			}
			
			System.out.println("Iteration " + TRACES + " done and new leaders are made...");
		}
		
		double mean = 0.0;
		for (int a = 0;a < ARTICLES_SIZE;++a)
			mean += maxSimilar[a];
		
		leadReader.seek(0);
		leadWriter.close();
		
		System.out.println("Done in " + tm.getTime() + " ms.");
		tm.end();
		//System.err.println("mean : " + mean / ARTICLES_SIZE);
		
	}
	
	public void copyToTemp() throws IOException{
		
		long sp = 0;
		for (int i = 0;i < BUCKETS_SIZE;++i){
			sp += (numWords[i] << 3);
			tempNumWords[i] = numWords[i];
		}
		
		
		//System.out.println(sp);
		leadReader.seek(0);
		tempWriter.seek(0);
		for (int i = 0;i < sp;++i)
			tempWriter.write(leadReader.nextByte());
		tempWriter.flush();
		
	}
	
	@SuppressWarnings("unchecked")
	public void saveResults() throws IOException{
		Vector<Integer> content[] = new Vector[BUCKETS_SIZE];
		for (int i = 0;i < BUCKETS_SIZE;++i)
			content[i] = new Vector<Integer>();
		
		for (int i = 0;i < ARTICLES_SIZE;++i)
			content[cluster[i]].add(i);
		
		OutputStream os = new OutputStream(true, new File(Configs.ADDRESS + "Clusters.dat"));
		for (int i = 0;i < BUCKETS_SIZE;++i){
			for (int j = 0;j < content[i].size();++j){
				int temp = content[i].get(j);
				for (int k = 1000000;k != 0;temp %= k, k /= 10)
					os.write((char)('0' + temp / k));
				os.write('\n');
			}
			
		}
		os.flush();
		os.close();
		
		os = new OutputStream(true, new File(Configs.ADDRESS + "Numbers.dat"));
		os.write("0000000\n".getBytes());
		int sum = 0;
		//System.out.println(BUCKETS_SIZE + " bucket");
		for (int i = 0;i < BUCKETS_SIZE;++i){
			sum += number[i];
			int temp = sum;
			for (int k = 1000000;k != 0;temp %= k, k /= 10)
				os.write((char)('0' + temp / k));
			os.write('\n');
		}
		os.flush();
		os.close();
		
		System.out.println("Cluster of each article has been saved in Clusters.dat");
	}
}
