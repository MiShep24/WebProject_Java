package com.rfr;

class User {
    private String firstName;
    private String lastName;
    private String middleName;
    private String birthday;
    private String passworduser;
    private String emailuser;
    private Integer id;
    private String avatar = Constants.PICTURES_URL_LOCATION + "nonamePhotoAvatar.jpg";

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setPassworduser(String passworduser) {
        this.passworduser = passworduser;
    }

    public void setEmailuser(String emailuser) {
        this.emailuser = emailuser;
    }

    private UserInfo userInfo = null;

    User(String firstName, String lastName, String middleName, String birthday, String passworduser, String emailuser) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.birthday = birthday;
        this.passworduser = passworduser;
        this.emailuser = emailuser;
    }

    User(Integer id, String firstName, String lastName, String middleName, String birthday, String passworduser, String emailuser, String avatar) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.birthday = birthday;
        this.passworduser = passworduser;
        this.emailuser = emailuser;
        this.id = id;
        this.avatar = avatar;
    }

    Integer getId() {
        return id;
    }

    String getFirstName() {
        return firstName;
    }

    String getLastName() {
        return lastName;
    }

    String getMiddleName() {
        return middleName;
    }

    String getBirthday() {
        return birthday;
    }

    String getPassworduser() {
        return passworduser;
    }

    String getEmailuser() {
        return emailuser;
    }

    String getAvatar() {
        return avatar;
    }

    void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
