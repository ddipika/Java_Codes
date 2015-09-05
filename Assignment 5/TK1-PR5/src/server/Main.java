package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The server main class accepts client connections in an endless loop and
 * starts a new Thread for each, handling the NTP.
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class Main {

	public static void main(String[] args) {
		System.out
				.printf("Server starting with artificial offset=%b, random artificial delay=%b\n",
						Connection.SERVER_USE_ARTFICIAL_OFFSET, Connection.SERVER_USE_ARTFICIAL_DELAY);

		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(9999);
			while (true) {
				Socket socket = serverSocket.accept();
				System.out
						.println("Server: Connection accepted, starting Thread");
				Connection c = new Connection(socket);
				// Start a new thread
				new Thread(c).start();
			}
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
