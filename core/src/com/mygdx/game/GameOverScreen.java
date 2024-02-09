package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameOverScreen implements Screen {
    private MyGdxGame game;
    private SpriteBatch spriteBatch;
    private Texture background;
    private String finalScore;

    private float fade = 1f; // Start fully black
    private float fadeDuration = 2f; // Duration of the fade effect

    public GameOverScreen(MyGdxGame game) {
        this.game = game;
        spriteBatch = new SpriteBatch();
        background = new Texture("gameOver.png");
        String finalScore = "Final Score: " + game.spaceshipScreen.getScore();

    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (fade > 0) {
            fade -= delta / fadeDuration;
            fade = Math.max(fade, 0); // Ensure fade does not become negative
        }

        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (fade > 0) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            spriteBatch.setColor(0, 0, 0, fade);
            spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.setColor(1, 1, 1, 1); // Reset color to opaque
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
        spriteBatch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.spaceshipScreen.dispose();
            this.dispose();
            game.setScreen(game.mainMenuScreen); 
        }
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        background.dispose();
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

