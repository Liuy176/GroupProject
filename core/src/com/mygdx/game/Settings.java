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
    private final Stage stage;
    private final Skin skin;
    private Slider musicVolSlider, gameVolSlider;
    private SelectBox<String> difficultySelect;
    private TextButton backButton;
    private Label musicVolLabel, difficultyLabel, gameVolLabel;

    private final SpaceBlastGame game;

    public Settings(SpaceBlastGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = game.getMenu().getSkin();
        musicVolLabel = new Label("Music Volume", game.getMenu().getLabelStyle());
        musicVolSlider = new Slider(0, 100, 1, false, skin);
        musicVolSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                float volume = musicVolSlider.getValue() / 100;
                game.saveVol(volume);
                game.getMenu().updateVol();
            }
        });

        gameVolLabel = new Label("Game Sounds Volume", game.getMenu().getLabelStyle());
        gameVolSlider = new Slider(0, 100, 1, false, skin);
        gameVolSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                float volume = gameVolSlider.getValue() / 100;
                game.saveGameVol(volume);
                //game.getMenu().updateGameVol();
            }
        });

        difficultyLabel = new Label("Difficulty", game.getMenu().getLabelStyle());
        difficultySelect = new SelectBox<>(skin);
        difficultySelect.setItems("Easy", "Medium", "Hard");
        difficultySelect.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                    game.saveDifficulty(difficultySelect.getSelected());
                    game.getMenu().updateHighScore();
            }
        });

        backButton = new TextButton("Back", game.getMenu().getButtonStyle());
        backButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
               // game.getMenu().getButtonSound().play(game.getGameVol());
                game.setScreen(game.getMenu());
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // game music volume slider
        musicVolSlider.setValue(game.getVol() * 100);

        // game sounds volume slider
        gameVolSlider.setValue(game.getGameVol() * 100);

        

        // game difficulty selector
        difficultySelect.setItems("Easy", "Medium", "Hard");
        difficultySelect.setSelected(game.getDif()); 

        // button to return to menu


        // adjust position
        musicVolLabel.setPosition(100, 300);
        musicVolSlider.setPosition(100, 250);
        musicVolSlider.setWidth(300);

        gameVolLabel.setPosition(100, 400);
        gameVolSlider.setPosition(100, 350);
        gameVolSlider.setWidth(300);
        
        difficultyLabel.setPosition(100, 200);
        difficultySelect.setPosition(100, 150);
        difficultySelect.setWidth(300);
        
        backButton.setPosition(100, 50);
        backButton.setSize(100, 50);

        stage.addActor(musicVolLabel);
        stage.addActor(musicVolSlider);
        stage.addActor(gameVolLabel);
        stage.addActor(gameVolSlider);
        stage.addActor(difficultyLabel);
        stage.addActor(difficultySelect);
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
