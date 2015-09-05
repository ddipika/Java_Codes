package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class runs after a client connected to the server. It waits for incoming
 * messages and sends responses back forever, until the client side disconnects.
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class Connection implements Runnable {

	/**
	 * If set to false, the actual time difference between two different
	 * machines can be measured. If set to true, it is good for testing the
	 * protocol with both client and server on the same machine.
	 */
	public static final boolean SERVER_USE_ARTFICIAL_DELAY = true;

	/**
	 * If set to false, the actual time difference between two different
	 * machines can be measured. If set to true, it is good for testing the
	 * protocol with both client and server on the same machine.
	 */
	public static final boolean SERVER_USE_ARTFICIAL_OFFSET = true;

	/**
	 * The socket already connected to the client.
	 */
	public Socket socket;

	/**
	 * @param socket
	 *            the socket already connected to the client
	 */
	public Connection(final Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		BufferedReader reader;
		PrintWriter writer;
		try {
			try {
				reader = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream(), true);

				// Do as long as the socket is open
				while (!socket.isClosed()) {
					System.out.println("Server: Waiting for a new message.");

					// Receive a message (contains t1)
					String inMsg = reader.readLine();
					// Sleep for artificial delay on receiving if required
					if (SERVER_USE_ARTFICIAL_DELAY) {
						// Random delay between 10 and 100
						long randomDelay = (long) (10 + Math.random() * 90);
						System.out.println("Server: Artificial delay (in ms):"
								+ randomDelay);
						Thread.sleep(randomDelay);
					}
					// Take receive timestamp immediately
					long timinus2 = System.currentTimeMillis();

					// Add artificial offset if required
					if (SERVER_USE_ARTFICIAL_OFFSET) {
						timinus2 += 1000;
					}

					// If the message is null, we abort
					if (inMsg == null) {
						break;
					}

					System.out.printf(
							"Server: Message received. t_i-3=%s t_i-2=%d\n",
							inMsg, timinus2);

					// Send message back
					String outMsg = inMsg + "," + timinus2 + ",";
					// Take send timestamp and append directly to outgoing
					// message
					long timinus1 = System.currentTimeMillis();
					// Add artificial offset if required
					if (SERVER_USE_ARTFICIAL_OFFSET) {
						timinus1 += 1000;
					}
					writer.println(outMsg + timinus1);

					System.out
							.printf("Server: Message sent. t_i-3=%s t_i-2=%d t_i-1=%d\n",
									inMsg, timinus2, timinus1);
				}

			} catch (IOException | InterruptedException e) {
				System.err.println("Server: Thread error: " + e.getMessage());
				e.printStackTrace();

			} finally {
				// Close the socket
				System.out.println("Server: Closing socket.");
				socket.close();
			}
		} catch (IOException e) {
			System.err.println("Server: Socket could not be closed properly: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}
}
