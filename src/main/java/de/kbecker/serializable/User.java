package de.kbecker.serializable;

import de.kbecker.main.ServerMain;
import javax.persistence.*;
import java.io.Serializable;


/**
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
@Entity
@Table(name="users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private int id;

    /**
     * TODO: Set username as primary key and remove id, because it's unique as well
     */
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    public User(){}

    public User(String username, String password, String salt){
        this.username = username;
        this.password = password;
        this.salt = salt;
    }

    public String getSalt() {
        return salt;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public int getId() {
        return id;
    }


    /**
     * Stores 'this' user in the database
     * @return 0-> if already exists, -2-> if name/password too long/short, 1-> if successful, -1-> if any other error occurred
     */
    public int storeToDatabase(){

        if(User.readFromDatabase(username) != null){
            return 0;
        }
        if(password.length()<5 || username.length()<5 || username.length()>30){
            return -2;
        }
        EntityManager em = ServerMain.getEntityManagerFactory().createEntityManager();
        EntityTransaction et = null;
        try{
            et = em.getTransaction();
            et.begin();
            em.persist(this);
            et.commit();
            return 1;
        }catch (Exception ex){
            if(et != null){
                et.rollback();
            }
            ex.printStackTrace();
        }
        finally{
            em.close();
        }
        return -1;
    }


    /**
     * Can't use session.get() here, because the users need to be filtered by their username and not id
     * @param username of the user
     * @return User object from database
     */
    public static User readFromDatabase(String username){
        EntityManager em = ServerMain.getEntityManagerFactory().createEntityManager();
        TypedQuery<User> tq = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
        tq.setParameter("username", username);
        User user = null;
        try{
            user = tq.getSingleResult();
        }catch(NoResultException nex) {
            em.close();
            return null;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            em.close();
        }
        return user;
    }
}
