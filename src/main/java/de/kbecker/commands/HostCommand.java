package de.kbecker.commands;

import com.google.gson.JsonObject;
import de.kbecker.game.GameInstance;
import de.kbecker.serializable.User;
import de.kbecker.threads.ServerRequestWorkerThread;
import de.kbecker.utils.Helper;

/**
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
public class HostCommand extends Command{

    public HostCommand(ServerRequestWorkerThread wt) {
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
        GameInstance newGameInstance = new GameInstance();
        newGameInstance.addPlayer(sessionID);
        response.addProperty("code",1);
        response.addProperty("lobbyID",newGameInstance.getGameID());
        response.addProperty("slotText",newGameInstance.getPlayerCount()+"/"+newGameInstance.getSlots());
        return response;
    }
}
