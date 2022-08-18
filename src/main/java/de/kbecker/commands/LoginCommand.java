package de.kbecker.commands;

import com.google.gson.JsonObject;
import de.kbecker.serializable.User;
import de.kbecker.threads.ServerRequestWorkerThread;
import de.kbecker.utils.Helper;
import de.kbecker.utils.SessionManager;

/**
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
public class LoginCommand extends Command{

    public LoginCommand(ServerRequestWorkerThread wt) {
        super(wt);
    }

    @Override
    public JsonObject exec(JsonObject jobj) {
        JsonObject response = new JsonObject();
        response.addProperty("task", "login");
        String message = "Username and password do not match";
        User user = User.readFromDatabase(jobj.get("username").getAsString());
        if(user != null) {
            String password = Helper.getInstance().hashWithSha256(jobj.get("password").getAsString() + user.getSalt());
            int code = 0;
            if(password.equals(user.getPassword())){
                code = 1;
                response.addProperty("sessionID", SessionManager.getInstance().addNewSession(user.getUsername(),wt));
                response.addProperty("username", jobj.get("username").getAsString());
            }
            response.addProperty("message", message);
            response.addProperty("code", code);
        }else{
            response.addProperty("message", message);
            response.addProperty("code", 0);
        }
        return response;
    }

}
