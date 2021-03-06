import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JToggleButton;

public class FinalClass {

	private JFrame frame;
	private static JLabel label1;
	private JButton stop1;
	private JButton stop2;
	private JButton continue1;
	private JButton continue2;
	 static JButton LiveChart;

	int counter = 0;
	
	 static MainClass thread;
     static TimerCounter thread2;
     private static boolean thread_running = false;
     private static boolean thread2_running  = false;

	private static boolean started = false;
	 static JTextField Value_1;
	 static JTextField Value_2;
	 private JTextField txtFreq;
	 private JTextField txtQuantity;
	 
	 private JTextField frequency;
	 private JTextField quantity;
	 private JTextField Counter;
	 static JTextField Count;
	 private JTextField txtBnPr;
	 private JTextField txtBnPrtimer;
	 
	public static void general()
	{
			thread = new MainClass();
			thread2 = new TimerCounter();
					
	      thread.setName("Running Program");
	      thread.start();
	      
	      thread2.setName("Counter(timer)");
	      thread2.start();
	      
	      Thread currentThread = Thread.currentThread();
			System.out.println("Main thread: " + currentThread.getName() + "(" + currentThread.getId() + ")");
	      
	
	    
	}
	
	public void run() throws InterruptedException, SQLException
	{
		try {
			Program.run();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	  public static void main(String [] args) {
	
			  EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							FinalClass window = new FinalClass();
							window.frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});	   
	   }
	
