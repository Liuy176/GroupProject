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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameOverScreen implements Screen {
    private SpaceBlastGame game;
    private SpriteBatch spriteBatch;
    private Texture background;
    private Stage stage;
    private Skin skin;
    private Label scoreLabel;
    private Label instructionLabel;
    private TextButton toMenuButton;
    private float fade = 1f; // start from black color
    private float fadeDuration = 2f;
    private int finalScore;

    public GameOverScreen(SpaceBlastGame game, int score) {
        this.game = game;
        spriteBatch = game.getBatch();
        background = new Texture("gameOver.png");
        finalScore = score;

    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        instructionLabel = new Label("Press SPACE to restart", game.getMenu().getLabelStyle());
        scoreLabel = new Label("Final Score: " + finalScore, game.getMenu().getLabelStyle());
        toMenuButton = new TextButton("To Main Menu", game.getMenu().getSkin());

        instructionLabel.setPosition(Gdx.graphics.getWidth() / 2 - instructionLabel.getWidth() / 2, 50);
        scoreLabel.setPosition(Gdx.graphics.getWidth() / 2 - scoreLabel.getWidth() / 2, 650);
        toMenuButton.setPosition(Gdx.graphics.getWidth() / 2 - toMenuButton.getWidth() / 2, 200);
        toMenuButton.setWidth(140);
        toMenuButton.setHeight(50);
        toMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Auto-generated method stub
                game.setScreen(game.getMenu());
            };
        });
        stage.addActor(instructionLabel);
        stage.addActor(scoreLabel);
        stage.addActor(toMenuButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (fade > 0) {
            fade -= delta / fadeDuration;
            fade = Math.max(fade, 0); // fade > 0
        }

        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        spriteBatch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        spriteBatch.begin();
        if (fade > 0) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            spriteBatch.setColor(0, 0, 0, fade);
            spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.setColor(1, 1, 1, 1);
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
        spriteBatch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(game.getSpaceshipScreen()); // start the game
            game.getMenu().restartMusic();
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
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}

