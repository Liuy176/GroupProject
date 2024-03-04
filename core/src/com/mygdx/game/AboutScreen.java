package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.helpers.SoundManager;

public abstract class AboutScreen implements Screen {
    protected SpaceBlastGame game;
    protected SpriteBatch spriteBatch;
    protected Texture background;
    protected Stage stage;
    protected TextButton toMenuButton;
    protected SoundManager sounds;

    public AboutScreen(SpaceBlastGame game, SoundManager sounds, String backgroundPath) {
        this.game = game;
        this.spriteBatch = game.getBatch();
        this.sounds = sounds;
        this.background = new Texture(backgroundPath);
        this.toMenuButton = new TextButton("menu", game.getMenu().getButtonStyle());
        this.toMenuButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.getMenu());
                sounds.playButton();
            }
        });
        createButtons();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        setUpMenuButton();
        setupButtons();
        stage.addActor(toMenuButton);
    }

    private void setUpMenuButton(){
        toMenuButton.setPosition(40, Gdx.graphics.getHeight()-60);
        toMenuButton.setHeight(40);
        toMenuButton.setWidth(100);
    }

    public abstract void createButtons();
    public abstract void setupButtons();

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void dispose() {
        background.dispose();
        stage.dispose();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

}

