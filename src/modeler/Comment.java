package modeler;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Date;

/**
 *
 * @author a19carlosvz
 */
public class Comment {
    private int id;
    private Date date;
    private String text;
    private Post post;
    //author
    private Profile sourceProfile;
    
    /**
     *
     * @return
     */
    public Post getPost() {
        return post;
    }

    /**
     *
     * @param post
     */
    public void setPost(Post post) {
        this.post = post;
    }

    /**
     *
     * @return
     */
    public Profile getSourceProfile() {
        return sourceProfile;
    }

    /**
     *
     * @param sourceProfile
     */
    public void setSourceProfile(Profile sourceProfile) {
        this.sourceProfile = sourceProfile;
    }

    /**
     * Constructor
     * @param id
     * @param date
     * @param text
     * @param post
     * @param sourceProfile
     */
    public Comment(int id, Date date, String text, Post post, Profile sourceProfile) {
        this.id = id;
        this.date = date;
        this.text = text;
        this.post = post;
        this.sourceProfile = sourceProfile;
    }

    /**
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public Date getDate() {
        return date;
    }

    /**
     *
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     *
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }
    
    
    
}
