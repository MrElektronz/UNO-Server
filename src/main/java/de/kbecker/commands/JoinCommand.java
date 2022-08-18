package de.kbecker.commands;

import com.google.gson.JsonObject;
import de.kbecker.game.GameInstance;
import de.kbecker.threads.ServerRequestWorkerThread;

/**
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
public class JoinCommand extends Command{

    public JoinCommand(ServerRequestWorkerThread wt) {
        super(wt);
    }

    @Override
    public JsonObject exec(JsonObject jobj) {
        String sessionID = jobj.get("sessionID").getAsString();
        JsonObject response = new JsonObject();
        response.addProperty("task", "lobbyUpdate");
        if(GameInstance.getGameInstanceOfPlayer(sessionID) != null){
            response.addProperty("code",0);
            return response;
        }
        GameInstance lobby = GameInstance.getGameInstanceByID(jobj.get("lobbyID").getAsString());
        int code = -1;
        if(lobby == null){
            System.out.println("game with "+jobj.get("lobbyID").getAsString()+" is null");
            response.addProperty("code",code);
            return response;
        }
        if(lobby.getState() != GameInstance.GameState.Lobby){
            code = -2;
            response.addProperty("code",code);
            return response;
        }
        code = 1;
        lobby.addPlayer(sessionID);
        response.addProperty("code",code);
        response.addProperty("slotText",lobby.getPlayerCount()+"/"+lobby.getSlots());
        response.addProperty("lobbyID",lobby.getGameID());
        lobby.sendLobbyUpdate();

        return response;
    }
}
