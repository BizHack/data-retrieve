package zakhire;

import java.util.Scanner;

public class Trie {
	static int SIZE = 100;
	private String data[];
	public Trie(){
		data = new String[SIZE];
		for (int i = 0;i < SIZE;++i)
			data[i] = ";";
	}
	
	private int getPlace(int numOfCommas, int let){
		int idx = 0;
		for (int i = 0;i < numOfCommas;++idx){
			char now = data[let].charAt(idx);
			if (now == ';') return idx;
			if (now == ',') ++i;
		}
		return idx;//avvalin character ba'de comma
	}
	
	private Pair isIn(char c, int let, int numOfCommas){
		int idx = getPlace(numOfCommas, let);
		while (true){
			char now = data[let].charAt(idx);
			if (now == ',' || now == ';') return new Pair(idx, false);
			if (now == c) return new Pair(idx, true);
			idx++;
		}
	}
	
	public boolean insert(String word){
		int let = 0, numOfCommas = 0;
		Pair tmp = new Pair(0, false);
		
		while (let < word.length()){
			tmp = isIn(word.charAt(let), let, numOfCommas);
			if (!tmp.find()) break;
			let++;
			numOfCommas = tmp.getPlace() - numOfCommas;
		}
		
		if (let == word.length()){
			
			return false;
		}
		
		int place = tmp.getPlace();
		data[let] = data[let].substring(0, place) + word.charAt(let) + data[let].substring(place);
		numOfCommas = place - numOfCommas;
		
		for (++let;let <= word.length();let++){
			System.out.println(place + " " + numOfCommas + " " + let);
			String str = "";
			if (let == word.length()) str = "";
			else str += word.charAt(let);
			place = getPlace(numOfCommas, let);
			data[let] = data[let].substring(0, place) + str + "," + data[let].substring(place);
			numOfCommas = place - numOfCommas;
			/*
			if (place != 0){
				//(data[let].charAt(place) != ',' && data[let].charAt(place) != ';')){
				//beyne 2 ta , nabayad baz ham , gozasht
				System.out.println(data[let].substring(0, place));
				data[let] = data[let].substring(0, place) + "," + str + data[let].substring(place);
				numOfCommas = place - numOfCommas + 1;
			} else {
				if (let == word.length()) str = ",";//null tahe har word
				data[let] = data[let].substring(0, place) + str + data[let].substring(place);
				numOfCommas = place - numOfCommas;
			}*/
		}
		return true;
	}
	
	public void print(){
		for (int i = 0;i < 10;++i)
			System.out.print(data[i]);
		System.out.println();
	}
	
	public static void main(String args[]){
		Trie mt = new Trie();
		String st = "Alisafe";
		System.out.println(st.substring(0, 5) + st.substring(5));
		Scanner sc = new Scanner(System.in);
		while (sc.hasNext()){
			String word = sc.next();
			if (mt.insert(word)) System.out.println("Successfuly inserted!");
			else System.out.println("This word is in the dictionary!");
			mt.print();
		}
	}
	
	
	public class Pair{
		private int x;
		private boolean b;
		public Pair(int x, boolean b){
			this.x = x;
			this.b = b;
		}
		
		public int getPlace(){
			return x;
		}
		
		public boolean find(){
			return b;
		}
		
	}
	
	@SuppressWarnings("unused")
	private class Data {
		byte b;
		public Data(){
			
		}
		
		public void setChar(char c){
			b = (byte) (((b >> 5) << 5) | c);
		}
		
		public void setEnd(){
			b = (byte) (b & 32);
		}
		
		public char getChar(){
			return (char) ('a' + (b & 31));
		}
	}

}
