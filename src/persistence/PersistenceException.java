/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

/**
 *
 * @author Carlos
 */
public class PersistenceException extends Exception {
    private int code;

    /**
     *
     * @param code
     * @param message
     */
    public PersistenceException(int code, String message) {
        super(message);  
        this.code = code;        
    }

    /**
     *
     * @return
     */
    public int getCode() {
        return code;
    }

    /**
     *
     * @param code
     */
    public void setCode(int code) {
        this.code = code;
    }
    
    /**
     *
     */
    public static final int CONECTION_ERROR = 0;

    /**
     *
     */
    public static final int CANNOT_READ = 1;

    /**
     *
     */
    public static final int CANNOT_WRITE = 2;
}
