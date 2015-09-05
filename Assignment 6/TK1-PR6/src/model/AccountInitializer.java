/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class initializes our three accounts.
 
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class AccountInitializer {

	// The upper and lower bound for the initial balance of an account
	private static final int LOWER_BOUND = 500;
	private static final int UPPER_BOUND = 1000;
	private static Random rand = new Random();

	// A list of the accounts
	private static List<Account> accounts = new ArrayList<>();

	/**
	 * @return the accounts
	 */
	public static List<Account> getAccounts() {
		return accounts;
	}

	/**
	 * @return a random initial balance for an account.
	 */
	public static int getRandomAmount() {
		return rand.nextInt(UPPER_BOUND - LOWER_BOUND) + LOWER_BOUND;
	}

	/**
	 * Creates three accounts and for each of them two outgoing communication
	 * channels to communicate with each other.
	 */
	public static void initAccounts() {
		Account account1 = new Account("k1", getRandomAmount(), 9999);
		Account account2 = new Account("k2", getRandomAmount(), 9998);
		Account account3 = new Account("k3", getRandomAmount(), 9997);

		CommunicationChannel c12 = new CommunicationChannel(9998);
		CommunicationChannel c13 = new CommunicationChannel(9997);
		account1.addCommunicationChannel("k2", c12);
		account1.addCommunicationChannel("k3", c13);

		CommunicationChannel c21 = new CommunicationChannel(9999);
		CommunicationChannel c23 = new CommunicationChannel(9997);
		account2.addCommunicationChannel("k1", c21);
		account2.addCommunicationChannel("k3", c23);

		CommunicationChannel c31 = new CommunicationChannel(9999);
		CommunicationChannel c32 = new CommunicationChannel(9998);
		account3.addCommunicationChannel("k1", c31);
		account3.addCommunicationChannel("k2", c32);

		accounts.add(account1);
		accounts.add(account2);
		accounts.add(account3);
	}

	/**
	 * Starts the simulation by starting a thread for each account.
	 */
	public static void startSimulation() {
		for (Account account : accounts) {
			new Thread(account).start();
		}
	}
}
