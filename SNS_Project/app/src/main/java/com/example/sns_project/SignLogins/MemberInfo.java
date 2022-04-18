package com.example.sns_project.SignLogins;

public class MemberInfo {
    private String UidCode;
    private String email;
    private String name;
    private String phoneNumber;
    private String birthDay;
    private String address;
    private String photoUrl;

    public MemberInfo(String UidCode, String email, String name, String phoneNumber, String birthDay, String address, String photoUrl){
        this.UidCode = UidCode;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDay = birthDay;
        this.address = address;
        this.photoUrl = photoUrl;
    }
    public MemberInfo(String UidCode, String email, String name, String phoneNumber, String birthDay, String address){
        this.UidCode = UidCode;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDay = birthDay;
        this.address = address;
    }

    public String getUidCode(){
        return this.UidCode;
    }
    public void setUidCode(String UidCode){
        this.UidCode = UidCode;
    }
    public String getEmail(){
        return this.email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getPhoneNumber(){
        return this.phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }
    public String getBirthDay(){
        return this.birthDay;
    }
    public void setBirthDay(String birthDay){
        this.birthDay = birthDay;
    }
    public String getAddress(){
        return this.address;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getphotoUrl(){ return this.photoUrl; }
    public void setphotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }
}
