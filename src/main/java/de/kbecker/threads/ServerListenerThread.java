package de.kbecker.threads;

import de.kbecker.main.ServerMain;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class serves as a thread which only listens for incoming clients and
 * creates a new worker thread for every one of them This type of architecture
 * can work on requests way faster, because it does not have to wait for a
 * request to be finished to start a new one.
 *
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
public class ServerListenerThread extends Thread {

	// TODO: Change the connection later on to SSLSockets for secure communication (also needed for the client)
	private ServerSocket serverSocket;

	/**
	 * 
	 * @param port to let the server run on
	 * @throws IOException
	 */
	public ServerListenerThread(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

	/**
	 * 
	 * @return the server socket
	 */
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	
	/**
	 * waits for a request of a client and sends the message over to a new
	 * "woker"-thread, which handles it in detail.
	 */
	@Override
	public void run() {
		while (!serverSocket.isClosed() && serverSocket.isBound()) {
			try {
				System.out.println("Waiting for clients at " + serverSocket.getLocalPort());
				Socket client = serverSocket.accept();

				ServerRequestWorkerThread worker = new ServerRequestWorkerThread(client);
				worker.start();

			} catch (Exception ex) {
				try {
					serverSocket.close();
					ServerMain.getEntityManagerFactory().close();

				} catch (IOException e) {

				}
			}
		}
	}

	/**
	 * gets called whenever "shutdown" is used by an admin
	 */
	public void shutdown() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
