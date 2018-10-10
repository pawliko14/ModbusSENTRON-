import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class MainClass extends Thread{
	
	   public boolean suspended = false;

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
	
	public void run()
	{
		try {
			Program.run();
			Thread.sleep(200);
			synchronized(this){
				while(suspended) {
					wait();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
