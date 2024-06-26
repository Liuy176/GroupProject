package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.helpers.SoundManager;

public class Settings implements Screen{
    private Stage stage;
    private Skin skin;
    private Slider musicVolSlider, gameVolSlider;
    private SelectBox<String> difficultySelect;
    private TextButton backButton;
    private Label musicVolLabel, difficultyLabel, gameVolLabel;
    private Texture background;

    private final SpaceBlastGame game;

    public Settings(SpaceBlastGame game, SoundManager sounds) {
        this.game = game;
        background = new Texture("about1.png");
        stage = new Stage(new ScreenViewport());
        skin = game.getMenu().getSkin();

        // game music volume slider
        musicVolLabel = new Label("Music Volume", game.getMenu().getLabelStyle());
        musicVolSlider = new Slider(0, 100, 1, false, skin);
        musicVolSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                float volume = musicVolSlider.getValue() / 100;
                game.saveVol(volume);
                sounds.updateMusicVol();;
            }
        });

        // game sounds volume slider
        gameVolLabel = new Label("Game Sounds Volume", game.getMenu().getLabelStyle());
        gameVolSlider = new Slider(0, 100, 1, false, skin);
        gameVolSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                float volume = gameVolSlider.getValue() / 100;
                game.saveGameVol(volume);
            }
        });

        // game difficulty selector
        difficultyLabel = new Label("Difficulty", game.getMenu().getLabelStyle());
        difficultySelect = new SelectBox<>(skin);
        difficultySelect.setItems("Easy", "Medium", "Hard");
        difficultySelect.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                    game.saveDifficulty(difficultySelect.getSelected());
                    game.getMenu().updateHighScore();
            }
        });

        // button to return to menu
        backButton = new TextButton("Back", game.getMenu().getButtonStyle());
        backButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                sounds.playButton();
                game.setScreen(game.getMenu());
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        musicVolSlider.setValue(game.getVol() * 100);
        gameVolSlider.setValue(game.getGameVol() * 100);

        difficultySelect.setItems("Easy", "Medium", "Hard");
        difficultySelect.setSelected(game.getDif()); 

        // adjust position
        musicVolLabel.setPosition(Gdx.graphics.getWidth() / 2 - musicVolLabel.getWidth()/2, 450);
        musicVolSlider.setPosition(Gdx.graphics.getWidth() / 2 - 150, 400);
        musicVolSlider.setWidth(300);

        gameVolLabel.setPosition(Gdx.graphics.getWidth() / 2 - gameVolLabel.getWidth()/2, 550);
        gameVolSlider.setPosition(Gdx.graphics.getWidth() / 2 - 150, 500);
        gameVolSlider.setWidth(300);
        
        difficultyLabel.setPosition(Gdx.graphics.getWidth() / 2 - difficultyLabel.getWidth()/2, 350);
        difficultySelect.setPosition(Gdx.graphics.getWidth() / 2 - 150, 300);
        difficultySelect.setWidth(300);
        
        backButton.setPosition(40, Gdx.graphics.getHeight()-60);
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

        // draw background
        game.getBatch().begin();
        game.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getBatch().end();

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
        skin.dispose();
        background.dispose();
    }
    
}
