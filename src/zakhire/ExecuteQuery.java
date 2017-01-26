package zakhire;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

public class ExecuteQuery {
	static int ARTICLE_SIZE = DocTermsAndTermsDoc.ArticleCounter;
	static int BUCKET_SIZE = DocTermsAndTermsDoc.BucketSize;
	static int MAXCHECK, MAXRES, MAXART;
	static float[] leader = Clustering.leader;
	
	SeekerReader sr;
	InputStream numbers, clusters;
	
	double[] wei;
	int[] dat, cnt, vec;
	Point[] ans, topLeaders;
	
	public ExecuteQuery(int expectedResulst) throws IOException {
		// TODO Auto-generated method stub
		numbers = new InputStream(new File(Configs.ADDRESS + "Numbers.dat"));
		clusters = new InputStream(new File(Configs.ADDRESS + "Clusters.dat"));
		sr = new SeekerReader();
		
		MAXCHECK = (int) Math.sqrt(BUCKET_SIZE);
		MAXRES = expectedResulst;
		
		ans = new Point[MAXRES + 1];
		for (int i = 0;i <= MAXRES;++i)
			ans[i] = new Point(-1, 0.0);
		
		topLeaders = new Point[MAXCHECK + 1];	
		for (int i = 0;i <= MAXCHECK;++i)
			topLeaders[i] = new Point(-1, 0.0);
		
		MAXART = MAXRES * MAXCHECK;
		vec = new int[MAXART];
	}
	
	public Vector<Vector<String>> executeQuery(int[] d, int[] c) throws IOException{
		dat = d;
		cnt = c;
		
		changeToWeight();
		checkLeaders();
		checkArticles();
		
		Vector<Vector<String>> res = new Vector<Vector<String>>();
		for (int i = 0;i < MAXRES;++i){
			if (ans[i].art == -1) break;
			Vector<String> v = new Vector<String>();
			v.add(Integer.toString(i + 1));
			v.add(Integer.toString(ans[i].art));
			int temp = (int)(ans[i].sim * 10000);
			char[] ch = {'0', '0', '.', '0', '0', ' ', '%'};
			for (int j = 4;j >= 0;--j){
				ch[j] = (char)('0' + temp % 10);
				temp /= 10;
				if (j == 3) j--;
			}
			v.add(new String(ch));
			res.add(v);
		}
		return res;
	}
		
	private void changeToWeight() {
		wei = new double[dat.length + 1];
		
		double queryNorm = 0.0, nmax = 0.0;
		for (int i = 0;i < dat.length;++i)
			nmax = Math.max(nmax, cnt[i]);
		for (int i = 0;i < dat.length;++i){
			wei[i] = (cnt[i] * 1.0 /  nmax * Math.log10(ARTICLE_SIZE * 1.0 / DocTermsAndTermsDoc.TermDocCnt[dat[i]]));
			queryNorm += wei[i] * wei[i];
		}
		wei[dat.length]= Math.sqrt(queryNorm);
	}

	private void checkLeaders() throws IOException{
		for (int i = 0;i <= MAXCHECK;++i){
			topLeaders[i].art = -1;
			topLeaders[i].sim = 0.0;
		}
		
		//for (int i = 0;i < BUCKET_SIZE;++i)
		//	System.out.println(Clustering.numWords[i]);
		
		for (int l = 0;l < BUCKET_SIZE;++l){
			Clustering.readLeader(l);
			double temp = getSimilarToLeader();
			
			//System.out.println(l + " ????????? " + temp);
			if (temp == 0.0) continue;
			
			topLeaders[MAXCHECK].art = l;
			topLeaders[MAXCHECK].sim = temp;
			Arrays.sort(topLeaders);
		}
		
		System.out.println(MAXCHECK + " BEST CLUSTERS SELECTED");
		for (int i = 0;i < MAXCHECK;++i){
			//System.out.println(topLeaders[i].art + " " + topLeaders[i].sim);
		}
	}
	
	private void checkArticles() throws IOException{
		for (int i = 0;i <= MAXRES;++i){
			ans[i].art = -1;
			ans[i].sim = 0.0;
		}
		int CJ = 0;
		for (int i = 0;i < MAXCHECK;++i){
			if (topLeaders[i].art == -1) break;
			numbers.seek(topLeaders[i].art * 7 + topLeaders[i].art);
			int beg = numbers.nextInt();
			int end = numbers.nextInt();
			//System.err.println(beg + " " + end);
			
			clusters.seek(beg * 7 + beg);
			for (int j = beg;j < end;++j){
				vec[CJ++] = clusters.nextInt();
				if (CJ == MAXART) break;
			}
			if (CJ == MAXART) break;
		}
		Arrays.sort(vec, 0, CJ);
		for (int i = 0;i < CJ;++i){
			int art = vec[i];
			double sim = getSimilarToArticle(art);
			if (sim == 0.0) continue;
			
			ans[MAXRES].art = art;
			ans[MAXRES].sim = sim;
			Arrays.sort(ans);
		}
		
	}

	private double getSimilarToArticle(int art) throws IOException {
		sr.seekOnArticle(art);
		double temp = 0.0, dot = 0.0, articleNorm = 0.0;
		int i = 0, cnt, dim;
		long res = sr.nextWord();
		while (sr.hasNext() && i < dat.length){
			dim = (int) (res >> 30) - 1;
			cnt = (int) (res & ((1l << 30) - 1));
			temp = (float) ((cnt * 1.0 / DocTermsAndTermsDoc.nmax[art]) * Math.log10(ARTICLE_SIZE * 1.0 / DocTermsAndTermsDoc.TermDocCnt[dim]));
		    articleNorm += temp * temp;
		    
		    if (dat[i] == dim) {
		    		dot += wei[i++] * temp;
		    		res = sr.nextWord();
		    }
		    else if (dat[i] < dim) i++;
		    else res = sr.nextWord();
		}
		while (sr.hasNext()){
			res = sr.nextWord();
			dim = (int) (res >> 30) - 1;
			cnt = (int) (res & ((1l << 30) - 1));
			temp = (float) ((cnt * 1.0 / DocTermsAndTermsDoc.nmax[art]) * Math.log10(ARTICLE_SIZE * 1.0 / DocTermsAndTermsDoc.TermDocCnt[dim]));
		    articleNorm += temp * temp;
		}
		articleNorm = Math.sqrt(articleNorm);
		if (articleNorm == 0.0 || wei[wei.length - 1] == 0.0) return 0.0;
		return dot / (articleNorm * wei[wei.length - 1]);
	}

	private double getSimilarToLeader(){
		double dot = 0.0, queryNorm = wei[wei.length - 1];
		
		for (int i = 0;i < dat.length;++i)
			dot += wei[i] * leader[dat[i]];
		
		//System.out.println("Oops: " + dot + " " + Clustering.leaderNorm + " " + queryNorm);
		if (Clustering.leaderNorm == 0.0 || queryNorm == 0.0) return 0.0;
		return dot / (Clustering.leaderNorm * queryNorm);
	}
	
	
	private class Point implements Comparable<Point>{
		int art;
		double sim;
		public Point(int art, double sim) {
			// TODO Auto-generated constructor stub
			this.art = art;
			this.sim = sim;
		}
		
		@Override
		public int compareTo(Point that) {
			// TODO Auto-generated method stub
			if (this.sim == that.sim) return 0;
			if (this.sim < that.sim) return 1;
			return -1;
		}
	}
}
