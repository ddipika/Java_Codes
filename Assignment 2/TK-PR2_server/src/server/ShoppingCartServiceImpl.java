/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.Random;
import javax.jws.WebService;
import server.data.ShoppingCart;
import server.data.Stock;

/**
 * Implementation of the SOAP ShoppingCartService interface.
 *
 * @author floriment
 */
@WebService(endpointInterface = "server.ShoppingCartService")
public class ShoppingCartServiceImpl implements ShoppingCartService {

	/**
	 * To generate pseudo-random IDs for the clients.
	 */
    private Random rand = new Random();

    @Override
    public long identify(long id) {
        // -1 for the first time
        long answer = 0;
        if (id == -1) {
        	// A random ID
            answer = rand.nextLong();
            Main.server.createNewUserWithShoppingCart(answer);
        } else {
            answer = id;
            //make sure if the user is in server
            if (!Main.server.checkIfUserExists(id)) {
                Main.server.createNewUserWithShoppingCart(id);
            }
        }
        return answer;
    }

    @Override
    public Stock getStock() {
        return Main.server.getStock();
    }

    @Override
    public ShoppingCart getShoppingCart(long id) {
        try {
            return Main.server.getShoppingCart(id);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    @Override
    public void addProductToShoppingCart(long id, long productID) {
        try {
            Main.server.addProductToShoppingCart(id, productID);
        } catch (Exception ex) {
            System.out.println("Client request to add a product to shopping card failed: no existing user");

        }
    }

    @Override
    public void clearShoppingCart(long id) {
        try {
            Main.server.clearShoppingCart(id);
        } catch (Exception ex) {
            System.out.println("Client request to clear a shoping cart of a non-existing user");
        }
    }

    @Override
    public boolean buy(long id) {
        return Main.server.buy(id);
    }

}
