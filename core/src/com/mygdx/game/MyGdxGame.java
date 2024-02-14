package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.helpers.Constants;

import java.lang.invoke.ConstantCallSite;
import java.util.concurrent.ConcurrentHashMap;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MyGdxGame extends Game {
	private int screenWidth, screenHeight;
	private OrthographicCamera ortographicCamera;
	public MenuScreen mainMenuScreen;
	public SpaceshipScreen spaceshipScreen;
	private SpriteBatch batch;

	public MyGdxGame() {}

    @Override
    public void create() {
		this.batch = new SpriteBatch();
		this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();
        this.ortographicCamera = new OrthographicCamera();
        this.ortographicCamera.setToOrtho(false, screenWidth, screenHeight);

		this.mainMenuScreen = new MenuScreen(this);
		this.spaceshipScreen = new SpaceshipScreen(this, Constants.maxPlayerHealth);

        setScreen(mainMenuScreen); //menu screen appears after starting the game
    }

	public void saveHighScore(int highScore) {
		Preferences prefs = (Preferences) Gdx.app.getPreferences("MyGamePreferences");
		prefs.putInteger("highScore", highScore);
		try{prefs.flush();}
		catch (Exception e){}
	}

	public int loadHighScore() {
		Preferences prefs = (Preferences) Gdx.app.getPreferences("MyGamePreferences");
		return prefs.getInteger("highScore", 0);
	}
	

	public void render(){
		super.render();
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public OrthographicCamera getCamera() {
		return ortographicCamera;
	}

	public SpriteBatch getBatch(){
		return batch;
	}

    @Override
    public void dispose() {
        super.dispose();
    }
}
