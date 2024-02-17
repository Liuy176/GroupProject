package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.helpers.SoundManager;

public class GameOverScreen implements Screen {
    private SpaceBlastGame game;
    private SpriteBatch spriteBatch;
    private Texture background;
    private Stage stage;
    private Label scoreLabel, instructionLabel;
    private TextButton toMenuButton;
    private float fade = 1f;
    private float fadeDuration = 2f;
    private int finalScore;
    private SoundManager sounds;

    public GameOverScreen(SpaceBlastGame game, int score, SoundManager sounds) {
        this.game = game;
        this.sounds = sounds;
        spriteBatch = game.getBatch();
        background = new Texture("gameOver.png");
        finalScore = score;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // create buttons/labels 
        instructionLabel = new Label("Press SPACE to restart", game.getMenu().getLabelStyle());
        scoreLabel = new Label("Final Score: " + finalScore, game.getMenu().getLabelStyle());
        toMenuButton = new TextButton("To Main Menu", game.getMenu().getButtonStyle());

        // edit label properties
        scoreLabel.setPosition(Gdx.graphics.getWidth() / 2 - scoreLabel.getWidth() / 2, 650);

        // edit button properties
        toMenuButton.setPosition(Gdx.graphics.getWidth() / 2 - toMenuButton.getWidth() / 2, 200);
        toMenuButton.setWidth(240);
        toMenuButton.setHeight(50);
        toMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //game.getMenu().getButtonSound().play(game.getGameVol());
                sounds.playButton();
                game.setScreen(game.getMenu());
            };
        });

        // edit label properties
        instructionLabel.setPosition(Gdx.graphics.getWidth() / 2 - instructionLabel.getWidth() / 2, 50);
        // make label change color
        instructionLabel.addAction(Actions.run(()-> instructionLabel.addAction(Actions.forever(
            Actions.sequence(
                Actions.color(new Color(1,0,0,1),1),
                Actions.delay(0.5f), 
                Actions.color(new Color(1, 1, 1, 1), 1), 
                Actions.delay(0.5f)
            )
        ))));

        stage.addActor(instructionLabel);
        stage.addActor(scoreLabel);
        stage.addActor(toMenuButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // draw background
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // draw fade in effect
        spriteBatch.begin();
        fadeIn(delta);
        spriteBatch.end();

        // SPACE to restart the game
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(game.getSpaceshipScreen());
            game.getMenu().restartMusic();
        }
    }

    private void fadeIn(float delta){
        if (fade > 0) {
            fade -= delta / fadeDuration;
            fade = Math.max(fade, 0); // fade > 0

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            spriteBatch.setColor(0, 0, 0, fade);
            spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.setColor(1, 1, 1, 1);
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    @Override
    public void dispose() {
        background.dispose();
        stage.dispose();
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

