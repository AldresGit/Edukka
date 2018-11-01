package com.javier.edukka.controller;

import com.javier.edukka.model.GameModel;

public class GameSingleton {
    private static final GameSingleton ourInstance = new GameSingleton();
    private GameModel model;

    private GameSingleton() {}

    public static GameSingleton getInstance() {
        return ourInstance;
    }

    public GameModel getGameModel() {
        return model;
    }

    public void setGameModel(GameModel model) {
        this.model = model;
    }
}
