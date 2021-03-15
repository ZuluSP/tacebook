/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

/**
 *
 * @author Carlos
 */
public interface InitMenuView {
    
public boolean showLoginMenu();
public void showLoginErrorMessage();
public void showConnectionErrorMessage();
public void showReadErrorMessage();
public void showWriteErrorMessage();
public String showNewNameMenu();
public void showRegisterMenu();
    
}
