package impl;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGameServer extends Remote{
	void login (String playerName, IGameClient client) throws RemoteException;
	void logout(String playerName) throws RemoteException;
	void huntFly(String playerName) throws RemoteException;

}
