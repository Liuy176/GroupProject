package com.mygdx.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.mygdx.game.SpaceBlastGame;

public class SoundManager {
    private Sound buttonClick, shot, hit, jump, explosion;
    private SpaceBlastGame game;

    public SoundManager (SpaceBlastGame game){
        this.game = game;
        buttonClick = Gdx.audio.newSound(Gdx.files.internal("button.mp3")); // sound from: https://pixabay.com/sound-effects/button-124476/
        shot = Gdx.audio.newSound(Gdx.files.internal("laserShoot.wav")); // sound from https://pixabay.com/sound-effects/blaster-2-81267/
        hit = Gdx.audio.newSound(Gdx.files.internal("hitHurt.wav"));
        jump  = Gdx.audio.newSound(Gdx.files.internal("jump.wav"));
        explosion = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
    }

    public void playButton() {
        buttonClick.play(game.getGameVol());
    }

    public void playShotSound() {
        shot.play(game.getGameVol()/4);
    }

    public void playEnenyShotSound() {
        shot.play(game.getGameVol()/7);
    }

    public void playHit(){
        hit.play(game.getGameVol());
    }
    public void playEnemyHit(){
        hit.play(game.getGameVol()/4);
    }

    public void playJump(){
        jump.play(game.getGameVol());
    }
    public void playEnemyJump(){
        jump.play(game.getGameVol()/5);
    }

    public void playExplosion(){
        explosion.play(game.getGameVol());
    }

    public void dispose(){
        buttonClick.dispose();
        shot.dispose();
        jump.dispose();
        explosion.dispose();
        hit.dispose();
    }
}
