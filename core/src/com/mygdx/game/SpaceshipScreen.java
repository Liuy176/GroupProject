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
import com.badlogic.gdx.graphics.GL20;

public class SpaceshipScreen implements Screen {
    private SpriteBatch batch;
    private Texture img, tNave, tMissile, tEnemy1, tCandy;
    private Sprite nave, missile;
    private float posX, posY, velocity, xMissile, yMissile;
    private boolean  gameover;
    private Array<Rectangle> enemies1;
  
    private long lastEnemyTime;
    private int score, power, numEnemies;
    private boolean toIncrementScore = false; //Andrejs edit
  
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private BitmapFont bitmap;
    private Array<Rectangle> candies;
    private long lastCandyTime;
    
    private boolean paused; //Andrej's edit (to be able to pause)
    private MyGdxGame game;
    
    private float gravity = 0.5f; 
    private float jumpVelocity = -10f; 
    private float currentVelocity = 0f;

    private boolean isBlinking;
    private float blinkStartTime;
    private float blinkDuration = 2f;
    private float blinkInterval = 0.2f; 
    private boolean isShipVisible = true;

    private float asteroidBatchDistance = 210; // distance between asteroids in a pair
    private float lastAsteroidBatchX = 0;
    private float timeSinceLastAsteroidPair = 0f;
    private float pairGenInterval = 1f;
    private boolean fadeOut = false;
    private float fadeOutSpeed = 0.5f;
    private float fadeOutOpacity = 0.0f;
    private float collisionTimer = 0f;
    private int timesCrashed;
    public float playerHealth;
    private float weaponStrength;

  public SpaceshipScreen(MyGdxGame game, float health){
    this.game = game;
    batch = game.getBatch();
    img = new Texture("9.png");
    tNave = new Texture("ship-1.png.png");
    nave = new Sprite(tNave);
    posX = 100;
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

    tEnemy1 = new Texture("asteroidNew.png");
    enemies1 = new Array<Rectangle>();
    lastEnemyTime = 0;

    playerHealth = health;
    weaponStrength = 15;
    timesCrashed = 0;
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
    isBlinking=false;
    blinkStartTime = 0f;
    paused=false; //Andrejs edit
  }
  
    @Override
    public void render(float delta) {
      if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) paused = !paused; // Andrejs edit
      if(!paused){
        this.moveNave();
        this.moveEnemies(delta);
        this.moveCandy();
      }
      //Andrejs edit
      if (isBlinking) {
        float elapsed = (TimeUtils.nanoTime() - blinkStartTime) *0.000000001f;
        if (elapsed > blinkDuration) {
            isBlinking = false;
            paused = false;
            isShipVisible = true;
        } else {
            if ((int)(elapsed / blinkInterval) % 2 == 0) {
                isShipVisible = true;
            }else {
              isShipVisible = false;
            }
        }
    } 
    
    if (fadeOut) {
      fadeOutOpacity += fadeOutSpeed * delta;
      fadeOutOpacity = Math.min(fadeOutOpacity, 1.0f); // opacity< 1
      }   //Until here
  
      ScreenUtils.clear(1, 0, 0, 1);
      batch.begin();
      batch.draw(img, 0, 0);
      
