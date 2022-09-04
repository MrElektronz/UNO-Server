package de.kbecker.commands;

import com.google.gson.JsonObject;
import de.kbecker.cards.Card;
import de.kbecker.game.GameInstance;
import de.kbecker.threads.ServerRequestWorkerThread;

/**
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
public class SetCardCommand extends Command{

    public SetCardCommand(ServerRequestWorkerThread wt) {
        super(wt);
    }

    @Override
    public JsonObject exec(JsonObject jobj) {
        JsonObject message = new JsonObject();
        String sessionID = jobj.get("sessionID").getAsString();
        Card card = Card.deserialize(jobj.get("card").getAsJsonObject());

        GameInstance instance = GameInstance.getGameInstanceOfPlayer(sessionID);
        if(instance != null && instance.setCard(sessionID, card)){
            instance.sendGameUpdate();
        }
        return null;
    }
}
