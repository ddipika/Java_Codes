/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server.data;

/**
 * A user in the server model.
 *
 * @author floriment
 */
public class User {

	// Only attribute of a user
	private ShoppingCart cart;

	public User() {
		cart = new ShoppingCart();
	}

	public ShoppingCart getCart() {
		return cart;
	}

	public void clearCart() {
		cart.actualStock.clear();
	}

}
