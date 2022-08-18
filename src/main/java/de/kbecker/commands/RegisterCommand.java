package de.kbecker.commands;

import com.google.gson.JsonObject;
import de.kbecker.serializable.User;
import de.kbecker.threads.ServerRequestWorkerThread;
import de.kbecker.utils.Helper;

/**
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
public class RegisterCommand extends Command{

    public RegisterCommand(ServerRequestWorkerThread wt) {
        super(wt);
    }

    @Override
    public JsonObject exec(JsonObject jobj) {
        String salt = Helper.getInstance().randomString(32, true);
        String password = Helper.getInstance().hashWithSha256(jobj.get("password").getAsString()+salt);
        User user = new User(jobj.get("username").getAsString(), password, salt);
        int storedResult = user.storeToDatabase();
        String message = "";
        switch(storedResult){
            case 0: message = "This username already exists";
                break;
            case 1: message = "The user has been created successfully";
                break;
            case -1: message = "An unexpected error has occurred";
                break;
            case -2: message = "Length of password/username does not fit";
                break;
            default:
                break;
        }
        JsonObject response = new JsonObject();
        response.addProperty("task", "register");
        response.addProperty("message", message);
        response.addProperty("code", storedResult);
        return response;
    }
}
