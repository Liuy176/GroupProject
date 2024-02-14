package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Settings implements Screen{
    private Stage stage;
    private Skin skin;
    private Slider volumeSlider;
    private SelectBox<String> difficultySelectBox;
    private TextButton backButton;
    private Label volumeLabel, difficultyLabel;

    private final SpaceBlastGame game;

    public Settings(SpaceBlastGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = game.getMenu().getSkin();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // volume Slider
        volumeLabel = new Label("Volume", game.getMenu().getLabelStyle());
        volumeSlider = new Slider(0, 100, 1, false, skin);
        volumeSlider.setValue(game.getVol() * 100);
        volumeSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue() / 100;
                game.saveVol(volume);
            }
        });
        

        // game difficulty selector
        difficultyLabel = new Label("Difficulty", game.getMenu().getLabelStyle());
        difficultySelectBox = new SelectBox<>(skin);
        difficultySelectBox.setItems("Easy", "Medium", "Hard");
        difficultySelectBox.setSelected(game.getDif()); 
        difficultySelectBox.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                    game.saveDifficulty(difficultySelectBox.getSelected());
            }
        });

        // button to return
        backButton = new TextButton("Back", game.getMenu().getButtonStyle());
        backButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.getMenu());
            }
        });

        volumeLabel.setPosition(100, 300);
        volumeSlider.setPosition(100, 250);
        volumeSlider.setWidth(300);
        
        difficultyLabel.setPosition(100, 200);
        difficultySelectBox.setPosition(100, 150);
        difficultySelectBox.setWidth(300);
        
        backButton.setPosition(100, 50);
        backButton.setSize(100, 50);

        stage.addActor(volumeLabel);
        stage.addActor(volumeSlider);
        stage.addActor(difficultyLabel);
        stage.addActor(difficultySelectBox);
        stage.addActor(backButton);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
    
}
