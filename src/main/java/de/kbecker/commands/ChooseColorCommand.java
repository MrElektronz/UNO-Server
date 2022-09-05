package de.kbecker.commands;

import com.google.gson.JsonObject;
import de.kbecker.game.GameInstance;
import de.kbecker.threads.ServerRequestWorkerThread;

/**
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
public class ChooseColorCommand extends Command{

    public ChooseColorCommand(ServerRequestWorkerThread wt) {
        super(wt);
    }

    @Override
    public JsonObject exec(JsonObject jobj) {
        String sessionID = jobj.get("sessionID").getAsString();
        GameInstance instance = GameInstance.getGameInstanceOfPlayer(sessionID);
        if(instance != null){
            if(instance.chooseColor(sessionID, jobj.get("color").getAsString())){
                instance.sendGameUpdate();
            }
        }
        return null;
    }
}
