import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TimerCounter extends Thread {
   public boolean suspended = false;
   public static Date dates;
   public static String datesString;
   
   public TimerCounter() {
   }
   
   public void run() {
		Thread thread = Thread.currentThread();
		System.out.println("RunnableJob is being run by " + thread.getName() + " (" + thread.getId() + ")");
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

      while(true) {
    		Date date = new Date();
    		String dates = dateFormat.format(date);	
    		System.out.println(dates);
			FinalClass.Value_2.setText(dates);

         
         try {
			Thread.sleep(1000);
			synchronized(this){
				while(suspended) {
					wait();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
   }
   
   public void suspendThread()
   {
	   suspended = true;
   }
   
   public void resumeThread()
   {
	   suspended = false;
	   synchronized(this) {
		   notify();
	   }
   }
}