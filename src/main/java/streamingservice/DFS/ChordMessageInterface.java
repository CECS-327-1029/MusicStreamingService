package streamingservice.DFS;

import java.rmi.*;
import java.io.*;

public interface ChordMessageInterface extends Remote
{
    ChordMessageInterface getPredecessor()  throws RemoteException;
    ChordMessageInterface locateSuccessor(long key) throws RemoteException;
    ChordMessageInterface closestPrecedingNode(long key) throws RemoteException;
    void joinRing(String Ip, int port)  throws RemoteException;
    void joinRing(ChordMessageInterface successor) throws RemoteException;
    void notify(ChordMessageInterface j) throws RemoteException;
    boolean isAlive() throws RemoteException;
    long getId() throws RemoteException;
    
    
    void put(long guidObject, RemoteInputFileStream inputStream) throws IOException, RemoteException;
    void put(long guidObject, String text) throws IOException, RemoteException;
    RemoteInputFileStream get(long guidObject) throws IOException, RemoteException;
    byte[] get(long guidObject, int offset, int len) throws IOException, RemoteException;
    void delete(long guidObject) throws IOException, RemoteException;
}
