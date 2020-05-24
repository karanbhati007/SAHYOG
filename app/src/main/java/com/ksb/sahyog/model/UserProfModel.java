package com.ksb.sahyog.model;

public class UserProfModel {
    public String name, dob, gender,address,loc,city,pin;
    public UserProfModel(){

    }
    public UserProfModel(String name, String dob, String gender, String loc, String address, String city, String pin){
        this.name=name;
        this.dob=dob;
        this.gender=gender;
        this.loc=loc;
        this.address=address;
        this.city=city;
        this.pin=pin;

    }
    public String toString() {
        return "user_profile{ " + "name= ' " + name + '\'' + "dob= ' " + dob + '\'' +
                "gender= '" + gender + '\'' +
                "address='" + address + '\'' +
                "location='" + loc + '\'' +
                "city='" + city + '\'' +
                "pin= '" + city + '\'' +
                "}";

    }
}
