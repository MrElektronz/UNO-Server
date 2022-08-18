package de.kbecker.commands;

import com.google.gson.JsonObject;
import de.kbecker.game.GameInstance;
import de.kbecker.threads.ServerRequestWorkerThread;

/**
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
public class StartGameCommand extends Command{

    public StartGameCommand(ServerRequestWorkerThread wt) {
        super(wt);
    }

    @Override
    public JsonObject exec(JsonObject jobj) {
        String sessionID = jobj.get("sessionID").getAsString();
        GameInstance instance = GameInstance.getGameInstanceOfPlayer(sessionID);
        if(instance != null){
            instance.startGame();
            instance.sendGameUpdate();
        }
        return null;
    }
}
