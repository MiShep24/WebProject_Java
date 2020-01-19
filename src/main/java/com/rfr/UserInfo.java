package com.rfr;

public class UserInfo {
    private String city;
    private String school;
    private String rang;
    private String trener;
    private String about;

    public UserInfo(String city, String school, String rang, String trener, String about) {
        this.city = city;
        this.school = school;
        this.rang = rang;
        this.trener = trener;
        this.about = about;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getRang() {
        return rang;
    }

    public void setRang(String rang) {
        this.rang = rang;
    }

    public String getTrener() {
        return trener;
    }

    public void setTrener(String trener) {
        this.trener = trener;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
