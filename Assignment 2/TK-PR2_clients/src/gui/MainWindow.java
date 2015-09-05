/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.Dimension;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import restClient.ShoppingCartRestClient;
import server.ShoppingCart;
import server.ShoppingCartService;
import server.ShoppingCartServiceImplService;
import server.Stock;

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
	 * 
	 * @param args
	 *            either "REST" or "SOAP"
	 */
	public static void main(String[] args) {
		if (args.length != 1
				|| (!args[0].equals("REST") && !args[0].equals("SOAP"))) {
			System.out
					.println("Please provide exactly one argument, 'REST' or 'SOAP'");
			return;
		}
		boolean useSoap = args[0].equals("SOAP");
		MainWindow window = new MainWindow(useSoap);
		window.setVisible(true);
	}

	// Dimensions of the window.
	public static final int WINDOW_WIDTH = 500;
	public static final int WINDOW_HEIGHT = 400;

	public static final String ADD_TO_CART_STR = "<html><font color=\"red\">Add to cart</font></html>";

	// The tables for shop and cart
	JTable shopTable;
	JTable cartTable;
	// The total price label
	JLabel priceLabel;

	// The shop and cart contents
	ShoppingCart shoppingCart;
	Stock stock;

	// The used service
	ShoppingCartService service;

	// The ID of this client
	long id;

	/**
	 * Constructor for the window.
	 * 
	 * @param useSoap
	 *            {@code true} if SOAP should be used, {@code false} if REST
	 *            should be used.
	 */
	public MainWindow(boolean useSoap) {
		super("Shopping cart - " + (useSoap ? "SOAP" : "REST"));

		// Initialize either SOAP or REST and checking connection
		if (useSoap) {
			try {
				ShoppingCartServiceImplService serviceImpl = new ShoppingCartServiceImplService();
				service = serviceImpl.getShoppingCartServiceImplPort();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(MainWindow.this,
						"No connection to SOAP server.");
				System.exit(-1);
			}
		} else {
			service = new ShoppingCartRestClient();
		}

		try {
			id = IDHandler.getID(service, useSoap);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(MainWindow.this, "No connection to "
					+ (useSoap ? "SOAP" : "REST") + " server.");
			System.exit(-1);
		}

		// Exit VM when closing
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		this.setResizable(false);

		JPanel mainPanel = new JPanel();
		mainPanel.add(new JLabel("Shop products"));

		// A refresh button
		JButton refreshShopButton = new JButton("Refresh");
		ActionListener refreshSopAL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				updateStock();
			}
		};
		refreshShopButton.addActionListener(refreshSopAL);
		mainPanel.add(refreshShopButton);

		// Add tables inside of scrollPanes
		ScrollPane firstScrollPane = new ScrollPane();
		firstScrollPane.setBounds(0, 0, 450, 150);

		shopTable = new JTable();
		DefaultTableModel shopTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// Add Click Listener on shopTable
		shopTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTable target = (JTable) e.getSource();
				int shopTablerow = target.getSelectedRow();
				int shopTablecolumn = target.getSelectedColumn();

				// If we're clicking the "Add to cart" cell
				// Only do sth. if we clicked an actual "Add to cart", not an
				// empty cell in that column
				if (shopTablecolumn == 4
						&& target.getModel()
								.getValueAt(shopTablerow, shopTablecolumn)
								.equals(ADD_TO_CART_STR)) {
					long pid = (long) target.getModel().getValueAt(
							shopTablerow, 0);

					// Add 1 product to shopping cart
					service.addProductToShoppingCart(id, pid);
					// Update the cart
					updateCart();

					Stock.ActualStock.Entry cartEntry = findProductInStockOrCart(
							shoppingCart, pid);
					int cartAmount = cartEntry == null ? 0 : cartEntry
							.getValue().getAmount();
					int stockAmount = (int) target.getModel().getValueAt(
							shopTablerow, 3);

					// Remove "Add to cart" if cart.amount(pid) >=
					// shop.amount(pid)
					if (cartAmount >= stockAmount) {
						target.getModel().setValueAt("", shopTablerow,
								shopTablecolumn);
					}
				}
			}
		});

		shopTable.setModel(shopTableModel);
		// The columns are: ID, name, price (for 1 product), available amount,
		// "add to cart" clickable field
		shopTableModel.setColumnCount(5);

		shopTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		shopTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		shopTable.getColumnModel().getColumn(2).setPreferredWidth(60);
		shopTable.getColumnModel().getColumn(3).setPreferredWidth(120);

		// The header row
		shopTableModel.addRow(new Object[] { "<html><b>ID</b></html>",
				"<html><b>Name</b></html>", "<html><b>Price</b></html>",
				"<html><b>Available Amount</b></html>" });

		firstScrollPane.add(shopTable);
		mainPanel.add(firstScrollPane);

		mainPanel.add(new JLabel("Your shopping cart"));

		ScrollPane secondScrollPane = new ScrollPane();
		secondScrollPane.setBounds(0, 0, 450, 150);

		cartTable = new JTable();
		DefaultTableModel cartTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		cartTable.setModel(cartTableModel);
		// The columns are: ID, name, price for the amount of this product in
		// the shopping cart, amount in the cart
		cartTableModel.setColumnCount(4);

		cartTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		cartTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		cartTable.getColumnModel().getColumn(2).setPreferredWidth(100);

		cartTableModel.addRow(new Object[] { "<html><b>ID</b></html>",
				"<html><b>Name</b></html>", "<html><b>Price</b></html>",
				"<html><b>Amount</b></html>" });

		secondScrollPane.add(cartTable);
		mainPanel.add(secondScrollPane);

		// Empty Cart
		ActionListener emptyCartAL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				service.clearShoppingCart(id);
				updateCart();
			}
		};

		// Buy Product
		ActionListener buyProductAL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {

				// Warning for trying to shop empty Cart
				if (cartTable.getRowCount() == 1) {
					JOptionPane.showMessageDialog(MainWindow.this,
							"Your shopping cart is EMPTY.\n"
									+ "To proceed, add at least one \n"
									+ "product in your shopping cart.",
							"Choose a Product to buy.",
							JOptionPane.WARNING_MESSAGE);
				} else {
					// Show Success/Failure
					boolean successful = service.buy(id);
					if (successful) {
						JOptionPane
								.showMessageDialog(
										MainWindow.this,
										"The purchase has been completed.\n"
												+ "The products will be shipped to\n"
												+ "a random address, as we do not have yours.",
										"Your purchase was successful.",
										JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane
								.showMessageDialog(
										MainWindow.this,
										"The purchase could not be completed.\n"
												+ "Some products were not available\n"
												+ "(in the desired amount) any more.\n"
												+ "They have been removed from your cart\n "
												+ "or the amount has been decreased.",
										"Your purchase was Unsuccessful.",
										JOptionPane.ERROR_MESSAGE);
					}
				}
				updateCart();
				updateStock();
			}
		};

		// Panel to hold clear cart button total price and buy button
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

		// For clearing the products from cart
		JButton clearButton = new JButton("Clear Cart");
		clearButton.addActionListener(emptyCartAL);
		bottomPanel.add(clearButton);
		// For Space after Button
		bottomPanel.add(new JLabel("            "));
		// For displaying Total Price
		bottomPanel.add(new JLabel("Total price: "));
		priceLabel = new JLabel();
		bottomPanel.add(priceLabel);
		// For Space before Button
		bottomPanel.add(new JLabel("            "));
		// For buying the product
		JButton buyButton = new JButton("Buy!");
		buyButton.addActionListener(buyProductAL);
		bottomPanel.add(buyButton);
		mainPanel.add(bottomPanel);

		mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		this.add(mainPanel);
		this.pack();

		// Get the contents for cart and shop for the first time
		updateCart();
		updateStock();
	}

	/**
	 * Updates the shopping cart.
	 */
	private void updateCart() {
		shoppingCart = service.getShoppingCart(id);

		// The price sum
		int sum = 0;

		DefaultTableModel cartTableModel = (DefaultTableModel) cartTable
				.getModel();
		cartTableModel.setRowCount(1);
		for (Stock.ActualStock.Entry entry : shoppingCart.getActualStock()
				.getEntry()) {
			cartTableModel.addRow(new Object[] {
					entry.getKey(),
					entry.getValue().getProduct().getName(),
					entry.getValue().getProduct().getPrice()
							* entry.getValue().getAmount(),
					entry.getValue().getAmount() });

			sum += entry.getValue().getProduct().getPrice()
					* entry.getValue().getAmount();
		}

		// Calculate total price!
		priceLabel.setText("" + sum);
	}

	/**
	 * Updates the stock. Must be called after an update to the cart, so that
	 * the "Add to cart"'s are added at the right positions.
	 */
	private void updateStock() {
		stock = service.getStock();

		DefaultTableModel shopTableModel = (DefaultTableModel) shopTable
				.getModel();
		shopTableModel.setRowCount(1);
		// only include "Add to cart", if cart.amount(pid) <
		// stock.amount(pid)
		for (Stock.ActualStock.Entry entry : stock.getActualStock().getEntry()) {
			long pid = entry.getKey();

			Stock.ActualStock.Entry cartEntry = findProductInStockOrCart(
					shoppingCart, pid);
			int cartAmount = cartEntry == null ? 0 : cartEntry.getValue()
					.getAmount();
			int stockAmount = entry.getValue().getAmount();

			String addToCartText = cartAmount < stockAmount ? ADD_TO_CART_STR
					: "";
			shopTableModel.addRow(new Object[] { pid,
					entry.getValue().getProduct().getName(),
					entry.getValue().getProduct().getPrice(), stockAmount,
					addToCartText });
		}
	}

	/**
	 * Finds the entry (KeyValuePair) for a product id in a stock.
	 * 
	 * @param stock
	 *            the stock or cart
	 * @param pid
	 *            the product id
	 * @return the entry
	 */
	private Stock.ActualStock.Entry findProductInStockOrCart(Stock stock,
			long pid) {
		for (Stock.ActualStock.Entry entry : stock.getActualStock().getEntry()) {
			if (entry.getKey() == pid) {
				return entry;
			}
		}
		return null;
	}
}
