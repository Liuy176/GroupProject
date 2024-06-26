package com.mygdx.game;

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
import com.mygdx.helpers.SoundManager;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MenuScreen implements Screen {
    private SpaceBlastGame game;
    private Stage stage;
    private Skin skin;
    private int highScore;
    private Texture background;
    private FreeTypeFontGenerator gen;
    private BitmapFont font;
    private TextButtonStyle style;
    private LabelStyle labelStyle;
    private FreeTypeFontParameter param;
    private TextButton playButton, settingsButton, aboutButton, exitButton;
    
    public MenuScreen(SpaceBlastGame game, SoundManager sounds) {
        this.game = game;
        highScore = game.loadHighScore(game.getDif());
        gen = new FreeTypeFontGenerator(Gdx.files.internal("pixelmix.ttf"));
        labelStyle = new LabelStyle();
        param = new FreeTypeFontParameter();
        param.size = 26;
        font = gen.generateFont(param);
        labelStyle.font = font;
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        background = new Texture("menuScreen.png");

        // button style
        style = new TextButtonStyle();
        style.up = skin.newDrawable("default-round", Color.ROYAL);
        style.down = skin.newDrawable("default-round-down", Color.GRAY);
        style.font = font;

        //buttons
        playButton = new TextButton("Play", style);
        playButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                sounds.playButton();
                sounds.playIfNotPlaying();
                game.setScreen(game.getSpaceshipScreen()); // start the game
            }
        });
        settingsButton = new TextButton("Settings", style);
        settingsButton.addListener(new ChangeListener() {                
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.getSettingsScreen());
                sounds.playButton();
            }
        });
        aboutButton = new TextButton("About", style);
        aboutButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.getAboutScreen1());
                sounds.playButton();
            }
        });
        exitButton = new TextButton("Exit", style);
        exitButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                sounds.playButton();
                Gdx.app.exit(); // exit the application
            }
        });

        sounds.getBackground1().play();
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);


        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        //table.setDebug(true); 

        // high score lable
        String highScoreText = "High Score: " + highScore;
        Label highScoreLabel = new Label(highScoreText, labelStyle);
        highScoreLabel.setFontScale(1.1f); // make label bigger


        // show high score
        table.add(highScoreLabel).colspan(2).padRight(900).padTop(30).row();
        // add buttons to the table
        table.row();
        table.row();
        table.add(playButton).fillX().uniformX().padLeft(85).padBottom(20).padTop(330).width(400).height(45);
        table.row();
        table.add(settingsButton).fillX().uniformX().padLeft(85).padBottom(20).width(400).height(45);
        table.row();
        table.add(aboutButton).fillX().uniformX().padLeft(85).padBottom(20).width(400).height(45);
        table.row();
        table.add(exitButton).fillX().uniformX().padLeft(85).width(400).height(45);
    }
    

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 1, 1);
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

    public void updateHighScore(){
        highScore = game.loadHighScore(game.getDif());
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
        font.dispose();
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
    public TextButtonStyle getButtonStyle(){
        return style;
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}

