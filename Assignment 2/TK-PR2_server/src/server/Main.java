/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import javax.xml.ws.Endpoint;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

/**
 * The main class of the server.
 *
 * @author Satia Herfert
 */
public class Main {

	// Both URIs
	static final String REST_BASE_URI = "http://localhost:8080/shoppingCart/";
	static final String SOAP_BASE_URI = "http://localhost:8090/ws/cart";

	// The SOAP service implementation.
	public static final ShoppingCartService service = new ShoppingCartServiceImpl();
	// The central server module.
	public static final Server server = new Server();

	/**
	 * Start a server.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Endpoint.publish(SOAP_BASE_URI, service);

		try {
			HttpServer restServer = HttpServerFactory.create(REST_BASE_URI);
			restServer.start();
			System.out.println("Press Enter to stop the server. ");
			System.in.read();

			restServer.stop(0);
			System.exit(0);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
