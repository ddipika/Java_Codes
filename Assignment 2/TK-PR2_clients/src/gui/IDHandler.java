/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import server.ShoppingCartService;

/**
 * This class handles the ID of the client(s).
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class IDHandler {

	/**
	 * This method looks into a respective file to get the client ID. There
	 * is one file for SOAP and one for REST. If there is no ID yet, it identifies
	 * with the server, to get a new ID. This is also done if there is an ID
	 * in fact, since the server may have crashed and forgot your ID in the meantime,
	 * which would mean you would get a new ID and your cart data would be lost.
	 * @param service the service object.
	 * @param useSoap whether SOAP or REST is used.
	 * @return the (new) ID.
	 */
	public static long getID(ShoppingCartService service, boolean useSoap){
		// The file that holds our id
		File idFile = new File((useSoap ? "SOAP" : "REST") + ".id");
		long id = -1;
		if(idFile.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(idFile));
				String idS = reader.readLine();
				id = Long.parseLong(idS);
				reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
			} 
		} else {
			try {
				// Create the file
				idFile.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		// Identify with the server
		long newid = service.identify(id);
		
		// Write id back if it changed
		if(newid != id) {
			try {
				FileWriter writer = new FileWriter(idFile, false);
				writer.write("" + newid);
				writer.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return newid;
	}
}
