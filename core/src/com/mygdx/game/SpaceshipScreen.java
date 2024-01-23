package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;


import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;

public class SpaceshipScreen implements Screen {
    SpriteBatch batch;
    Texture img, tNave, tMissile, tEnemy1;
    private Sprite nave, missile;
    private float posX, posY, velocity, xMissile, yMissile;
    private boolean  gameover;
    private Array<Rectangle> enemies1;
  
    private long lastEnemyTime;
    private int score, power, numEnemies;
  
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private BitmapFont bitmap;
    private Texture tCandy;
    private Array<Rectangle> candies;
    private long lastCandyTime;
  
  public SpaceshipScreen(MyGdxGame game){
    batch = game.getBatch();
    img = new Texture("7.png");
    tNave = new Texture("spaceship.png");
    nave = new Sprite(tNave);
    posX = 0;
    posY = 0;
    velocity = 10;

    tMissile = new Texture("6445166.png");
    missile  = new Sprite(tMissile);
    xMissile = posX;
    yMissile = posY;
    //attack = false; */
    tCandy = new Texture("6445166.png");
    candies = new Array<Rectangle>();
    lastCandyTime = TimeUtils.nanoTime();



    tEnemy1 = new Texture("asteroid2.png");
    enemies1 = new Array<Rectangle>();
    lastEnemyTime = 0;

    score = 0;
    power = 3;
    numEnemies = 999999999;

    generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
    parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    
    parameter.size = 30;
    parameter.borderWidth = 1;
    parameter.borderColor = Color.BLACK;
    parameter.color = Color.WHITE;
    bitmap = generator.generateFont(parameter);

    gameover = false;
  }
  
    @Override
    public void render(float delta) {
  
      this.moveNave();
      this.moveEnemies();
      this.moveCandy();
  
      ScreenUtils.clear(1, 0, 0, 1);
      batch.begin();
      batch.draw(img, 0, 0);
  
      if(!gameover){
  
        batch.draw(nave, posX, posY);
        for (Rectangle candy : candies) {
          batch.draw(tCandy, candy.x, candy.y);
        }
  
        for(Rectangle enemy : enemies1 ){
          batch.draw(tEnemy1, enemy.x, enemy.y);
        }
         
        bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
        bitmap.draw(
            batch, "Power: " + power, 
            Gdx.graphics.getWidth() - 150, 
            Gdx.graphics.getHeight() - 20
            );
            
      }else{
        
        bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
        bitmap.draw(
            batch, "GAME OVER", 
            Gdx.graphics.getWidth() - 150, 
            Gdx.graphics.getHeight() - 20
            );
  
        if( Gdx.input.isKeyPressed(Input.Keys.ENTER) ){
          score = 0;
          power = 5;
          posX = 0;
          posY = 0;
          gameover = false;
        }
      }
  
  
      batch.end();
    }
  
    @Override
    public void dispose () {
      batch.dispose();
      img.dispose();
      tNave.dispose();
      tCandy.dispose();
    }
  
    private void moveNave(){
      if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
        if( posX < Gdx.graphics.getWidth() - nave.getWidth() ){
          posX += velocity;
        }
      }
      if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
        if( posX > 0 ){
          posX -= velocity;
        }
      }
      if(Gdx.input.isKeyPressed(Input.Keys.UP)){
        if( posY < Gdx.graphics.getHeight() - nave.getHeight() ){
          posY += velocity;
        }
      }
      if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
        if( posY > 0 ){
          posY -= velocity;
        }
      }
    }
  
    private void produceCandy() {
      Rectangle candy = new Rectangle(
              Gdx.graphics.getWidth(),
              MathUtils.random(0, Gdx.graphics.getHeight() - tCandy.getHeight()),
              tCandy.getWidth(),
              tCandy.getHeight()
      );
      candies.add(candy);
      lastCandyTime = TimeUtils.nanoTime();
    }
  
  
    private void ProductEnemies(){
      Rectangle enemy = new Rectangle( Gdx.graphics.getWidth(), MathUtils.random(0, Gdx.graphics.getHeight() - tEnemy1.getHeight()), tEnemy1.getWidth(), tEnemy1.getHeight());
      enemies1.add(enemy);
      lastEnemyTime = TimeUtils.nanoTime();
    }
    private void moveCandy() {
  
      if (TimeUtils.nanoTime() - lastCandyTime > 2000000000) { // Adjust the time as needed
        this.produceCandy();
      }
  
      for (Iterator<Rectangle> iter = candies.iterator(); iter.hasNext(); ) {
        Rectangle candy = iter.next();
        candy.x -= 800 * Gdx.graphics.getDeltaTime(); // Adjust speed as needed
        if (candy.x + tCandy.getWidth() < 0) iter.remove();
        else if (collide(candy.x, candy.y, candy.width, candy.height, posX, posY, nave.getWidth(), nave.getHeight())) {
          power++; // Restore power
          iter.remove();
        }
      }
    }
  
    private void moveEnemies() {
      if (TimeUtils.nanoTime() - lastEnemyTime > numEnemies) {
        this.ProductEnemies();
      }
  
      for (Iterator<Rectangle> iter = enemies1.iterator(); iter.hasNext();) {
        Rectangle enemy = iter.next();
        enemy.x -= 400 * Gdx.graphics.getDeltaTime();
  
        // Check if the asteroid is off screen and increase score
        if (enemy.x + enemy.width < 0) {
          iter.remove();
          if (!gameover) {
            score++; // Increment score when an asteroid is avoided
          }
          continue; // Continue to next iteration
        }
  
        // Check for collision with the ship
        if (collide(enemy.x, enemy.y, enemy.width, enemy.height, posX, posY, nave.getWidth(), nave.getHeight())) {
          if (!gameover) {
            --power;
            if (power <= 0) {
              gameover = true;
            }
          }
          iter.remove();
        }
      }
    }
  
  
    private boolean collide ( float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2){
          // Shrink the collision area slightly for more precise detection
          float shrinkFactor = 0.8f;
          float newW1 = w1 * shrinkFactor;
          float newH1 = h1 * shrinkFactor;
          float newW2 = w2 * shrinkFactor;
          float newH2 = h2 * shrinkFactor;
  
          // Adjust positions to keep the reduced area centered
          float newX1 = x1 + (w1 - newW1) / 2;
          float newY1 = y1 + (h1 - newH1) / 2;
          float newX2 = x2 + (w2 - newW2) / 2;
          float newY2 = y2 + (h2 - newH2) / 2;
  
          // Check collision with adjusted areas
          if (newX1 + newW1 > newX2 && newX1 < newX2 + newW2 && newY1 + newH1 > newY2 && newY1 < newY2 + newH2) {
            return true;
          }
          return false;
        }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'show'");
    }


    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'resize'");
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

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'hide'");
    }
  
      }
