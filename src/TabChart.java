import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class TabChart {

	private double Random = new Random().nextDouble();
	
    private void display() {
        JFrame f = new JFrame("MachinesCharts");
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JTabbedPane jtp = new JTabbedPane();
         
        jtp.add("BN25", createPane(Program.CurrentValue, "BN25"));  
        jtp.add("BN01", createPane(Program.CurrentValue * Random* 2, "BN01"));
        jtp.add("BN02", createPane(Program.CurrentValue * Random * 3, "BN02"));
        

        f.add(jtp, BorderLayout.CENTER);
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
     
        f.add(p, BorderLayout.SOUTH);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private ChartPanel createPane(double value, String nazwa) {
        final XYSeries series = new XYSeries("Data");
  
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        new Timer(Program.CZESTOTLIWOSC, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                series.add(series.getItemCount(), value);
            }
        }).start();
        JFreeChart chart = ChartFactory.createXYLineChart(nazwa, "-",
            "kW", dataset, PlotOrientation.VERTICAL, false, false, false);
        
        XYPlot plot = chart.getXYPlot();
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setAutoRange(false);
        rangeAxis.setRange(0.03, 0.40);
        rangeAxis.centerRange(Program.CurrentValue); // center curve around first measured value
        
        
        return new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(480, 240);
            }
        };
    }

    public static void tabchart() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TabChart().display();
            }
        });
    }
}