import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class ShowLiveCharts {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void NewScreen() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ShowLiveCharts window = new ShowLiveCharts();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ShowLiveCharts() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(53, 132, 86, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
	}
}
