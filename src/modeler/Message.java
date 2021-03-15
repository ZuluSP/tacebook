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
public class Message {
    private int id;
    private String text;
    private Date date;
    private boolean read;
    private Profile destProfile;
    private Profile sourceProfile;

    /**
     *
     * @return
     */
    public Profile getDestProfile() {
        return destProfile;
    }

    /**
     *
     * @param destProfile
     */
    public void setDestProfile(Profile destProfile) {
        this.destProfile = destProfile;
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
     * @param text
     * @param date
     * @param read
     */
    public Message(int id, String text, Date date, boolean read, Profile destProfile, Profile sourceProfile) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.read = read;
        this.destProfile = destProfile;
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
    public boolean isRead() {
        return read;
    }

    /**
     *
     * @param read
     */
    public void setRead(boolean read) {
        this.read = read;
    }
    
}