	public FinalClass() throws IOException {
		

		IniFile.Ini(); // Czyta iniFile, ktory zawiera zmienne do pomiarow
						// nie trzeba bedzie modyfikowac kodu, tylko sam ini.txt
		
		initialize();
			
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 698, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		label1 = new JLabel("�");
		label1.setForeground(Color.RED);
		label1.setBounds(207, 26, 15, 14);
		frame.getContentPane().add(label1);
		
		JLabel label2 = new JLabel("�");
		label2.setForeground(Color.RED);
		label2.setBounds(400, 26, 20, 14);
		frame.getContentPane().add(label2);
		
		JButton Start = new JButton("Start gathering");
		
		Start.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
			if (!frequency.getText().equals("0") && !quantity.getText().equals("0"))
			{
				frequency.setEditable(false);
				quantity.setEditable(false);
				counter++;
				//Clicked for the first time
				if(counter <= 1) {
					started = true;
					if(thread_running == false || thread2_running == false)
					{
						System.out.println("watki niezywe");
						
						thread_running = true;
						thread2_running = true;
						
					}
					general();
					
					if(thread2.isAlive()) 
						label2.setForeground(Color.GREEN);
						
					if(thread.isAlive())
						label1.setForeground(Color.GREEN);
								
					stop1.setEnabled(true);
					stop2.setEnabled(true);
					
				}
				if (counter > 2)
				{
					System.out.println("clikniete razy: "+ counter );
					thread.stop(); // dangerous method
					thread2.stop();
					
					if(thread2.isAlive() || thread.isAlive())
						System.out.println("watki zywe");
					
					else 
					{
						System.out.println("watki NIEZYWE");
						general();

					}
				 }

				
			 }
			}
		});
		Start.setBounds(24, 11, 134, 45);
		frame.getContentPane().add(Start);
		
		stop1 = new JButton("stop");
		stop1.setVerticalAlignment(SwingConstants.BOTTOM);
		stop1.setEnabled(false);
		stop1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				thread_running = false;
				
				thread.suspendThread();


				if(!thread.isAlive())
					label1.setForeground(Color.RED);
				
				stop1.setEnabled(false);
				continue1.setEnabled(true);

			}
		});
		stop1.setBounds(183, 70, 65, 23);
		frame.getContentPane().add(stop1);
		
		stop2 = new JButton("Stop");
		stop2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				started = false;			
				thread2_running = false;
				
				thread2.suspendThread();
				
				if(!thread2.isAlive() || thread2.suspended == true)
					label2.setForeground(Color.RED);

				
				stop2.setEnabled(false);
				continue2.setEnabled(true);
				
			}
		});
		stop2.setEnabled(false);
		stop2.setBounds(380, 71, 76, 23);
		frame.getContentPane().add(stop2);
		
		 continue1 = new JButton("continue");
		continue1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				stop1.setEnabled(true);
				continue1.setEnabled(false);
				label1.setForeground(Color.GREEN);
				thread2.resumeThread();



				
			}
		});
		continue1.setEnabled(false);
		continue1.setBounds(258, 71, 76, 23);
		frame.getContentPane().add(continue1);
		
		
		
		 continue2 = new JButton("Continue");
		continue2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				stop2.setEnabled(true);
				continue2.setEnabled(false);
				label2.setForeground(Color.GREEN);
				thread2.resumeThread();
				
			}
		});
		
		
		continue2.setEnabled(false);
		continue2.setBounds(459, 71, 96, 23);
		frame.getContentPane().add(continue2);
		
		Value_1 = new JTextField();
		Value_1.setEditable(false);
				
		Value_1.setBounds(183, 45, 151, 20);
		frame.getContentPane().add(Value_1);
		Value_1.setColumns(10);
		
		Value_2 = new JTextField();
		Value_2.setEditable(false);
		Value_2.setColumns(10);
		Value_2.setBounds(380, 45, 175, 20);
		frame.getContentPane().add(Value_2);
		
		txtFreq = new JTextField();
		txtFreq.setEditable(false);
		txtFreq.setText("frequency");
		txtFreq.setBounds(10, 67, 57, 20);
		frame.getContentPane().add(txtFreq);
		txtFreq.setColumns(10);
		
		txtQuantity = new JTextField();
		txtQuantity.setEditable(false);
		txtQuantity.setText("quantity");
		txtQuantity.setColumns(10);
		txtQuantity.setBounds(77, 67, 49, 20);
		frame.getContentPane().add(txtQuantity);
		
		frequency = new JTextField();
		frequency.setEditable(false);
		
		frequency.setText(Integer.toString(Program.CZESTOTLIWOSC));
		
		frequency.setBounds(10, 98, 57, 20);
		frame.getContentPane().add(frequency);
		frequency.setColumns(10);
		
		quantity = new JTextField();
		quantity.setEditable(false);
		
		quantity.setText(Integer.toString(Program.LICZBA_POMIAROW));
		
		quantity.setColumns(10);
		quantity.setBounds(80, 98, 46, 20);
		frame.getContentPane().add(quantity);
		
		Counter = new JTextField();
		Counter.setText("Count");
		Counter.setEditable(false);
		Counter.setBounds(136, 67, 37, 20);
		frame.getContentPane().add(Counter);
		Counter.setColumns(10);
		
		Count = new JTextField();
		Count.setText(Integer.toString(Program.z));

		Count.setEditable(false);
		Count.setBounds(146, 98, 30, 20);
		frame.getContentPane().add(Count);
		Count.setColumns(10);
		
		
		 LiveChart = new JButton("Show final Charts");
		LiveChart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				TabChart test = new TabChart();
				test.tabchart();
				
				if(LiveChart.getModel().isEnabled()) 
					LiveChart.setEnabled(false);	
				
								
			}
		});
		LiveChart.setBounds(10, 228, 177, 23);
		frame.getContentPane().add(LiveChart);
		
		txtBnPr = new JTextField();
		txtBnPr.setHorizontalAlignment(SwingConstants.CENTER);
		txtBnPr.setEditable(false);
		txtBnPr.setText("BN25- Pr2");
		txtBnPr.setBounds(248, 23, 86, 20);
		frame.getContentPane().add(txtBnPr);
		txtBnPr.setColumns(10);
		
		txtBnPrtimer = new JTextField();
		txtBnPrtimer.setText("BN25- Pr2(timer)");
		txtBnPrtimer.setHorizontalAlignment(SwingConstants.CENTER);
		txtBnPrtimer.setEditable(false);
		txtBnPrtimer.setColumns(10);
		txtBnPrtimer.setBounds(444, 23, 111, 20);
		frame.getContentPane().add(txtBnPrtimer);
	}
}
