package impl;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGameClient extends Remote{
	void recieveFlyHunted(String playerName, int newPoints) throws RemoteException;
	void recieveFlyPosition(int x, int y) throws RemoteException;
}
