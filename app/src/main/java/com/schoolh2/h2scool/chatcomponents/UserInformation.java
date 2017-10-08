package com.schoolh2.h2scool.chatcomponents;

/**
 * Created by MMenem on 10/8/2017.
 */

public class UserInformation {
    private String userName;
    private String phone;
    private String age;
    private String gender;
    private String image;

    public UserInformation() {

    }

    public UserInformation(String userName, String phone, String age, String gender, String image) {
        this.userName = userName;
        this.phone = phone;
        this.gender = gender;
        this.age = age;
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


}
