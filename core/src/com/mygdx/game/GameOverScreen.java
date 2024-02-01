package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameOverScreen implements Screen {
    private final MyGdxGame game;
    private BitmapFont gameOverFont;
    private SpriteBatch spriteBatch;

    public GameOverScreen(final MyGdxGame game) {
        this.game = game;
        spriteBatch = new SpriteBatch();
        gameOverFont = new BitmapFont();
        gameOverFont.setColor(Color.WHITE);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        gameOverFont.draw(spriteBatch, "GAME OVER", Gdx.graphics.getWidth() / 2.3f, Gdx.graphics.getHeight() / 2f);
        spriteBatch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.spaceshipScreen.dispose();
            game.setScreen(game.mainMenuScreen); 
            this.dispose();
        }
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        gameOverFont.dispose();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}

