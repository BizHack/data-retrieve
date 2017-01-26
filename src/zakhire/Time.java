package zakhire;

public class Time implements Runnable{
	private int day, hour, min, sec;
	boolean end = false;
	public Time(){
		day = hour = min = sec = 0;
        new Thread(this).start();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true){
			try {
				Thread.sleep(100);
				increaseTime();
				if (end) break;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void increaseTime(){
		sec += 1;
		if (sec == 60) {min++; sec = 0;};
		if (min == 60) {hour++; min = 0;}
		if (hour == 12) {day++; hour = 0;}
	}
	
	public int getTime(){
		return (sec + min * 60 + hour * 3600) * 10;
	}
	
	public void setTime(){
		day = hour = min = sec = 0;
	}
	
	public void end(){
		this.end = true;
	}
}
