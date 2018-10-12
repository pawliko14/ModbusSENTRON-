import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.*;
import de.re.easymodbus.modbusclient.ModbusClient.RegisterOrder;


/*
sprawdzic czy po reconnection wciaz jaki bedzie stan licznika,
zaimplementowac globalny licznik pomiarow niezalezny od stanu po rozlaczeniu,
czyli liczy liczy np 10, myk rozlaczylo i polaczylo, ma byc potem 11 12 pomiarow itp...
 */

public class Program extends JFrame {

	static Connection connection=null;
	static public int LICZBA_POMIAROW =10;    //  450, czyli 15h pracy z pomiarem co 2 minuty
	static public int CZESTOTLIWOSC = 60000*1;// 60000 * 2 -> co 2 minuty pomiar
	static private int register = 25;
	static private int Offset = 2;   // 2 for 65 regiester, 4 for 801 register
	static boolean Show = false;					// pokazuje na ekranie 200 pierwszych rejestrow
	static String Connection_ip = "192.168.90.145";
	static String Directory = "C://Users/el08/Desktop/charts/";
	
	public static double CurrentValue = 0;
	static private String start_time= "";
	static private String end_time = "";
	static private String Measurment_day = "";
	static public int z =0;
	
	static private String FileName = " test";
	
	
	
	static double[] kWh_table = new double[LICZBA_POMIAROW];
	static String[] dates = new String[LICZBA_POMIAROW];	
	
	
    public Program() throws IOException {
    	
        initUI();
    }

    private void initUI() throws IOException {

        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        add(chartPanel);

        pack();
        setTitle("Line chart");
        setLocationRelativeTo(null);
       // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static XYDataset createDataset() {

        XYSeries series = new XYSeries("kWh,  Date :" + Measurment_day + "     Start time:"+ start_time + "     End time:" + end_time);

        // UWAGA! TUTAJ SERIA X JEST CYFRAMI 1,2... POWINNY BYC POZNIEJ DATY!
        for(int i = 0 ; i<kWh_table.length ;i++)
         series.add(i, kWh_table[i]);
        

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        return dataset;
    }

    private static JFreeChart createChart(XYDataset dataset) throws IOException {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "energy consumption over time", 
                "TIME", 
                "kWh", 
                dataset, 
                PlotOrientation.VERTICAL,
                true, 
                true, 
                false 
        );

        XYPlot plot = chart.getXYPlot();
        // CHANGE Y-AXIS LIMITS
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setAutoRange(true);
        rangeAxis.centerRange(kWh_table[0]); // center curve around first measured value
        
