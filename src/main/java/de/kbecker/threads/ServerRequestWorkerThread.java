package de.kbecker.threads;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.kbecker.commands.*;
import de.kbecker.utils.SessionManager;


/**
 * 
 * This thread is the "core" of this project, it processes every new request a
 * client sends to a server.
 * 
 * @author KBeck
 * 
 *
 */
public class ServerRequestWorkerThread extends Thread {

	private Socket client;
	private DataInputStream in;
	private DataOutputStream out;
	private SessionManager sm;
	private HashMap<String, Command> commandMap;

	/**
	 * Initiates all commands for this thread, does create a new command instance
	 * for every thread in order to avoid multiple threads from accessing the same method of the same
	 * object at the same time.
	 * @param client socket to client who sent the message
	 */
	public ServerRequestWorkerThread(Socket client) {
		this.client = client;
		sm = SessionManager.getInstance();
		commandMap = new HashMap<>();
		commandMap.put("login", new LoginCommand(this));
		commandMap.put("register", new RegisterCommand(this));
		commandMap.put("host", new HostCommand(this));
		commandMap.put("quit", new QuitCommand(this));
		commandMap.put("join", new JoinCommand(this));
		commandMap.put("leave", new LeaveCommand(this));
		commandMap.put("startGame", new StartGameCommand(this));
		try {
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!client.isClosed()) {
			try {
				String received = "";
				try {
					received = in.readUTF();
				} catch (Exception ex) {
					client.close();
				}
				JsonElement jsonReceived = new JsonParser().parse(received);

				if (jsonReceived.isJsonObject()) {
					//Call 'exec' method of corresponding command object
					System.out.println(jsonReceived.getAsJsonObject().get("command").getAsString());
					JsonObject response = commandMap.get(jsonReceived.getAsJsonObject().get("command").getAsString()).exec(jsonReceived.getAsJsonObject());
					if(response != null){
						System.out.println("send: "+response.toString());
						out.writeUTF(new Gson().toJson(response));
					}
				}
				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		shutdown();
	}

	/**
	 * 
	 * @return the Outputstream corresponding to the client
	 */
	public DataOutputStream getOut() {
		return out;
	}

	/**
	 * 
	 * @return socket to client who send the message
	 */
	public Socket getClient() {
		return client;
	}

	/**
	 * closes all sockets and logs the player out
	 */
	public void shutdown() {
		try {
			in.close();
			out.close();
			client.close();
			// logout if error
			sm.logout(sm.getSession(this));
			System.out.println("Close connection to CLIENT");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
