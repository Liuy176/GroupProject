package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.helpers.SoundManager;


public class AboutScreen1 extends AboutScreen {
    private TextButton nextButton;

    public AboutScreen1(SpaceBlastGame game, SoundManager sounds) {
        super(game, sounds, "aboutStory.png");
    }

    public void createButtons() {
        nextButton = new TextButton(">", game.getMenu().getButtonStyle());
        nextButton.addListener(new ChangeListener() {                
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.getAboutScreen2());
                sounds.playButton();
            }
        });
    }
    @Override
    public void setupButtons() {
        nextButton.setPosition(Gdx.graphics.getWidth()-70, Gdx.graphics.getHeight()-60);
        nextButton.setHeight(40);
        nextButton.setWidth(40);

        stage.addActor(nextButton);
    }

    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
}

