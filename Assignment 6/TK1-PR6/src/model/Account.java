/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import gui.AccountInfoPanel;
import gui.MainWindow;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * One account.
 
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class Account implements Runnable {

	// Lower an upper bound for the time to sleep
	// between two outgoing transfers
	private static final int LOWER_BOUND = 5000;
	private static final int UPPER_BOUND = 10000;
	private static Random rand = new Random();

	// The total balance of all accounts, calculated once at the beginning
	private static AtomicInteger totalBalance = new AtomicInteger(0);
	/**
	 * The total balances estimated for each snapshot (of each process). They
	 * should all be equal to {@code totalBalance}
	 * */
	private static Map<String, Map<Integer, AtomicInteger>> totalBalances = new ConcurrentHashMap<>();

	/**
	 * @return the initially estimated total balance
	 */
	public static int getTotalBalance() {
		return totalBalance.get();
	}

	/**
	 * @return a random delay to sleep between two outgoing transfers.
	 */
	public static int getRandomDelay() {
		return rand.nextInt(UPPER_BOUND - LOWER_BOUND) + LOWER_BOUND;
	}

	// The port number for this account
	private int port;

	// The name of this account
	private String name;

	// The current balance
	private Integer amount;

	// A reference to the main window
	private MainWindow mainWindow;
	// A reference to the info panel for this account
	private AccountInfoPanel accountPanel;

	// All the states saved by this process for all requested snapshots
	private Map<String, Map<Integer, State>> states = new ConcurrentHashMap<>();

	// A counter for how many snapshots this process has done
	private AtomicInteger snapshotCounter = new AtomicInteger(0);

	// A socket to receive transfers
	private DatagramSocket serverSocket;

	// The outgoing communication channels
	private Map<String, CommunicationChannel> channels = new HashMap<String, CommunicationChannel>();

	/**
	 * It starts an account with the specified name, with the initialAmount
	 * which listens on the specified port.
	 * 
	 * @param name
	 * @param initialAmount
	 * @param port
	 */
	public Account(String name, int initialAmount, int port) {
		this.name = name;
		this.amount = initialAmount;
		this.port = port;
		states.put(name, new ConcurrentHashMap<>());
		try {
			serverSocket = new DatagramSocket(this.port);
		} catch (SocketException ex) {
			Logger.getLogger(Account.class.getName()).log(Level.SEVERE, null,
					ex);
		}

		// Add to total balance
		totalBalance.addAndGet(initialAmount);
		totalBalances.put(name, new ConcurrentHashMap<>());
	}

	// Getters and Setters
	public void setMainWindow(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	public void setAccountPanel(AccountInfoPanel accountPanel) {
		this.accountPanel = accountPanel;
	}

	public int getAmount() {
		return amount;
	}

	public String getName() {
		return name;
	}

	/**
	 * Adds an outgoing communication channel.
	 * 
	 * @param key
	 *            the name of the other account
	 * @param comChannel
	 *            the channel
	 */
	public void addCommunicationChannel(String key,
			CommunicationChannel comChannel) {
		channels.put(key, comChannel);
		states.put(key, new ConcurrentHashMap<>());
	}

	@Override
	public void run() {
		for (Map.Entry<String, CommunicationChannel> entrySet : channels
				.entrySet()) {
			String key = entrySet.getKey();
			CommunicationChannel comChannel = entrySet.getValue();

			// for every communication channel we start the threads which
			// maintain a queue and pull messages out within a certain delay
			new Thread(comChannel).start();

			// we also create a thread for each outgoing channel that is going
			// to transfer money from this account to the two other accounts
			// via these channels.
			new Thread() {

				@Override
				public void run() {
					while (true) {
						try {
							// Sleep a random time
							Thread.sleep(getRandomDelay());
						} catch (InterruptedException ex) {
							Logger.getLogger(Account.class.getName()).log(
									Level.SEVERE, null, ex);
						}
						// Get a random amount to transfer, between 5 and 60 €
						int randomAmount = 5 + rand.nextInt(55);
						synchronized (amount) {
							// Assure that the amount cannot become negative,
							// otherwise abort transfer
							if (getAmount() - randomAmount > 0) {
								synchronized (channels) {
									// Transfer the money
									transfer(key, randomAmount);
								}
							}
						}

					}
				}

			}.start();
		}

		// after that we start the infinite loop that is going to receive
		// packets
		// on the specified port and distinguishes between markers and transfer
		// messages
		while (true) {
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			try {
				serverSocket.receive(receivePacket);
			} catch (IOException ex) {
				Logger.getLogger(Account.class.getName()).log(Level.SEVERE,
						null, ex);
			}

			// Extract the message from the datagram.
			String message = new String(receivePacket.getData());
			System.out.println("MSGRECV: " + message);

			// Split by ":"
			String[] messageSplited = message.split(":");

			if (messageSplited[0].equals("M")) {
				// Marker message have the format
				// "M:sender:initiator:sequenceNumber"
				markerReceived(messageSplited[1], messageSplited[2],
						new Integer(messageSplited[3].trim()));
			} else {
				// Transfer message have the format "T:sender:amount"
				int value = new Integer(messageSplited[2].trim());

				// Potentially record any transfer messages, if recording is
				// enabled
				recordMessage("+ " + value + " €", messageSplited[1]);
				messageReceived(messageSplited[1], value);
			}
		}
	}

	/**
	 * We received a transfer message from the accountName with the amount value
	 * 
	 * @param accountName
	 * @param value
	 */
	private void messageReceived(String accountName, int value) {
		// Increase balance
		synchronized (amount) {
			this.amount += value;
		}

		this.accountPanel.refresh();
		this.mainWindow.printMessage("Received: " + accountName + " => "
				+ this.getName() + "(" + value + " €). New account balance of "
				+ this.getName() + "=" + amount);
	}

	/**
	 * Transfer the amount value to the specified id account. It only pushes a
	 * message to the outgoing CommunicationChannel responsible for that
	 * account.
	 * 
	 * @param id
	 * @param value
	 */
	private void transfer(String id, int value) {
		// Decrease balance (syncronized already used in Thread.run() calling
		// this method)
		this.amount -= value;

		this.accountPanel.refresh();
		this.mainWindow.printMessage("Sending: " + this.getName() + " => " + id
				+ "( - " + value + " €). New account balance of "
				+ this.getName() + "=" + amount);

		String message = "T:" + getName() + ":" + value;
		System.out.println("MSGSENT: " + message);

		// Add the message to the queue
		channels.get(id).addMessageToQueue(message);
	}

	/**
	 * We received a marker from the accountName, with the initiator specified
	 * and markerNumber which keeps track of the rounds.
	 * 
	 * @param accountName
	 * @param initiator
	 * @param markerNumber
	 */
	private void markerReceived(String accountName, String initiator,
			int markerNumber) {
		mainWindow.printMessage(getName() + " marker received from: "
				+ accountName + " with initiator:" + initiator
				+ " and marker number:" + markerNumber);

		if (!states.get(initiator).containsKey(markerNumber)) {
			// If this is the first time, we receive that marker message:

			synchronized (channels) {
				// saving the state: we create a new state
				State state = new State();
				state.setAmount(amount);

				// And save it in the map for all states
				states.get(initiator).put(markerNumber, state);

				// Also create an atomic integer to hold
				// the total balance for that recorded snapshot
				synchronized (totalBalances.get(initiator)) {
					if (totalBalances.get(initiator).get(markerNumber) == null) {
						totalBalances.get(initiator).put(markerNumber,
								new AtomicInteger(0));
					}
				}

				mainWindow.printMessage(getName() + " recorded state ("
						+ amount + ") for snapshot with initiator:" + initiator
						+ " and marker number:" + markerNumber);

				for (Map.Entry<String, CommunicationChannel> entry : channels
						.entrySet()) {
					String recipient = entry.getKey();

					// Only start recording for all the channels
					// except the one we just got the marker message on
					if (!accountName.equals(recipient)) {
						Recordings recs = new Recordings();
						recs.setRecord(true);

						state.getRecordings().put(recipient, recs);

						mainWindow.printMessage(getName()
								+ " starts recording input channel from "
								+ recipient);
					}

					// Send marker messages on all outgoing channels
					sendMarker(recipient, initiator, markerNumber);
				}

			}
		} else {
			// If this is NOT the first time, we receive that marker message:

			// Stop recording on that incoming channel, we just got this
			// marker message from.
			State state = states.get(initiator).get(markerNumber);
			state.getRecordings().get(accountName).setRecord(false);
			mainWindow.printMessage(getName()
					+ " stops recording input channel from " + accountName);

			// Check if we're finished (that means, we are not listening on any
			// incoming channel any more).
			Map<String, Recordings> allRec = state.getRecordings();
			boolean answer = true;
			for (Map.Entry<String, Recordings> entrySet : allRec.entrySet()) {
				Recordings recording = entrySet.getValue();
				if (recording.isRecord()) {
					answer = false;
					break;
				}
			}
			// In that case: Print some information about the recorded state and messages.
			if (answer) {

				// Add to total balance for that recorded state
				int totalBalance = totalBalances.get(initiator)
						.get(markerNumber).addAndGet(state.getAmount());

				StringBuilder sb = new StringBuilder();
				sb.append(getName()).append(" recorded state for snapshot ");
				sb.append(markerNumber);
				sb.append(" from ");
				sb.append(initiator).append(":\n");

				sb.append("value=");
				sb.append(state.getAmount());
				sb.append("\n");

				for (Map.Entry<String, Recordings> entrySet : allRec.entrySet()) {
					String channelName = entrySet.getKey();
					Recordings recordings = entrySet.getValue();
					sb.append("channel from ");
					sb.append(channelName);
					sb.append(" = ");
					sb.append(recordings.getMessages());
					sb.append("\n");
				}

				sb.append("Total recorded balance so far: ");
				sb.append(totalBalance - state.getAmount());
				sb.append(" + ").append(state.getAmount());
				for (Map.Entry<String, Recordings> entrySet : allRec.entrySet()) {
					Recordings recordings = entrySet.getValue();

					for (String msg : recordings.getMessages()) {
						sb.append(msg);

						totalBalance = totalBalances
								.get(initiator)
								.get(markerNumber)
								.addAndGet(
										Integer.parseInt(msg.substring(2,
												msg.length() - 2)));
					}
				}
				sb.append(" = ").append(totalBalance);
				sb.append("\n");

				mainWindow.printMessage(sb.toString());
			}
		}
	}

	/**
	 * This method is used to simulate the receiving of a marker from itself, in
	 * order to start a snapshot.
	 */
	public void startSnapshot() {
		markerReceived(getName(), getName(), snapshotCounter.incrementAndGet());
	}

	/**
	 * Send a marker message to the account with identifier id, including the
	 * initiator and the markerNumber
	 * 
	 * @param id
	 * @param initiator
	 * @param markerNumber
	 */
	private void sendMarker(String id, String initiator, int markerNumber) {
		// Add message to the queue
		channels.get(id).addMessageToQueue(
				"M:" + getName() + ":" + initiator + ":" + markerNumber);

		mainWindow.printMessage("marker sent from: " + getName() + " to: " + id
				+ " with initiator:" + initiator + " and marker number:"
				+ markerNumber);

	}

	/**
	 * This method is used to keep track of messages when recording is enabled
	 * 
	 * @param message
	 * @param sender
	 */
	private void recordMessage(String message, String sender) {
		// Go through all states that are being recorded
		for (Map.Entry<String, Map<Integer, State>> entry : states.entrySet()) {
			Map<Integer, State> states = entry.getValue();
			for (Map.Entry<Integer, State> entry2 : states.entrySet()) {

				State state = entry2.getValue();
				// Get the Recordings object for that sender
				Recordings rec = state.getRecordings().get(sender);
				// If the recording on that incoming channel is enabled...
				if (rec != null && rec.isRecord()) {
					// ... add the message to the Recordings
					rec.getMessages().add(message);
				}
			}

		}
	}

}
