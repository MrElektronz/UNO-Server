package de.kbecker.commands;

import com.google.gson.JsonObject;
import de.kbecker.game.GameInstance;
import de.kbecker.threads.ServerRequestWorkerThread;

/**
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
public class DrawCardCommand extends Command{

    public DrawCardCommand(ServerRequestWorkerThread wt) {
        super(wt);
    }

    @Override
    public JsonObject exec(JsonObject jobj) {
        JsonObject message = new JsonObject();
        message.addProperty("task", "drawCard");
        String sessionID = jobj.get("sessionID").getAsString();
        GameInstance instance = GameInstance.getGameInstanceOfPlayer(sessionID);
        if(instance != null){
            instance.drawCard(sessionID);
            instance.sendGameUpdate();
        }
        return null;
    }
}
