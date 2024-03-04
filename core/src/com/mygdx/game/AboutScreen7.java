package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.helpers.SoundManager;


public class AboutScreen7 extends AboutScreen {
    private TextButton prevButton;

    public AboutScreen7(SpaceBlastGame game, SoundManager sounds) {
        super(game, sounds, "aboutCombat2.png");
    }

    public void createButtons() {
        prevButton = new TextButton("<", game.getMenu().getButtonStyle());
        prevButton.addListener(new ChangeListener() {                
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.getAboutScreen6());
                sounds.playButton();
            }
        });
    }
    @Override
    public void setupButtons() {
        prevButton.setPosition(Gdx.graphics.getWidth()-120, Gdx.graphics.getHeight()-60);
        prevButton.setHeight(40);
        prevButton.setWidth(40);

        stage.addActor(prevButton);
    }

    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
}
