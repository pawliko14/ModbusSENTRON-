
public class TimerCounter implements Runnable {
	private int id;

	public TimerCounter(int id) {
		this.id = id;
	}

	@Override
	public void run() {
		while(true) {
			System.out.println("Watek "+id);
			try {
				//usypiamy w¹tek na 100 milisekund
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


}
