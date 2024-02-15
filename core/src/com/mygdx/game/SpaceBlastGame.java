package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.helpers.Constants;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class SpaceBlastGame extends Game {
	private int screenWidth, screenHeight;
	private OrthographicCamera ortographicCamera;
	private MenuScreen mainMenuScreen;
	private SpaceshipScreen spaceshipScreen;
	private SpriteBatch batch;
	private Preferences pref;

	public SpaceBlastGame() {}

    @Override
    public void create() {
		this.batch = new SpriteBatch();
		this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();
		this.ortographicCamera = new OrthographicCamera();
        this.ortographicCamera.setToOrtho(false, screenWidth, screenHeight);
		this.pref = (Preferences) Gdx.app.getPreferences("SpaceBlast");
		this.mainMenuScreen = new MenuScreen(this);
		this.spaceshipScreen = new SpaceshipScreen(this, Constants.maxPlayerHealth);
		
        setScreen(mainMenuScreen); //menu screen appears after starting the game
    }

	// function to save high score for future game sessions
	public void saveHighScore(int highScore, String mode) {
		pref.putInteger(mode, highScore);
		try{pref.flush();}
		catch (Exception e){}
	}

	// function to load high score when launching the game
	public int loadHighScore(String mode) {
		return pref.getInteger(mode, 0);
	}

	// function to save set volume for future sessions
	public void saveVol(float volume){
		pref.putFloat("volume", volume);
		pref.flush();
	}
	
	// function to load previously saved volume value
	public float getVol(){
		return pref.getFloat("volume", 0.5f);
	}

	// function to save set difficulty for future sessions
	public void saveDifficulty(String dif){
		pref.putString("difficulty", dif);
		pref.flush();
	}

	// function to load previously saved difficulty value
	public String getDif(){
		return pref.getString("difficulty", "Medium");
	}
	
	public void render(){
		super.render();
	}

	public MenuScreen getMenu(){
		return mainMenuScreen;
	}

	public SpaceshipScreen getSpaceshipScreen(){
		return spaceshipScreen;
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
