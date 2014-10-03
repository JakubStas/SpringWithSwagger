package com.jakubstas.swagger.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel
public class UserList {

    private List<User> users = new ArrayList<User>();

    public UserList() {
    }

    public UserList(final Collection<User> users) {
        this.users.addAll(users);
    }

    @ApiModelProperty(required = true, position = 1)
    public List<User> getUsers() {
        return users;
    }

    @ApiModelProperty(required = true, position = 2)
    public int getCount() {
        return users.size();
    }
}
