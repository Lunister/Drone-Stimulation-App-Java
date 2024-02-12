import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.JButton;

public class test {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					test window = new test();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the application.
	 */
	public test() {
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
		
		JLabel lblWelcomeToThe = new JLabel("Welcome to the Drone Simulator Project");
		lblWelcomeToThe.setBounds(66, 12, 284, 15);
		frame.getContentPane().add(lblWelcomeToThe);
		
		JLabel lblWhatIsYour = new JLabel("What is your inquiry?");
		lblWhatIsYour.setBounds(133, 39, 150, 15);
		frame.getContentPane().add(lblWhatIsYour);
		
		JButton btnViewStatus = new JButton("View status");
		btnViewStatus.setBounds(133, 66, 150, 25);
		frame.getContentPane().add(btnViewStatus);
		
		JButton btnViewCatalog = new JButton("View catalog");
		btnViewCatalog.setBounds(133, 107, 150, 25);
		frame.getContentPane().add(btnViewCatalog);
		
		JButton btnViewFlightDynamics = new JButton("View flight dynamics");
		btnViewFlightDynamics.setBounds(119, 150, 179, 25);
		frame.getContentPane().add(btnViewFlightDynamics);
		

		
		btnViewStatus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(() -> new DroneStatus().setVisible(true));
			}
		});
		
        btnViewCatalog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	SwingUtilities.invokeLater(() -> new DroneCatalog().setVisible(true));
            }
        });
		
		
        btnViewFlightDynamics.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		SwingUtilities.invokeLater(() -> new FlightDynamics().setVisible(true));
        	}
        });
        
        
        
	}
}
