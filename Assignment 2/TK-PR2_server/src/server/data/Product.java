/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server.data;

/**
 * A product with ID, Name and price.
 *
 * @author floriment
 */
public class Product {

	private long id;
	// Name of the product
	private String name;
	// Price in cents
	private int price;

	public Product(long id) {
		this.id = id;
	}

	public Product(long id, String name, int price) {
		this.id = id;
		this.name = name;
		this.price = price;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Product) {
			if (this.id == ((Product) obj).id)
				return true;
			else
				return false;
		}
		return super.equals(obj);
	}

}
