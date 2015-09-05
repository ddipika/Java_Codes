package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import model.Account;
import model.AccountInitializer;

/**
 * The GUI's main window.
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	/**
	 * The main method showing the GUI.
	 */
	public static void main(String[] args) {
		MainWindow window = new MainWindow();
		window.setVisible(true);
	}

	// Dimensions of the window.
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 400;

	/**
	 * A big text area for all information being printed.
	 */
	private JTextArea infoArea;

	/**
	 * Whether the simulation has been started.
	 */
	private boolean started = false;

	/**
	 * A list of the account info panels.
	 */
	private List<AccountInfoPanel> infoPanels = new ArrayList<>();

	/**
	 * Constructor for the window.
	 */
	public MainWindow() {
		// Set window title
		setTitle("Snapshot Chandy/Lamport");

		// Exit VM when closing
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

		// A TextArea for all the info
		infoArea = new JTextArea();
		Border infoBorder = BorderFactory.createLineBorder(Color.BLACK);
		infoArea.setBorder(BorderFactory.createCompoundBorder(infoBorder,
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		infoArea.setEditable(false);
		JScrollPane infoScrollPane = new JScrollPane(infoArea);
		infoScrollPane.setViewportView(infoArea);
		mainPanel.add(infoScrollPane);

		// A area for the three account info panels
		JPanel accountsPanel = new JPanel();
		accountsPanel.setLayout(new BoxLayout(accountsPanel, BoxLayout.Y_AXIS));

		// Init accounts
		AccountInitializer.initAccounts();
		List<Account> accounts = AccountInitializer.getAccounts();

		// Add AccountInfoPanels
		for (Account account : accounts) {
			AccountInfoPanel accPanel = new AccountInfoPanel(account);
			accountsPanel.add(accPanel);
			infoPanels.add(accPanel);
			
			// Add references to GUI in the account
			account.setMainWindow(this);
			account.setAccountPanel(accPanel);
		}

		mainPanel.add(accountsPanel);

		// A button to start the simulation
		final JButton activateSimulationButton = new JButton("Start simulation");
		activateSimulationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				activateSimulationButton.setEnabled(false);
				if (!started) {
					started = true;
					AccountInitializer.startSimulation();
					for (AccountInfoPanel infoPanel : infoPanels) {
						infoPanel.activateSnapshotButton();
					}
				}
			}
		});
		
		accountsPanel.add(activateSimulationButton);

		mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		this.add(mainPanel);
		this.pack();
		
		// Print total balance at the beginning
		printMessage("Total balance: " + Account.getTotalBalance());
	}

	/**
	 * Print a message in a new line to the info panel.
	 * 
	 * @param message
	 *            the message.
	 */
	public void printMessage(String message) {
		System.out.println(message);
		infoArea.append(message + "\n");
	}
}
