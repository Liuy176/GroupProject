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

//class for all about screens
public class AboutScreen implements Screen {
    private SpaceBlastGame game;
    private SpriteBatch spriteBatch;
    private Texture background;
    private Stage stage;
    private TextButton toMenuButton;
    private SoundManager sounds;
    private TextButton prevScreenBtn;
    private TextButton nextScreenBtn;
    private AboutScreen next;
    private AboutScreen prev;

    public AboutScreen(SpaceBlastGame game, SoundManager sounds, String backgroundPath, AboutScreen next, AboutScreen prev) {
        this.game = game;
        this.spriteBatch = game.getBatch();
        this.sounds = sounds;
        this.background = new Texture(backgroundPath);
        this.next = next;
        this.prev = prev;
        this.toMenuButton = new TextButton("Menu", game.getMenu().getButtonStyle());
        this.toMenuButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.getMenu());
                sounds.playButton();
            }
        });
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        setUpMenuButton();
        createButtons(next, prev);
        setupButtons(next, prev);
        stage.addActor(toMenuButton);
        if(next!=null)stage.addActor(nextScreenBtn);
        if(prev!=null)stage.addActor(prevScreenBtn);
    }

    // position 'Menu' button
    private void setUpMenuButton(){
        toMenuButton.setPosition(40, Gdx.graphics.getHeight()-60);
        toMenuButton.setHeight(40);
        toMenuButton.setWidth(100);
    }

    // create additional necessary buttons
    public  void createButtons(AboutScreen next, AboutScreen prev){
        if(next!=null){
            this.nextScreenBtn = new TextButton(">", game.getMenu().getButtonStyle());
            nextScreenBtn.addListener(new ChangeListener() {                
                public void changed(ChangeEvent event, Actor actor) {
                    game.setScreen(next);
                    sounds.playButton();
                }
            });
        }
        if(prev!=null){
            this.prevScreenBtn = new TextButton("<", game.getMenu().getButtonStyle());
            prevScreenBtn.addListener(new ChangeListener() {                
                public void changed(ChangeEvent event, Actor actor) {
                    game.setScreen(prev);
                    sounds.playButton();
                }
            });
        }
    }
    // configure the additionally added buttons
    public void setupButtons(AboutScreen next, AboutScreen prev){
        if(next!=null){
            nextScreenBtn.setPosition(Gdx.graphics.getWidth()-70, Gdx.graphics.getHeight()-60);
            nextScreenBtn.setHeight(40);
            nextScreenBtn.setWidth(40);
        }
        if(prev!=null){
            prevScreenBtn.setPosition(Gdx.graphics.getWidth()-120, Gdx.graphics.getHeight()-60);
            prevScreenBtn.setHeight(40);
            prevScreenBtn.setWidth(40);
        }
    };

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

    public void setNext(AboutScreen next){
        this.next = next;
    }

    @Override
    public void dispose() {
        background.dispose();
        stage.dispose();
        sounds.dispose();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {}

    @Override
    public void resize(int arg0, int arg1) {}

    @Override
    public void resume() {}

}

