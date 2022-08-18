package de.kbecker.commands;

import com.google.gson.JsonObject;
import de.kbecker.threads.ServerRequestWorkerThread;

/**
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
public abstract class Command {

    protected ServerRequestWorkerThread wt;
    public Command(ServerRequestWorkerThread wt){
        this.wt = wt;
    }
    public abstract JsonObject exec(JsonObject jobj);
}
