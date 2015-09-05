/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.data;

/**
 * The stock of a certain product. This a tuple of product and amount.
 * 
 * @author floriment
 */
public class ProductStock {

	private Product product;
	private int amount;

	public ProductStock() {

	}

	public ProductStock(Product product, int amount) {
		this.product = product;
		this.amount = amount;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void decreaseAmount() {
		this.amount--;
	}

	public void decreaseAmount(int amount) {
		this.amount = this.amount - amount;
	}

	public void increaseAmount() {
		this.amount++;
	}

}
