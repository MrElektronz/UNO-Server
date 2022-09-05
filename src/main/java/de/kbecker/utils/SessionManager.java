package de.kbecker.utils;

import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

import de.kbecker.game.GameInstance;
import de.kbecker.threads.ServerRequestWorkerThread;


/**
 * 
 * This class has the important task to manage all sessions, which means
 * managing all logins and logouts/disconnects.
 *
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 *
 */
public class SessionManager {

	private static SessionManager instance;

	/*
	 * private constructor, so no other class can instanciate from SessionManager
	 */
	private SessionManager() {
		sessions = new HashMap<String, Session>();
	}

	/**
	 * 
	 * @return the one and only instance of SessionManager
	 */
	public static SessionManager getInstance() {
		if (instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}

	/**
	 * 
	 * @return HashMap of a clone of all sessions (which means read-only)
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Session> getSessions() {
		return (HashMap<String, Session>) sessions.clone();
	}

	// Has format SessionID,Session(Username, lastTimeSeen)
	private HashMap<String, Session> sessions;


	/**
	 * 
	 * @param wt       the ServerRequestWorkerThread belonging to the user
	 * @param username the user's name
	 * @return sessionID if username does not already have session, if so: return
	 *         "-1"
	 */
	public String addNewSession(String username, ServerRequestWorkerThread wt) {

		String id = createNewSessionID();
		Session s = new Session(username, wt);
		if (!sessions.containsValue(s)) {
			sessions.put(id, s);
			return id;
		} else {
			return "-1";
		}
	}

	/**
	 * 
	 * @param sessionID of the user
	 * @return "Session"-object of the given sessionID
	 */
	public Session getSession(String sessionID) {
		return sessions.get(sessionID);
	}

	/**
	 * 
	 * @param wThread to get the corresponding session from
	 * @return "Session"-object of the given ServerRequestWorkerThread
	 */
	public Session getSession(ServerRequestWorkerThread wThread) {
		for (Session s : sessions.values()) {
			if (s.getWorkerThread().equals(wThread)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param id
	 * 
	 *           Delete session with the given id
	 */
	public void delSession(String id) {
		Session s = sessions.get(id);
		s.getCurrentGame().removePlayer(id);
		sessions.remove(id);

	}

	/**
	 * 
	 * @param session
	 * 
	 *                Delete the given session
	 */
	public void delSession(Session session) {
		String id = "";
		for (String sID : sessions.keySet()) {
			if (sessions.get(sID).equals(session)) {
				id = sID;
			}
		}
		// Remove player from game instance
		GameInstance instance = session.getCurrentGame();
		if(instance != null){
			instance.removePlayer(id);
			instance.sendLobbyUpdate();
		}
		sessions.remove(id);
	}

	/**
	 * 
	 * @param sessionID of the session we want to check
	 * @return true if session with given id exists, false if otherwise
	 */
	public boolean doesSessionExist(String sessionID) {
		return sessions.containsKey(sessionID);
	}


	/**
	 * useful function to print details about each session
	 */
	public void printSessions() {
		Object[] keys = sessions.keySet().toArray();
		System.out.println("--------------------------------------");
		for (int i = 0; i < sessions.size(); i++) {
			System.out.println(i + ": " + keys[i] + " " + sessions.get(keys[i]));
		}
		System.out.println("--------------------------------------");
	}

	/**
	 * 
	 * @return new sessionID does not check for duplicates now
	 */
	private String createNewSessionID() {

		String generatedString = Helper.getInstance().randomString(16,true);
		if (doesSessionExist(generatedString)) {
			return createNewSessionID();
		} else {
			return generatedString;
		}
	}

	/**
	 * 
	 * @param sessionID of the session which should be logged out
	 * @return 1
	 *                  logs out the session with the given sessionID
	 */
	public int logout(String sessionID) {
		System.out.println("Session " + sessionID + "(" + sessions.get(sessionID) + ")" + " deleted");
		delSession(sessionID);
		return 1;
	}

	/**
	 * 
	 * @param session we want to log out
	 * @return 1 if session was deleted, 0 if session does not exist
	 * 
	 *         logs out the given session
	 */
	public int logout(Session session) {
		if (session != null) {
			System.out.println("Session " + session.toString() + " deleted");
			delSession(session);
			return 1;
		} else {
			return 0;
		}
	}


	/**
	 * 
	 * @author KBeck
	 * 
	 *         Session class which stores data about a logged in session (such as
	 *         the username, the game instance the user is in and so on)
	 *
	 */
	public class Session {
		private String username;
		private Instant lastTimeSeen;
		private GameInstance currentGame;
		private ServerRequestWorkerThread wt;

		/**
		 * 
		 * @param username of the user belonging to the session
		 * @param wt the ServerRequestWorkerThread of the user
		 */
		public Session(String username, ServerRequestWorkerThread wt) {
			this.username = username;
			lastTimeSeen = Instant.now();
			//currentGame = null;
			this.wt = wt;
		}

		/**
		 * 
		 * @return user's username
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * 
		 * @return the client's socket
		 */
		public Socket getClient() {
			return wt.getClient();
		}

		/**
		 * 
		 * @return the ServerRequestWorkerThread which can be used to send data directly
		 *         to the client
		 */
		public ServerRequestWorkerThread getWorkerThread() {
			return wt;
		}

		/**
		 * 
		 * @return "Instant"-object which stores the last time the user has pinged the
		 *         server
		 */
		public Instant getLastTimeSeen() {
			return lastTimeSeen;
		}

		/**
		 * gets called whenever the client pings the server
		 */
		public void updateLastTimeSeen() {
			lastTimeSeen = Instant.now();
		}

		/**
		 * 
		 * @param currentGame the new game instance the user has joined
		 */
		public void setCurrentGame(GameInstance currentGame) {
			this.currentGame = currentGame;
		}

		/**
		 * 
		 * @return the current game instance
		 */
		public GameInstance getCurrentGame() {
			return currentGame;
		}

		/**
		 * Override equals in Order to use containsValue of class Hashmap
		 */
		@Override
		public boolean equals(Object arg0) {
			Session s = (Session) arg0;
			return username.equals(s.getUsername());
		}

		@Override
		public String toString() {
			return username + " last seen at: " + lastTimeSeen.toString();
		}

	}

}