        NumberAxis domainAxis = new NumberAxis("X-Axis");
        domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setRange(0,LICZBA_POMIAROW);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("Energy Consumption over Time",
                        new Font("Serif", java.awt.Font.BOLD, 18)
                )
        );

      //Save chart as PNG 1st goes to project directory, 2nd to the desktop charts directory
       // ChartUtilities.saveChartAsPNG(new File(Directory + "ConsumptionChart" +FileName +".png"), chart, 800, 600);
        
      //  exportAsPNG(chart);
        
        return chart;

    }
    
    private static void GenerateDataSet() throws UnknownHostException, IOException, InterruptedException
    {
    	ModbusClient modbusClient = new ModbusClient(Connection_ip,502);
		try
		{
			modbusClient.Connect();
			
			if(modbusClient.isConnected())
			{
				System.out.println("Connection is set");
				double Wh_1;
	
				if(Show == true ) 
					ShowAllRegiesters(modbusClient);
					
				while(z< LICZBA_POMIAROW)
				{
					System.out.println("pomiar:" + z);
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();
					
					// first double register, 2nd float
					Wh_1 = ModbusClient.ConvertRegistersToDouble(modbusClient.ReadHoldingRegisters(register, Offset),RegisterOrder.HighLow);
					Wh_1 = ModbusClient.ConvertRegistersToFloat(modbusClient.ReadHoldingRegisters(register, Offset),RegisterOrder.HighLow);

					// przeliczenie na kWh
					//1 Watogodzina [Wh] = 0,001 Kilowatogodzina [kWh]
					
					double kWh_1 = 0.001 * Wh_1;
					
					CurrentValue = kWh_1;
					FinalClass.Value_1.setText(String.valueOf(Program.CurrentValue));


					dates[z] = dateFormat.format(date);
					kWh_table[z] = kWh_1;
					
					
					end_time = dates[1];
					Measurment_day = "3";
					
					Thread.sleep(CZESTOTLIWOSC);
					FinalClass.Count.setText(Integer.toString(z));
					z++;
				}
				
				start_time= dates[0].substring(11, 19); // 2018
				Measurment_day = dates[0].substring(0, 10); // first value
				end_time = dates[z-1].substring(11, 19); // last value
	
				FileName = Measurment_day.substring(0,3);
				
				//z = 0; // <- sprawdzic pozniej czy nie spowoduje crashu
				
				
					for ( int i = 0 ;i < LICZBA_POMIAROW ;i++ )
						System.out.println(dates[i] + " - kWh: "+ kWh_table[i]);
				

			}

		}
		catch (Exception e)
		{		
			System.out.println("Connection lost, trying to connect:");      
			System.out.println(e.toString());
			//recurse run a program again, without losing collected data
			GenerateDataSet(); 

		}	
    }
    
    public static void ShowAllRegiesters(ModbusClient modbusClient) throws IllegalArgumentException, UnknownHostException, SocketException, ModbusException, IOException
	{		
		float[] variables = new float[101];
		int x = 0;
		for(int i = 1 ;i< 203; i+=2)
		{
			variables[x] =   ModbusClient.ConvertRegistersToFloat(modbusClient.ReadHoldingRegisters(i, 2),RegisterOrder.HighLow);
			x++;
		}
		x = 0;

		float prad = ModbusClient.ConvertRegistersToFloat(modbusClient.ReadHoldingRegisters(50013, 2),RegisterOrder.HighLow);
		//long prad_2 = (long)prad;
		//System.out.println("Prad po stronie wtornej(1-5A) : " + prad_2);
		
		for(int i = 0; i<variables.length;i++)
			System.out.println("var1: "+ i + " val: "+ variables[i]);
		
	}
    
    public static void exportAsPNG(JFreeChart chart) throws IOException 
    {
    	
        File f = new File("C://Users/el08/Desktop/charts/PNGTimeSeries" + start_time + ".png");
        BufferedImage chartImage = chart.createBufferedImage( 600, 400, null); 
        ImageIO.write( chartImage, "png", f ); 
        System.out.println("Chart created");
    }
    
    public static void SaveDataInTxt() throws FileNotFoundException 
    {

        File f = new File("C://Users/el08/Desktop/charts/CollectedData" + GetCUrrentDataTime() + ".txt");
        
       // dates[i] + "  kWh: "+ kWh_table[i]);
        
        try (PrintWriter out = new PrintWriter(f)) {
        	for(int i = 0 ;i < kWh_table.length;i++)
        		  out.println(dates[i] + ";"+ kWh_table[i]);
        }
          

    }
    
    public static String GetCUrrentDataTime()
    {
    		String datka;
            LocalDate localDate = LocalDate.now();            
            return datka = DateTimeFormatter.ofPattern("yyy-MM-dd").format(localDate);
    }
    
    public static void run() throws IOException, InterruptedException, SQLException 
    {
    	
    	Thread thread = Thread.currentThread();
		System.out.println("RunnableJob is being run by " + thread.getName() + " (" + thread.getId() + ")");
		
    	GenerateDataSet();
   	  	
    	 Program ex = new Program();
    	 ex.setVisible(true);

        try {
			SaveDataInTxt();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        SelectFromDatabase();
        
        System.out.println("proba wyslania do bazy danych");
        
        for(int i = 0; i < kWh_table.length;i++)
        	System.out.println("kWh:"+ kWh_table[i] + "dates:" + dates[i] );
   
        PushIntoDatabase();
        
        
  // 	 System.exit(1) ;
        // end program after genereting dataset and save them into file
        // recently was accept by button, after all it has to be run without buttons

	}
    
    public static void SelectFromDatabase()
    {
		connection = RCPdatabaseConnection.dbConnector("tosia", "1234","machines"); // test , fatdb

		String query = null;
		String ID;
		String Date;
		String Time;
		String Power;
		
		try {

			query = "select * from machines.bn25_pr2 order by ID";
			PreparedStatement pst=connection.prepareStatement(query);
			ResultSet rs=pst.executeQuery();
			
				while(rs.next())
				{
					ID = rs.getString("ID");
					Date = rs.getString("Date");
					Time = rs.getString("Time");
					Power = rs.getString("PowerConsumption");

					System.out.println(" Id: " + ID + " Date: " + Date + " Time: " + Time + " Power: " + Power );
					
				}	
			pst.close();			
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		

    }
    public static void PushIntoDatabase() throws SQLException
    {
		connection = RCPdatabaseConnection.dbConnector("tosia", "1234","machines"); // test , fatdb
		String query = "0";
		String  date = "2018-11-11";
		String Time = "10:10:10";
		String PowerConsumption = "4";
		
		
		//query = "INSERT INTO bn25_pr2 (Date, Time, PowerConsumption)\r\n" + 
		//		"VALUES ('"+date+"', '"+Time+"', '"+PowerConsumption+"')";
		String[] godzina = new String[dates.length];
		String[] dzien = new String[dates.length];
		String[] wartosc = new String[kWh_table.length];
		
		System.out.println("size1: " + dates.length + "size2 : "+ kWh_table.length);
		
		for(int i = 0; i< kWh_table.length;i++)
		{
			dzien[i] = dates[i].substring(0, 11);
			godzina[i] = dates[i].substring(11, 19);
			wartosc[i] = String.valueOf(kWh_table[i]);
		}
		System.out.println("dzien"+ dzien[3] + "godzina" + godzina[3] + "wartosc: "+ wartosc[3]);
		
		query = "INSERT INTO bn25_pr2 (Date, Time, PowerConsumption)\r\n" + 
				"VALUES (?,?,?)";

		PreparedStatement pst=connection.prepareStatement(query);
		//ResultSet rs=pst.executeQuery();
		
		for(int i = 0 ; i < dates.length-1 ;i++)
		{
			pst.setString(1, dzien[i]);
			pst.setString(2, godzina[i]);
			pst.setString(3, wartosc[i]);
			pst.addBatch();
		}
		pst.executeBatch();
		pst.close();			
		//rs.close();
		
		
    }
    

}
  



