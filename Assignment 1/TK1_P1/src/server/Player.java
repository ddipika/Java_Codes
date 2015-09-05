/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import impl.IGameClient;

/**
 * Scoring the points for players
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class Player {

	// The client
	public IGameClient client;
	// points of player
	private int points = 0;

	/**
	 * A Player.
	 * 
	 * @param c
	 *            the client.
	 */
	public Player(IGameClient c) {
		this.client = c;
	}

	/**
	 * Increment score of player.
	 */
	public void playerScored() {
		this.points++;
	}

	/**
	 * Get the current score of player.
	 * 
	 * @return the score.
	 */
	public int getPoints() {
		return this.points;
	}

}
