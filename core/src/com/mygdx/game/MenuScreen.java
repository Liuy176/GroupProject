package com.mygdx.game;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MenuScreen implements Screen {
    private SpaceBlastGame game;
    private Stage stage;
    private Skin skin;
    private int highScore;
    private Texture background;
    private Music backgroundMusic;
    private FreeTypeFontGenerator gen;
    private BitmapFont font;
    private TextButtonStyle style;
    private LabelStyle labelStyle;
    private FreeTypeFontParameter param;
    
    public MenuScreen(SpaceBlastGame game) {
        this.game = game;
        highScore = game.loadHighScore("medium");
        gen = new FreeTypeFontGenerator(Gdx.files.internal("pixelmix.ttf"));
        labelStyle = new LabelStyle();
        param = new FreeTypeFontParameter();
        param.size = 26;
        font = gen.generateFont(param);
        labelStyle.font = font;

        // start music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Liu.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        background = new Texture("menuScreen.png");

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        //table.setDebug(true); 
        style = new TextButtonStyle();
        style.up = skin.newDrawable("default-round", Color.ROYAL);
        style.down = skin.newDrawable("default-round-down", Color.GRAY);
        style.font = font;


        // high score lable
        String highScoreText = "High Score: " + highScore;
        Label highScoreLabel = new Label(highScoreText, labelStyle);
        //buttons
        TextButton playButton = new TextButton("Play", style);
        TextButton settingsButton = new TextButton("Settings", style);
        TextButton enemyModeButton = new TextButton("Go to Enemy Mode", style); // the button to test the enemy game mode
        TextButton exitButton = new TextButton("Exit", style);

        highScoreLabel.setFontScale(1.1f); // make label bigger

        // button listeners
        playButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.getSpaceshipScreen()); // start the game
                restartMusic();
            }
        });

        settingsButton.addListener(new ChangeListener() {                
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new Settings(game));
            }
        });

        enemyModeButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new EnemyGameScreen(game,5, 50000, 60, 50000)); // switch to Enemy Game Screen
            }
        });

        exitButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit(); // exit the application
            }
        });

        // show high score
        table.add(highScoreLabel).colspan(2).padRight(900).padTop(30).row();
        // add buttons to the table
        table.row();
        table.row();
        table.add(playButton).fillX().uniformX().padLeft(85).padBottom(20).padTop(330).width(400).height(45);
        table.row();
        table.add(settingsButton).fillX().uniformX().padLeft(85).padBottom(20).width(400).height(45);
        table.row();
        table.add(enemyModeButton).fillX().uniformX().padLeft(85).padBottom(20).width(400).height(45);
        table.row();
        table.add(exitButton).fillX().uniformX().padLeft(85).width(400).height(45);
    }
    

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

    public void restartMusic() {
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
        
        backgroundMusic.play();
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
        gen.dispose();
    }

    public int getHighScore(){
        return highScore;
    }
    public void setHighScore(int score){
        this.highScore = score;
    }
    public Stage getStage(){
        return stage;
    }
    public Skin getSkin(){
        return skin;
    }
    public LabelStyle getLabelStyle(){
        return labelStyle;
    }
    public BitmapFont getFont(){
        return font;
    }
    public Music getMusic(){
        return backgroundMusic;
    }
    public TextButtonStyle getButtonStyle(){
        return style;
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'resume'");
    }
}

