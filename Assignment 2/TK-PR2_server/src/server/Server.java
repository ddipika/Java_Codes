/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import server.data.Product;
import server.data.ProductStock;
import server.data.ShoppingCart;
import server.data.Stock;
import server.data.User;

/**
 * The central class for managing the shop on the server side.
 *
 * @author floriment
 */
public class Server {

	// The users
	private final Map<Long, User> users;
	// Everything in stock
	private final Stock stock;

	/**
	 * Public constructor.
	 */
	public Server() {
		this.users = new HashMap<>();
		this.stock = new Stock();
		// Using sequential IDs for the products!
		stock.addItemToStock(1l, new ProductStock(new Product(1l, "Apple", 23),
				4));
		stock.addItemToStock(2l, new ProductStock(new Product(2l, "Banana", 3),
				34));
		stock.addItemToStock(3l, new ProductStock(new Product(3l, "Shirt", 3),
				23));
	}

	/**
	 * Create a new user with the given ID.
	 * 
	 * @param id
	 *            the user's ID
	 */
	public void createNewUserWithShoppingCart(long id) {
		User user = new User();
		users.put(id, user);
	}

	/**
	 * @param id
	 *            the user's ID
	 * @return if the user exists.
	 */
	public boolean checkIfUserExists(long id) {
		return users.containsKey(id);
	}

	/**
	 * @return the stock.
	 */
	public Stock getStock() {
		return stock;
	}

	/**
	 * Get the shopping cart of a user.
	 * 
	 * @param id
	 *            the user's ID
	 * @return his/her shopping cart
	 * @throws Exception
	 *             if the user does not exist.
	 */
	public ShoppingCart getShoppingCart(long id) throws Exception {
		if (users.containsKey(id)) {
			return users.get(id).getCart();
		} else {
			throw new Exception("Non existing user");
		}
	}

	/**
	 * Add a product to a user's shopping cart
	 * 
	 * @param id
	 *            the user's ID
	 * @param productID
	 *            the ID of the product to add
	 * @throws Exception
	 *             if the user does not exist.
	 */
	public void addProductToShoppingCart(long id, long productID)
			throws Exception {
		if (users.containsKey(id)) {
			if (users.get(id).getCart().getActualStock().containsKey(productID)) {
				users.get(id).getCart().getActualStock().get(productID)
						.increaseAmount();
			} else {
				ProductStock productStock = new ProductStock(stock
						.getActualStock().get(productID).getProduct(), 1);
				users.get(id).getCart().getActualStock()
						.put(productID, productStock);
			}
		} else {
			throw new Exception("No such user");
		}
	}

	/**
	 * Clears a user's shopping cart.
	 * 
	 * @param id
	 *            the user's ID
	 * @throws Exception
	 *             if the user does not exist.
	 */
	public void clearShoppingCart(long id) throws Exception {
		if (users.containsKey(id)) {
			users.get(id).clearCart();
		} else {
			throw new Exception("No such user");
		}
	}

	/**
	 * Buys the content of the shopping cart.
	 * 
	 * @param id
	 *            the ID of the buying user.
	 * @return if it was successful.
	 */
	public synchronized boolean buy(long id) {
		boolean answer = true;
		List<Long> idsToRemove = new LinkedList<>();
		for (Map.Entry<Long, ProductStock> entrySet : users.get(id).getCart()
				.getActualStock().entrySet()) {
			Long key = entrySet.getKey();
			ProductStock productStock = entrySet.getValue();
			if (productStock.getAmount() > stock.getActualStock().get(key)
					.getAmount()) {
				productStock.setAmount(stock.getActualStock().get(key)
						.getAmount());
				answer = false;
				if (productStock.getAmount() == 0)
					idsToRemove.add(key);
			}
		}
		for (Long idToRemove : idsToRemove) {
			users.get(id).getCart().getActualStock().remove(idToRemove);
		}
		if (answer) {
			for (Map.Entry<Long, ProductStock> entrySet : users.get(id)
					.getCart().getActualStock().entrySet()) {
				Long key = entrySet.getKey();
				ProductStock productStock = entrySet.getValue();
				stock.getActualStock().get(key)
						.decreaseAmount(productStock.getAmount());
			}
			users.get(id).clearCart();
		}
		return answer;
	}

}
