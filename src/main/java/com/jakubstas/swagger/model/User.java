package com.jakubstas.swagger.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel
public class User {

    private String userName;

    private String firstName;

    private String surname;

    private String email;

    private byte[] avatar;

    private Date lastUpdated;

    private final String oneToBeHidden = "hiddenOne";

    @ApiModelProperty(position = 1, required = true, value = "username containing only lowercase letters or numbers")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @ApiModelProperty(position = 2, required = true)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @ApiModelProperty(position = 3, required = true)
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @ApiModelProperty(position = 4, required = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    @ApiModelProperty(position = 5, value = "timestamp of last modification")
    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @ApiModelProperty(access = "hidden")
    public String getOneToBeHidden() {
        return oneToBeHidden;
    }
}
