/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import impl.IGameClient;
import impl.IGameServer;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import server.GameServer;

/**
 *The client implementation.
 *
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
       
 */
public class GameClient extends UnicastRemoteObject  implements IGameClient {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The window
	MainWindow window;
	
	// The server interface
	IGameServer server;
	
	// Name of this client
	String name;
	
	// The player's current values
	Map<String, Integer> playerPoints;
	
        
    /**
     * Creates a client and connects to the server.
     * @param window the MainWindow.
     * @param name The name of the player.
     * @throws RemoteException  
     */
	public GameClient(MainWindow window, String name) throws RemoteException {
		this.name = name;
		this.window = window;
		this.playerPoints = new HashMap<>();
		
		try {
            // We always connect to local host.
			server = (IGameServer) Naming.lookup("//127.0.0.1/" + GameServer.SERVER_NAME);
			server.login(name, this);
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void recieveFlyHunted(String playerName, int newPoints) throws RemoteException {
		if(newPoints == -1) {
			// We defined this means the player logged out
			playerPoints.remove(playerName);
		} else {
			playerPoints.put(playerName, newPoints);
		}
		window.updatePlayerPoints(playerPoints);
	}

	@Override
	public void recieveFlyPosition(int x, int y) throws RemoteException {
		window.repaintFly(x, y);
	}
	
	/**
	 * Called from the GUI if the fly was killed.
	 */
	public void flyKilled() {
		System.out.println("Fly killed");
		try {
			server.huntFly(this.name);
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Called from the GUI if the window is closed.
	 */
	public void logout() {
		try {
			server.logout(this.name);
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}
}
