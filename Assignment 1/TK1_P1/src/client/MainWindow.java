/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * The GUI's main window.
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The main method showing the GUI, asking for the player's name and then
	 * starting a client.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MainWindow window = new MainWindow();
		window.setVisible(true);
	}

	// Some constant values shared by client and server
	
	public static final int WINDOW_WIDTH = 500;
	public static final int WINDOW_HEIGHT = 400;
	public static final int IMG_WIDTH = 30;
	public static final int IMG_HEIGHT = 29;

	// The fly image
	private BufferedImage flyImage;

	// Position of the fly (invalid at beginning)
	private int x = WINDOW_WIDTH;
	private int y = WINDOW_HEIGHT;

	// The client
	private GameClient gameClient;

	// The table for the points
	JTable pointsTable;

	/**
	 * Constructor for the window.
	 */
	public MainWindow() {
		super("Fly Hunting - 'yeah!'");

		// Dispose when closing
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
                
                this.setResizable(false);
                
		// Load the fly image
		URL imageURL = ClassLoader.getSystemResource("fliege-t20678.jpg");
		try {
			flyImage = ImageIO.read(imageURL);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		JPanel flyPanel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(flyImage, x, y, null);
			}
		};

		// Load curser image
		Image cursorImage = null;
		URL killingmachineURL = ClassLoader.getSystemResource("killingmachine.png");
		try {
			cursorImage = ImageIO.read(killingmachineURL);
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
		
		// Attach image over cursor
		final Point hotspot = new Point(0, 0);
		final String cursorName = "My Cursor";
		flyPanel.setCursor(getToolkit().createCustomCursor(cursorImage,
				hotspot, cursorName));

		flyPanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent me) {
			}

			@Override
			public void mousePressed(MouseEvent me) {
				// If the click is within the size of image then the fly is being
				// killed
				int clickedX = me.getX();
				int clickedY = me.getY();

				System.out.println("x:" + x + " y:" + y + " clickedX:"
						+ clickedX + " clickedY:" + clickedY);

				if (clickedX >= x && clickedX < x + IMG_WIDTH && clickedY >= y
						&& clickedY < y + IMG_HEIGHT) {
					gameClient.flyKilled();
				}
			}

			@Override
			public void mouseReleased(MouseEvent me) {
			}

			@Override
			public void mouseEntered(MouseEvent me) {
			}

			@Override
			public void mouseExited(MouseEvent me) {
			}
		});

		flyPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		this.add(flyPanel);

		// TODO put table at top
		pointsTable = new JTable(1, 2);
		pointsTable.setValueAt("Name", 0, 0);
		pointsTable.setValueAt("Points", 0, 1);

		this.add(pointsTable);

		this.pack();

		// Get the name
		String name = JOptionPane.showInputDialog(this, "Enter your name");
		if (name == null) {
			// Exit if no name given.
			System.exit(0);
		}

		try {
			// Establish the connection
			// A random 3-digit number to minimize chance of name clashes
			gameClient = new GameClient(this, name
					+ (int) (Math.random() * 900 + 100));
		} catch (RemoteException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "No connection to server.");
			System.exit(-1);
		}
	}

	@Override
	public void dispose() {
		// Logout and shutdown VM if the window is closed.
		gameClient.logout();
		System.exit(0);
	}

	/**
	 * Called to repaint the whole GUI (including the fly at its new position),
	 * @param x new x position of the fly
	 * @param y new y position of the fly
	 */
	public void repaintFly(int x, int y) {
		this.x = x;
		this.y = y;
		this.repaint();
	}

	/**
	 * Update the points of players in score table.
	 * 
	 * @param points
	 *            Point scored by the player
	 */
	public void updatePlayerPoints(Map<String, Integer> points) {
		DefaultTableModel dtm = (DefaultTableModel) pointsTable.getModel();
		dtm.setRowCount(1);

		for (Map.Entry<String, Integer> entry : points.entrySet()) {
			String name = entry.getKey();
			// Cut the 3 random digits off
			dtm.addRow(new Object[] { name.substring(0, name.length() - 3),
					entry.getValue() });
		}

		pointsTable.setModel(dtm);
	}
}
