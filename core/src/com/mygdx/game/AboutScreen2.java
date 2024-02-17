package com.mygdx.game;

import com.badlogic.gdx.Gdx;
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

public class AboutScreen2 implements Screen {
    private SpaceBlastGame game;
    private SpriteBatch spriteBatch;
    private Texture background;
    private Stage stage;
    private TextButton toMenuButton, nextButton, prevButton;


    public AboutScreen2(SpaceBlastGame game, SoundManager sounds) {
        this.game = game;
        spriteBatch = game.getBatch();
        background = new Texture("about3.png");
        toMenuButton = new TextButton("menu", game.getMenu().getButtonStyle());
        toMenuButton.addListener(new ChangeListener() {                
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.getMenu());
                sounds.playButton();
            }
        });
        
        nextButton = new TextButton(">", game.getMenu().getButtonStyle());
        nextButton.addListener(new ChangeListener() {                
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.getAboutScreen3());
                sounds.playButton();
            }
        });
        prevButton = new TextButton("<", game.getMenu().getButtonStyle());
        prevButton.addListener(new ChangeListener() {                
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.getAboutScreen());
                sounds.playButton();
            }
        });
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        toMenuButton.setPosition(40, Gdx.graphics.getHeight()-60);
        toMenuButton.setHeight(40);
        toMenuButton.setWidth(100);

        nextButton.setPosition(Gdx.graphics.getWidth()-70, Gdx.graphics.getHeight()-60);
        nextButton.setHeight(40);
        nextButton.setWidth(40);

        prevButton.setPosition(Gdx.graphics.getWidth()-120, Gdx.graphics.getHeight()-60);
        prevButton.setHeight(40);
        prevButton.setWidth(40);

        stage.addActor(toMenuButton);
        stage.addActor(nextButton);
        stage.addActor(prevButton);
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
