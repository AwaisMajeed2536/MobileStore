package misbah.naseer.mobilestore.model;

/**
 * Created by Devprovider on 28/04/2017.
 */

public class UserInformationModel {
    private String userId;
    private String UserName;
    private String contact;
    private String email;
    private String password;
    private String userType;

    public UserInformationModel() {
    }

    public UserInformationModel(String userId, String userName, String contact, String email, String password, String userType) {
        this.userId = userId;
        UserName = userName;
        this.contact = contact;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
