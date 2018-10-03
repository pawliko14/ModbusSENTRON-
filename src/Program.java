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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.*;
import de.re.easymodbus.modbusclient.ModbusClient.RegisterOrder;



public class Program extends JFrame {


	static private int LICZBA_POMIAROW = 50;    // 
	static private int CZESTOTLIWOSC = 30000;// 30000 = 30sek , przy 50000 powinien sie pojawic timeout
	static private int register = 25;
	static private int Offset = 2;   // 2 for 65 regiester, 4 for 801 register
	static boolean Show = false;					// pokazuje na ekranie 200 pierwszych rejestrow
	static String Connection_ip = "192.168.90.145";
	static String Directory = "C://Users/el08/Desktop/charts/";
	
	public static double CurrentValue = 0;
	static private String start_time= "";
	static private String end_time = "";
	static private String Measurment_day = "";
	
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
    
    private static void GenerateDataSet()
    {
    	ModbusClient modbusClient = new ModbusClient(Connection_ip,502);
		try
		{
			modbusClient.Connect();
			
			if(modbusClient.isConnected())
			{
				System.out.println("Connection is set");
				int z =0;
				double Wh_1;
	
				if(Show == true ) 
					ShowAllRegiesters(modbusClient);
					
				while(z< LICZBA_POMIAROW)
				{
					
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
					
					z++;
				}
				start_time= dates[0].substring(11, 19); // 2018
				Measurment_day = dates[0].substring(0, 10); // first value
				end_time = dates[z-1].substring(11, 19); // last value
	
				FileName = Measurment_day.substring(0,3);
				
				z = 0;
				
				
					for ( int i = 0 ;i < LICZBA_POMIAROW ;i++ )
						System.out.println(dates[i] + " - kWh: "+ kWh_table[i]);
				

			}
			else
			{
				System.out.println("Connection lost, trying to connect:");
				int timer = 0;
				while(!modbusClient.isConnected())
				{
				System.out.println("try nr: "+ timer);
				modbusClient.Connect();
				Thread.sleep(2000);
				}
				
			}

		}
		catch (Exception e)
		{		
	        System.out.println(e.toString());

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
    	String nazwa = "testChangeLater";
        File f = new File("C://Users/el08/Desktop/charts/CollectedData" + nazwa + ".txt");
        
       // dates[i] + "  kWh: "+ kWh_table[i]);
        
        try (PrintWriter out = new PrintWriter(f)) {
        	for(int i = 0 ;i < kWh_table.length;i++)
        		  out.println(dates[i] + ";"+ kWh_table[i]);
        }
          

    }
    
    public static void run() throws IOException 
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

	}

}
  



