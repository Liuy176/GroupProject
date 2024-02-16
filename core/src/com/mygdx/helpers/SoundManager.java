package com.mygdx.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.mygdx.game.SpaceBlastGame;

public class SoundManager {
    private Sound shot;
    private Sound buttonClick;
    private SpaceBlastGame game;

    public SoundManager (SpaceBlastGame game){
        this.game = game;
        buttonClick = Gdx.audio.newSound(Gdx.files.internal("button.mp3")); // sound from: https://pixabay.com/sound-effects/button-124476/
        shot = Gdx.audio.newSound(Gdx.files.internal("blaster.mp3")); // sound from https://pixabay.com/sound-effects/blaster-2-81267/
    }

    public void playButton() {
        buttonClick.play(game.getGameVol());
    }

    public void playShotSound() {
        shot.play(game.getGameVol()/4);
    }

    public void dispose(){
        buttonClick.dispose();
    }
}
