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
	private AboutScreen about1;
	private AboutScreen about2;
	private AboutScreen about3;
	private AboutScreen about4;
	private AboutScreen about5;
	private AboutScreen about6;
	private AboutScreen about7;

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
		this.about1 = new AboutScreen(this, soundmanager, "aboutStory.png", null, null);
		this.about2 = new AboutScreen(this, soundmanager, "aboutGameplay1.png", null, about1);
		this.about3 = new AboutScreen(this, soundmanager, "aboutGameplay2.png", null, about2);
		this.about4 = new AboutScreen(this, soundmanager, "aboutAsteroid1.png", null, about3);
		this.about5 = new AboutScreen(this, soundmanager, "aboutAsteroid2.png", null, about4);
		this.about6 = new AboutScreen(this, soundmanager, "aboutCombat1.png",  null, about5);
		this.about7 = new AboutScreen(this, soundmanager, "aboutCombat2.png", null, about6);

		setNextScreens();
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

	// function to save set music volume for future sessions
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

	// function to save set game volume for future sessions
	public void saveGameVol(float gameVol){
		pref.putFloat("gameVolume", gameVol);
		pref.flush();
	}
	
	// function to load previously saved game volume value
	public float getGameVol(){
		return pref.getFloat("gameVolume", 0.5f);
	}

	// for navugation between about screens
	private void setNextScreens(){
		about1.setNext(about2);
		about2.setNext(about3);
		about3.setNext(about4);
		about4.setNext(about5);
		about5.setNext(about6);
		about6.setNext(about7);
	}
	
	public void render(){
		super.render();
	}

	public MenuScreen getMenu(){
		return mainMenuScreen;
	}

	public AboutScreen getAboutScreen1(){
		return about1;
	}
	public AboutScreen getAboutScreen2(){
		return about2;
	}
	public AboutScreen getAboutScreen3(){
		return about3;
	}
	public AboutScreen getAboutScreen4(){
		return about4;
	}
	public AboutScreen getAboutScreen5(){
		return about5;
	}
	public AboutScreen getAboutScreen6(){
		return about6;
	}
	public AboutScreen getAboutScreen7(){
		return about7;
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
