package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AboutScreen implements Screen {
    private SpaceBlastGame game;
    private SpriteBatch spriteBatch;
    private Texture background;
    private Stage stage;
    private Label story;
    private TextButton toMenuButton, nextButton;


    public AboutScreen(SpaceBlastGame game) {
        this.game = game;
        spriteBatch = game.getBatch();
        background = new Texture("about1.png");
        String storyText = "In the vast, dark expanse of space,\n an alien finds himself stranded,\n light-years away from his home planet.\n His spacecraft,\n damaged during a mission,\n leaves him drifting among the stars,\n seeking a way back.\n The path home is fraught with dangers,\n most notably the asteroid belt,\n a treacherous maze of floating rocks\n inhabited by hostile creatures.\n These creatures are known\n to attack any intruder\n who dares to traverse their domain.\n Alien, armed only with his wits\n and a malfunctioning blaster,\n must overcome the obstacles\n that lie between him\n and his distant home.\n Each asteroid brings a new challenge,\n but the alien's determination\n fuels his journey through the darkness." ;
        story = new Label(storyText, game.getMenu().getLabelStyle());
        toMenuButton = new TextButton("back", game.getMenu().getButtonStyle());
        nextButton = new TextButton(">", game.getMenu().getButtonStyle());
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        story.setPosition(Gdx.graphics.getWidth() / 2 - story.getWidth() / 2 -60, Gdx.graphics.getHeight() / 2-300);
        story.setAlignment(Align.center);


        stage.addActor(story);
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
