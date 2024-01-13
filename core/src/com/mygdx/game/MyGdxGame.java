package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MyGdxGame extends Game {
	private static MyGdxGame INSTANCE = null;
	private int screenWidth, screenHeight;
	private OrthographicCamera ortographicCamera;

	public int V_WIDTH = 400;
	public int v_HEIGHT = 208;

	public MyGdxGame() {
		INSTANCE = this;
	}

    @Override
    public void create() {
		this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();
        this.ortographicCamera = new OrthographicCamera();
        this.ortographicCamera.setToOrtho(false, screenWidth, screenHeight);

        setScreen(new MenuScreen(this)); //menu shows when after starting the game
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

	/*public void changeScreen(Screen currentScreen, ScreenType newScreenType) {
		
		if(newScreenType == ScreenType.GAME)
			setScreen(new GameScreen(this.ortographicCamera));
		if(newScreenType == ScreenType.MENU)
			setScreen(new MenuScreen());
		if(newScreenType == ScreenType.INFO)
			setScreen(new InfoScreen());
			

		// LATER change this according to our needs
	}*/
	
	/*public void changeScreen(Screen currentScreen, ScreenType newScreenType, String message) {
		
		if(newScreenType == ScreenType.END_GAME){
			setScreen(new EndGameScreen(message));
		}

		// LATER change this according to our needs
	}*/

    @Override
    public void dispose() {
        super.dispose();
    }
}
