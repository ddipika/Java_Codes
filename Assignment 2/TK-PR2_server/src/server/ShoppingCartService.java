/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import server.data.ShoppingCart;
import server.data.Stock;

/**
 * The SOAP interface for our server.
 *
 * @author floriment
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ShoppingCartService {
	
	// if 0 passed, new id generated. New id returned
	@WebMethod
	long identify(long id);
	
	@WebMethod 
	Stock getStock();
	
	@WebMethod 
	ShoppingCart getShoppingCart(long id);
	
	// Buy 1 of this product
	@WebMethod 
	void addProductToShoppingCart(long id, long productID);
	
	@WebMethod 
	void clearShoppingCart(long id);	
	
	@WebMethod 
	boolean buy(long id);
}
