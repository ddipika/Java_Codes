/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The whole stock of the shop.
 *
 * @author floriment
 */
public class Stock {

	// Maps product IDs to the stocks of these individual products
	protected Map<Long, ProductStock> actualStock;

	public Stock() {
		actualStock = new ConcurrentHashMap<>();
	}

	public Map<Long, ProductStock> getActualStock() {
		return actualStock;
	}

	public void setActualStock(Map<Long, ProductStock> actualStock) {
		this.actualStock = actualStock;
	}

	public void addItemToStock(Long productId, ProductStock productStock) {
		this.actualStock.put(productId, productStock);
	}

	public void removeItemFromStock(Long productId) {
		if (this.actualStock.containsKey(productId))
			this.actualStock.remove(productId);
	}

}
