package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Account;

/**
 * A panel with info about one account
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
@SuppressWarnings("serial")
public class AccountInfoPanel extends JPanel {

	// The label with information on that account
	private JLabel infoLabel;
	// The button to start a snapshot from that account
	private JButton snapShotButton;

	private Account account;

	/**
	 * Creates a new account info panel.
	 * 
	 * @param account
	 *            the account to display info for
	 */
	public AccountInfoPanel(final Account account) {
		this.account = account;

		// Layout
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		infoLabel = new JLabel(account.getName() + " " + account.getAmount()
				+ " €");
		snapShotButton = new JButton("Snapshot");
		snapShotButton.setEnabled(false);

		snapShotButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.printf("Account %s hat started snapshot.\n",
						account.getName());
				account.startSnapshot();
			}
		});

		this.add(infoLabel);
		this.add(snapShotButton);

		// Set a border
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createCompoundBorder(
						BorderFactory.createEmptyBorder(10, 10, 10, 10),
						BorderFactory.createLineBorder(Color.BLACK)),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
	}

	/**
	 * Refresh the amount in the info panel.
	 */
	public void refresh() {
		infoLabel
				.setText((account.getName() + " " + account.getAmount() + " €"));
	}

	/**
	 * Activates the snapshot button
	 */
	public void activateSnapshotButton() {
		snapShotButton.setEnabled(true);
	}
}
