package de.kbecker.game;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.kbecker.cards.Card;
import de.kbecker.threads.ServerRequestWorkerThread;
import de.kbecker.utils.Helper;
import de.kbecker.utils.SessionManager;


/**
 * This class is an instance of a game, it contains all the players playing the
 * game and other important information about it
 * 
 * @author KBeck
 *
 */
public class GameInstance {

	private GameState state;

	private SessionManager sm;
	private static ArrayList<GameInstance> games = new ArrayList<>();
	private static final int SLOTS = 4;
	private GameSession game;
	private String gameId;
	/**
	 * Generates a new, empty game instance, which starts as a lobby
	 */
	public GameInstance() {
		sm = SessionManager.getInstance();
		state = GameState.Lobby;
		games.add(this);
		String gameId = Helper.getInstance().randomString(8,false);
		while(gameIdAlreadyExist(gameId)){
			gameId = Helper.getInstance().randomString(8,false);
		}
		this.gameId = gameId;
		game = new GameSession();
	}

	public String getGameID() {
		return gameId;
	}

	private boolean gameIdAlreadyExist(String gameId){
		for(GameInstance game : games){
			if(game.gameId != null && game.gameId.equals(gameId)){
				return true;
			}
		}
		return false;
	}

	public static GameInstance getGameInstanceByID(String gameID){
		for(GameInstance game : games){
			if(game.gameId.equals(gameID)){
				return game;
			}
		}
		return null;
	}

	public static GameInstance getGameInstanceOfPlayer(String sessionId){
		for(GameInstance game : games){
			for(Player player : game.getPlayers()){
				if(player.getSessionID().equals(sessionId)){
					return game;
				}
			}
		}
		return null;
	}

	public void sendLobbyUpdate() {
		JsonObject message = new JsonObject();
		message.addProperty("task", "lobbyUpdate");
		message.addProperty("slotText",getPlayerCount()+"/"+SLOTS);
		notifyAllClients(new Gson().toJson(message));
	}

	public void sendGameUpdate() {
		JsonObject message = new JsonObject();
		message.addProperty("task", "gameUpdate");
		message.add("currentCard", game.getCurrentCard().serialize());
		message.addProperty("turn", game.getTurn());
		message.addProperty("currentPlayer", game.getCurrentPlayerIndex());

		JsonArray playerList = new JsonArray();
		message.add("players", playerList);
		//Also add players as list
		notifyAllClients(new Gson().toJson(message));
	}

	/**
	 * 
	 * @return state of the game instance (either GameState.Lobby or
	 *         GameState.Ingame)
	 */
	public GameState getState() {
		return state;
	}

	/**
	 * 
	 * @return Array of all players, can include "null"
	 */
	public ArrayList<Player> getPlayers() {
		return game.getPlayers();
	}

	/**
	 * 
	 * @return the player whose turn it is
	 */
	public Player getCurrentPlayer() {
		return game.getCurrentPlayer();
	}


	public int getSlots() {
		return SLOTS;
	}

	/**
	 * @param sessionID of the user
	 * @return true if successful
	 */
	public boolean addPlayer(String sessionID) {
		if (state == GameState.Ingame) {
			return false;
		}
		if(game.getPlayers().size()<SLOTS){

			game.getPlayers().add(new Player(sessionID));
			sm.getSession(sessionID).setCurrentGame(this);

				// Sending new lobby status to all clients, because Base64 doesn't include ';',
				// we can use it as a separator
			/*try {
				notifyAllClients("!LobbyUpdate;" + Serializer.toString(getLobbyData()));
			} catch (IOException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			return true;
			}
		return false;
	}

	/**
	 * CAUTION: This method does not delete the game instance when it's empty,
	 * please use GameFinder.getInstance().removePlayerFromLobby(sessionID) for
	 * this. Removes the player from the game
	 * 
	 * @param sessionID of the user
	 */
	public void removePlayer(String sessionID) {
		for (int i = 0; i < game.getPlayers().size(); i++) {
				if (game.getPlayers().get(i).getSessionID().equals(sessionID)) {
					//game.removePlayer(players.get(i).getUsername());
					game.getPlayers().remove(i);

				}
		}
		SessionManager.Session s = sm.getSession(sessionID);
		if (s != null) {
			s.setCurrentGame(null);
		}

	}









	/**
	 * 
	 * @param sessionID of the user
	 * @return true if player is in lobby, false if not
	 */
	public boolean containsPlayer(String sessionID) {
		for (int i = 0; i < game.getPlayers().size(); i++) {
			if (game.getPlayers().get(i).getSessionID().equals(sessionID)) {
				return true;
			}
		}
		return false;
	}



	/**
	 * starts the game if the game instance is a lobby
	 */
	public void startGame() {
		if (state == GameState.Lobby) {
			state = GameState.Ingame;
		}
	}


	/**
	 * 
	 * @return true if gamestate is either ingame or it is lobby, but there are
	 *         already 4 people joined
	 */
	public boolean isFull() {
		return game.getPlayers().size() >= SLOTS;
	}

	/**
	 * 
	 * @return amount of all connected players in this game instance
	 */
	public int getPlayerCount() {
		return game.getPlayers().size();
	}

	/**
	 * sends data to every client in the game instance
	 * 
	 * @param data to be send to all connected clients
	 */
	public void notifyAllClients(String data) {
		for (int i = 0; i < game.getPlayers().size(); i++) {
				try {
					ServerRequestWorkerThread wThread = sm.getSession(game.getPlayers().get(i).getSessionID()).getWorkerThread();
					if (wThread.getClient().isConnected()) {
						wThread.getOut().writeUTF(data);
						wThread.getOut().flush();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	/**
	 * A trivial class which helps handling data about each player in the game
	 * instance
	 * 
	 * @author KBeck
	 *
	 */
	class Player {
		private String sessionID;
		private String username;
		private ArrayList<Card> cards;

		public Player(String sessionID) {
			this.sessionID = sessionID;
			username = "";
			cards = new ArrayList<>();
		}



		/**
		 * 
		 * @return player's session
		 */
		public SessionManager.Session getSession() {
			return sm.getSession(sessionID);
		}

		public ArrayList<Card> getCards() {
			return cards;
		}

		/**
		 * 
		 * @return player's username
		 */
		public String getUsername() {
			if (username.equals("")) {
				SessionManager.Session s = sm.getSession(sessionID);
				if (s != null) {
					this.username = s.getUsername();
				} else {
					this.username = "";
				}
			}
			return username;
		}


		/**
		 * 
		 * @return player's sessionID
		 */
		public String getSessionID() {
			return sessionID;
		}
	}

	/**
	 * 
	 * @author KBeck
	 * 
	 *         a small enumeration to make it more intuitive to determine whether a
	 *         game instance is still of type "Lobby" or already "Ingame"
	 *
	 */
	public enum GameState {
		Lobby, Ingame;
	}

}
