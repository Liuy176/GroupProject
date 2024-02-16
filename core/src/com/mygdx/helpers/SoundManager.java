package com.mygdx.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.SpaceBlastGame;

public class SoundManager {
    private Sound buttonClick;
    private SpaceBlastGame game;
    private float lastSoundPlayTime = 0;
    private float soundDebounceInterval = 0.2f; // 200ms between plays

    public SoundManager (SpaceBlastGame game){
        this.game = game;
        buttonClick = Gdx.audio.newSound(Gdx.files.internal("button.mp3")); // sound from: https://pixabay.com/sound-effects/button-124476/
    }

    public void playButton() {
        //float currentTime = TimeUtils.nanoTime() / 1000000000.0f; // Convert nanoseconds to seconds
        //if (currentTime - lastSoundPlayTime > soundDebounceInterval) {
            buttonClick.play(game.getGameVol());
            //lastSoundPlayTime = currentTime;
       // }
    }

    public void dispose(){
        buttonClick.dispose();
    }
}
