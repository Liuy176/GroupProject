package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.helpers.Constants;
import com.mygdx.helpers.SoundManager;
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
	private SoundManager soundmanager;
	private Settings settingsScreen;
	private AboutScreen about;
	private AboutScreen2 about2;
	private AboutScreen3 about3;
	private AboutScreen4 about4;

	public SpaceBlastGame() {}

    @Override
    public void create() {
		this.batch = new SpriteBatch();
		this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();
		this.ortographicCamera = new OrthographicCamera();
        this.ortographicCamera.setToOrtho(false, screenWidth, screenHeight);
		this.pref = (Preferences) Gdx.app.getPreferences("SpaceBlast");
		this.soundmanager = new SoundManager(this);
		this.mainMenuScreen = new MenuScreen(this, soundmanager);
		this.settingsScreen = new Settings(this, soundmanager);
		this.spaceshipScreen = new SpaceshipScreen(this, Constants.maxPlayerHealth, soundmanager);
		this.about = new AboutScreen(this, soundmanager);
		this.about2 = new AboutScreen2(this, soundmanager);
		this.about3 = new AboutScreen3(this, soundmanager);
		this.about4 = new AboutScreen4(this, soundmanager);

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

	public void saveGameVol(float gameVol){
		pref.putFloat("gameVolume", gameVol);
		pref.flush();
	}
	
	public float getGameVol(){
		return pref.getFloat("gameVolume", 0.5f);
	}
	
	public void render(){
		super.render();
	}

	public MenuScreen getMenu(){
		return mainMenuScreen;
	}

	public AboutScreen getAboutScreen(){
		return about;
	}
	public AboutScreen2 getAboutScreen2(){
		return about2;
	}
	public AboutScreen3 getAboutScreen3(){
		return about3;
	}
	public AboutScreen4 getAboutScreen4(){
		return about4;
	}

	public SpaceshipScreen getSpaceshipScreen(){
		return spaceshipScreen;
	}

	public Settings getSettingsScreen(){
		return settingsScreen;
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
