/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;
import modeler.Profile;
/**
 *
 * @author Carlos
 */
public interface ProfileView {
public int getPostsShowed();
public void showProfileMenu(Profile profile);
public void showProfileNotFoundMessage();
public void showCannotLikeOwnPostMessage();
public void showAlreadyLikedPostMessage();
public void showIsAlreadyFriendMessage(String profileName);
public void showExistsFriendshipRequestMessage(String profileName);
public void showDuplicateFriendshipRequestMessage(String profileName);
public void showConnectionErrorMessage();
public void showReadErrorMessage();
public void showWriteErrorMessage();
}
