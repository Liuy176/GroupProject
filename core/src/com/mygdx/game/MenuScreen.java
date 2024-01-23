package com.mygdx.game;

import com.badlogic.gdx.ScreenAdapter;

import javax.swing.event.ChangeEvent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MenuScreen extends ScreenAdapter {
    private MyGdxGame game;
    private Stage stage;
    private Skin skin;
    
    public MenuScreen(MyGdxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        //buttons
        TextButton playButton = new TextButton("Play", skin);
        TextButton settingsButton = new TextButton("Settings", skin);
        TextButton enemyModeButton = new TextButton("Go to Enemy Mode", skin); // the button to test the enemy game mode
        TextButton exitButton = new TextButton("Exit", skin);

        // button listeners
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new SpaceshipScreen(game));
            }
        });

        settingsButton.addListener(new ChangeListener() {
            @Override                  
            public void changed(ChangeEvent event, Actor actor) {
                // settings
            }
        });

        enemyModeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new EnemyGameScreen(game,4, 500, 30)); // Switch to Enemy Game Screen
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit(); // when pressed 'exit'
            }
        });

        // add buttons to the table
        table.add(playButton).fillX().uniformX().padBottom(10);
        table.row();
        table.add(settingsButton).fillX().uniformX().padBottom(10);
        table.row();
        table.add(enemyModeButton).fillX().uniformX().padBottom(10);
        table.row();
        table.add(exitButton).fillX().uniformX();
    }
    

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}

