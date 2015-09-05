
package server;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the server package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: server
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Stock }
     * 
     */
    public Stock createStock() {
        return new Stock();
    }

    /**
     * Create an instance of {@link Stock.ActualStock }
     * 
     */
    public Stock.ActualStock createStockActualStock() {
        return new Stock.ActualStock();
    }

    /**
     * Create an instance of {@link Product }
     * 
     */
    public Product createProduct() {
        return new Product();
    }

    /**
     * Create an instance of {@link ShoppingCart }
     * 
     */
    public ShoppingCart createShoppingCart() {
        return new ShoppingCart();
    }

    /**
     * Create an instance of {@link ProductStock }
     * 
     */
    public ProductStock createProductStock() {
        return new ProductStock();
    }

    /**
     * Create an instance of {@link Stock.ActualStock.Entry }
     * 
     */
    public Stock.ActualStock.Entry createStockActualStockEntry() {
        return new Stock.ActualStock.Entry();
    }

}
