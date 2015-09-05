package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import server.data.ShoppingCart;
import server.data.Stock;

/**
 * The REST interface for our server. It uses the SOAP service implementation
 * (accessed via {@code Main.service}) and augments only with JSON
 * (de)serialization.
 * 
 * @author Satia
 */
@Path("/cart")
public class ShoppingCartRest {

	@GET
	@Path("/identify/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String identify(@PathParam("id") long id) {
		return "" + Main.service.identify(id);
	}

	@GET
	@Path("/stock/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStock() {
		Stock stock = Main.service.getStock();
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		return gson.toJson(stock);
	}

	@GET
	@Path("/shoppingCart/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getShoppingCart(@PathParam("id") long id) {
		ShoppingCart shoppingCart = Main.service.getShoppingCart(id);
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		return gson.toJson(shoppingCart);

	}

	// Buy 1 of this product
	@GET
	@Path("/addProductToShoppingCart/{id}/{pid}")
	@Produces(MediaType.TEXT_PLAIN)
	public String addProductToShoppingCart(@PathParam("id") long id,
			@PathParam("pid") long productID) {
		Main.service.addProductToShoppingCart(id, productID);
		return "";
	}

	@GET
	@Path("/clearShoppingCart/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String clearShoppingCart(@PathParam("id") long id) {
		Main.service.clearShoppingCart(id);
		return "";
	}

	@GET
	@Path("/buy/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String buy(@PathParam("id") long id) {
		return Main.service.buy(id) + "";
	}
}
