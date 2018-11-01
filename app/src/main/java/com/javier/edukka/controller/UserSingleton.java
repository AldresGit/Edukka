package com.javier.edukka.controller;

import com.javier.edukka.model.UserModel;

public class UserSingleton {
    private static final UserSingleton ourInstance = new UserSingleton();
    private UserModel model;

    private UserSingleton() {}

    public static UserSingleton getInstance() {
        return ourInstance;
    }

    public UserModel getUserModel() {
        return model;
    }

    public void setUserModel(UserModel model) {
        this.model = model;
    }
}
