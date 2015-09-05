/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 * This object manages the recordings on one incoming channel. The messages and
 * whether recording is turned on/off is managed.
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class Recordings {

	// The list of messages
	private List<String> messages = new ArrayList<>();
	// If recording is turned on/off
	private boolean record = false;
	
	/**
	 * Recordings holds all the messages recorded to an incoming channel when
	 * the recording is enabled
	 */
	public Recordings() {

	}

	// Getters and setters
	public List<String> getMessages() {
		return messages;
	}

	public boolean isRecord() {
		return record;
	}

	public void setRecord(boolean record) {
		this.record = record;
	}

}
