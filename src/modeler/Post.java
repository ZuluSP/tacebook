package modeler;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author a19carlosvz
 */
public class Post {

    private int id;
    private Date date;
    private String text;
    private ArrayList<Comment> comments = new ArrayList<>();
    private Profile profile;
    private Profile author;
    
  
    private ArrayList<Profile> profileLikes = new ArrayList<>();

    public ArrayList<Profile> getProfileLikes() {
        return profileLikes;
    }

    public void setProfileLikes(ArrayList<Profile> profileLikes) {
        this.profileLikes = profileLikes;
    }
    

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Profile getAuthor() {
        return author;
    }

    public void setAuthor(Profile author) {
        this.author = author;
    }

  

    public Post(int id, Date date, String text, Profile profile, Profile author) {
        this.id = id;
        this.date = date;
        this.text = text;
        this.profile = profile;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
