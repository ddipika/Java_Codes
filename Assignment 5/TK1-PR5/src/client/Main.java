package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * This class provides the whole client functionality. It connects to a server,
 * runs the NTP 10x, prints the results and exits.
 */
public class Main {

	/**
	 * If set to false, the actual time difference between two different
	 * machines can be measured. If set to true, it is good for testing the
	 * protocol with both client and server on the same machine.
	 */
	public static final boolean CLIENT_USE_ARTFICIAL_DELAY = true;

	public static void main(String[] args) {
		System.out.printf("Client starting with random artificial delay=%b\n",
				CLIENT_USE_ARTFICIAL_DELAY);

		Socket client = null;
		try {
			try {
				// Either connect to localhost or to host given as argument
				if (args.length == 0 || args[0].isEmpty()) {
					System.out.println("Client: Connecting to localhost");
					client = new Socket("localhost", 9999);
				} else {
					System.out.println("Client: Connecting to " + args[0]);
					client = new Socket(args[0], 9999);
				}

				DataOutputStream outputStream = new DataOutputStream(
						client.getOutputStream());
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(client.getInputStream()));

				long lowest_d_i = Long.MAX_VALUE;
				double result_o_i = Double.NaN;

				// Repeat 10 times:
				for (int i = 0; i < 10; i++) {
					// Take timestamp and immediately send message
					long timinus3 = System.currentTimeMillis();
					outputStream.writeUTF("" + timinus3 + "\n");
					outputStream.flush();
					System.out.printf("Client: Message sent. t_i-3=%d\n",
							timinus3);

					// Receive response
					String messageback = bufferedReader.readLine();
					// Sleep for artificial delay on receiving if required
					if (CLIENT_USE_ARTFICIAL_DELAY) {
						// Random delay between 10 and 100
						long randomDelay = (long) (10 + Math.random() * 90);
						System.out.println("Client: Artificial delay (in ms):"
								+ randomDelay);
						Thread.sleep(randomDelay);
					}
					// Immediately take timestamp
					long ti = System.currentTimeMillis();

					// Split by ","
					String[] messageSeperated = messageback.trim().split(",");
					// The first value is just copied from our message, no
					// need to parse it
					long timinus2 = Long.parseLong(messageSeperated[1]);
					long timinus1 = Long.parseLong(messageSeperated[2]);

					System.out
							.printf("Client: Message received. t_i-3=%d t_i-2=%d t_i-1=%d t_i=%d\n",
									timinus3, timinus2, timinus1, ti);

					// Calculate delay d_i
					long d_i = (timinus2 - timinus3) + (ti - timinus1);
					System.out.println("Client: Delay (in ms) d_i=" + d_i);

					// Calculate estimate offset o_i
					double o_i = 0.5 * ((timinus2 - timinus3) + (timinus1 - ti));
					System.out.println("Client: Estimate offset (in ms) o_i="
							+ o_i);

					// Save as solution if d_i is lower than lowest d so far
					if (d_i < lowest_d_i) {
						lowest_d_i = d_i;
						result_o_i = o_i;
					}

					// Sleep 500 ms before issuing next request
					Thread.sleep(500);
				}

				// Display result
				System.out.println("Client: Best results: d_i=" + lowest_d_i
						+ " o_i=" + result_o_i);
			} catch (IOException | InterruptedException e) {
				System.err.println("Client: Error: " + e.getMessage());
				e.printStackTrace();
			} finally {
				if (client != null) {
					// Close the socket
					System.out.println("Client: Closing socket.");
					client.close();
				}
			}
		} catch (IOException e) {
			System.err.println("Client: Socket could not be closed properly: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}
}
