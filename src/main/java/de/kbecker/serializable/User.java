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