      if (paused) bitmap.draw(batch, "PAUSED", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2); // Andrejs edit
      if(!gameover){
        
        if(isShipVisible) batch.draw(nave, posX, posY);
        for (Rectangle candy : candies) {
          batch.draw(tCandy, candy.x, candy.y);
        }
  
        for(Rectangle enemy : enemies1 ){
          batch.draw(tEnemy1, enemy.x, enemy.y, enemy.width * 2, enemy.height*2);
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
      if (fadeOut) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.begin();
        batch.setColor(0, 0, 0, fadeOutOpacity);
        batch.draw(img, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(1, 1, 1, 1); // Reset color
        batch.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
     }
    }
  
    @Override
    public void dispose () {
      batch.dispose();
      img.dispose();
      tNave.dispose();
      tCandy.dispose();
    }
  
    private void moveNave(){
      if(paused) return; // Andrejs edit

      /*if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
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
      }*/
    if (Gdx.input.isKeyJustPressed(Input.Keys.R)) { // Press R to restart
        restart(true);
    }
    if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
        currentVelocity = jumpVelocity; // jump
    }

    // gravity
    currentVelocity += gravity;
    posY -= currentVelocity;

    if(posY < 0){
        posY = 0;
    }
    if(posY > Gdx.graphics.getHeight() - nave.getHeight()){
        posY = Gdx.graphics.getHeight() - nave.getHeight();
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
  
  
    /*private void ProductEnemies(){
      Rectangle enemy = new Rectangle( Gdx.graphics.getWidth(), MathUtils.random(0, Gdx.graphics.getHeight() - tEnemy1.getHeight()), tEnemy1.getWidth(), tEnemy1.getHeight());
      enemies1.add(enemy);
      lastEnemyTime = TimeUtils.nanoTime();
    }
    */
    private void moveCandy() {
      if(paused) return; // Andrejs edit

      if ((TimeUtils.nanoTime() - lastCandyTime)*0.1 > 2000000000) { // Adjust the time as needed
        this.produceCandy();
      }
  
      for (Iterator<Rectangle> iter = candies.iterator(); iter.hasNext(); ) {
        Rectangle candy = iter.next();
        candy.x -= 200 * Gdx.graphics.getDeltaTime(); // Adjust speed as needed
        if (candy.x + tCandy.getWidth() < 0) iter.remove();
        else if (collide(candy.x, candy.y, candy.width, candy.height, posX, posY, nave.getWidth(), nave.getHeight())) {
          power++; // Restore power
          iter.remove();
        }
      }
    }
  
    private void moveEnemies(float delta) {
      if(paused) return; //Andrejs edit

      //if ((TimeUtils.nanoTime() - lastEnemyTime)*1.5 > numEnemies) {
      //  this.produceAsteroidPair();
      //}
      this.produceAsteroidPair(delta);
  
      for (Iterator<Rectangle> iter = enemies1.iterator(); iter.hasNext();) {
        Rectangle enemy = iter.next();
        enemy.x -= 400 * Gdx.graphics.getDeltaTime();
        
        if (enemy.x < lastAsteroidBatchX) {
          lastAsteroidBatchX = enemy.x;
      }
        // Check if the asteroid is off screen and increase score
        if (enemy.x + enemy.width < 0) {
          iter.remove();
          /*if (!gameover) {
            if(!toIncrementScore){
              toIncrementScore =true;
            }
            else {
              score++;
              toIncrementScore = false;
            }// Increment score when an asteroid is avoided
           
          }
          continue;*/ // Continue to next iteration
        }
        if(enemy.x < posX && enemy.x + 400 * Gdx.graphics.getDeltaTime() >= 100){
          if(!toIncrementScore){
            toIncrementScore =true;
          }
          else {
            score++;
            toIncrementScore = false;
          }
        }
  
        // Check for collision with the ship
        if (collide(enemy.x, enemy.y, enemy.width, enemy.height, posX, posY, nave.getWidth(), nave.getHeight())) {
          if (!gameover) {
            // Andrejs edit
            isBlinking = true;
            blinkStartTime = TimeUtils.nanoTime();
            paused = true; 
            timesCrashed++;
            //--power;
            //game.setScreen(new EnemyGameScreen(game, 4, 500, 30));
            // until here
            //if (power <= 0) {
            //  gameover = true;
            //}
            fadeOut = true;
            collisionTimer = 0;
          }
          iter.remove();
        }

        if(fadeOut){
          collisionTimer +=delta;
          if(collisionTimer>=1){
            game.setScreen(new EnemyGameScreen(game, timesCrashed, playerHealth, weaponStrength));
          }
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

        private void produceAsteroidPair(float delta) {
          timeSinceLastAsteroidPair += delta;
            //float timeSinceLastPair = (TimeUtils.nanoTime() - lastEnemyTime) / 1000000000f; // Convert to seconds
            if (timeSinceLastAsteroidPair > pairGenInterval) {
                float baseY = MathUtils.random(0, Gdx.graphics.getHeight() - tEnemy1.getHeight() * 2 - asteroidBatchDistance);
                float y1 = baseY;
                float y2 = baseY + tEnemy1.getHeight() + asteroidBatchDistance;
        
                createAsteroid(Gdx.graphics.getWidth(), y1);
                createAsteroid(Gdx.graphics.getWidth(), y2);
        
                timeSinceLastAsteroidPair = 0f;
          }
      
      }

      private void createAsteroid(float x, float y) {
          Rectangle asteroid = new Rectangle(x, y, tEnemy1.getWidth(), tEnemy1.getHeight());
          enemies1.add(asteroid);
      }
      
      public void restart(boolean isPaused) {
        //score = 0;
        //power = 3;
        if(!isPaused) score = 0;
        posX = 100;
        enemies1.clear();
        candies.clear();
        lastEnemyTime = TimeUtils.nanoTime();
        lastCandyTime = TimeUtils.nanoTime();
        gameover = false;
        paused = isPaused;
        isBlinking = false;
        fadeOutOpacity=0f;
        fadeOut=false;
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
