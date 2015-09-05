/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import client.MainWindow;
import impl.IGameClient;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Server side class, containing all server functionalities.
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class GameServer extends UnicastRemoteObject implements impl.IGameServer {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// For mapping players and their scored points
    private Map<String, Player> clients;

    // For limiting x and y coordinates of image's position
    private int valid_max_x_position;
    private int valid_max_y_position;

    // For coordinates of new position of the fly
    private int[] newPosition;

    // For server name
    public static final String SERVER_NAME = "GameServer";

    /**
     * Main method starting a server, registering the RMI interface and waiting
     * for client connections.
     *
     * @param args
     */
    public static void main(String[] args) {
        System.setProperty("java.rmi.server.hostname", "localhost");
        System.out.println(System.getProperty("java.rmi.server.hostname"));
        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            GameServer gameServer = new GameServer();
            Naming.rebind(SERVER_NAME, gameServer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * GameServer constructor.
     *
     * @throws RemoteException
     */
    public GameServer() throws RemoteException {
        clients = new HashMap<>(20);
        // Limiting image position on the JPanel
        valid_max_x_position = MainWindow.WINDOW_WIDTH - MainWindow.IMG_WIDTH;
        valid_max_y_position = MainWindow.WINDOW_HEIGHT - MainWindow.IMG_HEIGHT;
        newPosition = getRandomFlyPosition();
    }

    @Override
    public void login(String playerName, IGameClient client) throws RemoteException {
        System.out.println(playerName + ": LOG IN");
        // Save client in the map
        clients.put(playerName, new Player(client));

        if (clients.size() < 2) {
            return;
        } else if (clients.size() == 2) {
            for (Map.Entry<String, Player> entrySet : clients.entrySet()) {
                entrySet.getValue().client.recieveFlyPosition(newPosition[0], newPosition[1]);
                entrySet.getValue().client.recieveFlyHunted(entrySet.getKey(), entrySet.getValue().getPoints());
            }
        } else {
            // Send him the current fly position
            client.recieveFlyPosition(newPosition[0], newPosition[1]);
        }

        for (Map.Entry<String, Player> entrySet : clients.entrySet()) {
            // Send him the current scores of all other players.
            client.recieveFlyHunted(entrySet.getKey(), entrySet.getValue().getPoints());
            // Send all other players his current score
            entrySet.getValue().client.recieveFlyHunted(playerName, 0);
        }
    }

    @Override
    public void logout(String playerName) throws RemoteException {
        System.out.println(playerName + ": LOG OUT");
        // Remove client from map.
        clients.remove(playerName);
        for (Map.Entry<String, Player> entrySet : clients.entrySet()) {
            // Send -1 points value to all other clients, which means the client left the game
            entrySet.getValue().client.recieveFlyHunted(playerName, -1);
        }
    }

    @Override
    public void huntFly(String playerName) throws RemoteException {
        // Get a new random position.
        newPosition = getRandomFlyPosition();
        // Increment his points
        clients.get(playerName).playerScored();
        for (Map.Entry<String, Player> entrySet : clients.entrySet()) {
            // Send the new points of that player and new fly position to all clients.
            entrySet.getValue().client.recieveFlyHunted(playerName, clients.get(playerName).getPoints());
            entrySet.getValue().client.recieveFlyPosition(newPosition[0], newPosition[1]);
        }
    }

    /**
     * Generating random fly position.
     *
     * @return
     */
    public int[] getRandomFlyPosition() {
        int[] array = new int[2];
        array[0] = (int) (Math.random() * valid_max_x_position);
        array[1] = (int) (Math.random() * valid_max_y_position);
        return array;
    }

}
