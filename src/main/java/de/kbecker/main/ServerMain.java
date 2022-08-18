package de.kbecker.main;

import com.google.gson.Gson;
import de.kbecker.serializable.User;
import de.kbecker.threads.ServerListenerThread;
import de.kbecker.utils.ConfigManager;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;

/**
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
public class ServerMain {

    private static int port;
    private static Gson gson;
    private static ServerListenerThread listenerThread;
    private static EntityManagerFactory entityManagerFactory;
    public static void main(String[] args) {
        loadConfig();
        entityManagerFactory = Persistence.createEntityManagerFactory("UNOServer");


        gson = new Gson();
        try {
            listenerThread = new ServerListenerThread(port);
            listenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            entityManagerFactory.close();
        }

    }

    /**
     * loads values from the config file
     */
    private static void loadConfig() {
        port = Integer.valueOf(ConfigManager.readFromProperties("server.port"));
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
}
