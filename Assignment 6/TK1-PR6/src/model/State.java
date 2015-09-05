/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A state that consists of the amount recorded and messages recorded on the
 * incoming channels.

 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class State {

	// The amount
	private int amount;
	// Objects managing the recorded messages
	private Map<String, Recordings> recordings = new ConcurrentHashMap<>();
	
	/**
	 * Create a State which will hold the recorded state which is the amount and
	 * all recording of the incoming channels for a specific account.
	 */
	public State() {

	}
	
	// Getters and setters
	public Map<String, Recordings> getRecordings() {
		return recordings;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

}
