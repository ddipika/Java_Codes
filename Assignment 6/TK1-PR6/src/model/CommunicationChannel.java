/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * One outgoing communication channel, that implements a queue of messages to
 * transfer with a delay.
 
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class CommunicationChannel implements Runnable {

	// Lower and upper bound for interval between transferring the next message.
	// This simulates the transmission delay, as messages are sent less
	// frequently (only all 5-10s).
	private static final int LOWER_BOUND = 1000;
	private static final int UPPER_BOUND = 5000;
	private static Random rand = new Random();

	/**
	 * @return a random transmission delay.
	 */
	public static int getRandomDelay() {
		return rand.nextInt(UPPER_BOUND - LOWER_BOUND) + LOWER_BOUND;
	}

	// The queue of messages.
	private Queue<String> messages = new ConcurrentLinkedQueue<>(
			new LinkedList<>());

	// The port to send datagrams to.
	private int port_dst;

	// The client datagram socket
	private DatagramSocket clientSocket;

	/**
	 * Constructs a CommunicationChannel which will communicate with an account
	 * that listens on the port port.
	 * 
	 * @param port
	 */
	public CommunicationChannel(int port) {
		this.port_dst = port;
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException ex) {
			Logger.getLogger(CommunicationChannel.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	/**
	 * Adds a message to the queue.
	 * 
	 * @param message
	 */
	public void addMessageToQueue(String message) {
		messages.add(message);
	}

	@Override
	public void run() {

		// Infinite loop
		while (true) {
			try {
				// - check if messages is empty
				if (!messages.isEmpty()) {
					InetAddress IPAddress = InetAddress.getByName("localhost");
					byte[] sendData;

					// - poll a message
					String value = messages.poll();

					// construct packet to send
					sendData = value.getBytes();
					final DatagramPacket sendPacket = new DatagramPacket(
							sendData, sendData.length, IPAddress, port_dst);

					// - sleep for a random time
					try {
						Thread.sleep(getRandomDelay());
						// - send a message to the outgoing port
						clientSocket.send(sendPacket);
					} catch (InterruptedException ex) {
						Logger.getLogger(CommunicationChannel.class.getName())
								.log(Level.SEVERE, null, ex);
					}
				}

			} catch (IOException e) {
				System.out.println("Exception: " + e.getMessage());
			}
		}
	}
}
