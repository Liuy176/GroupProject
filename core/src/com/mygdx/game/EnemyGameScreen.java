package com.mygdx.game;

import com.mygdx.helpers.Constants;
import com.mygdx.helpers.WorldContactListener;
import com.mygdx.helpers.WorldCreator;
import com.mygdx.sprites.Enemy;
//import com.mygdx.helpers.TileMap;
import com.mygdx.sprites.Player;
import com.mygdx.sprites.Bullet;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
//import com.mygdx.objects.player.Player;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;


public class EnemyGameScreen implements Screen{
    private MyGdxGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Player player;
    private Enemy enemy;
    private Enemy enemy2;

    private TextureAtlas atlas;

    private Array<Bullet> bullets;

    private float viewportWidth = 10;
    private float viewportHeight = 10;


    public EnemyGameScreen(MyGdxGame game){
        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        this.game = game;
        camera = new OrthographicCamera();
        //viewport = new FitViewport(800/Constants.PPM, 480/Constants.PPM, camera);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map2.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1/Constants.PPM);
        //camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        
        bullets = new Array<Bullet>();

        world = new World(new Vector2(0,-10), true);
        debugRenderer = new Box2DDebugRenderer();

        new WorldCreator(world, map);
        player = new Player(world, this);
        enemy = new Enemy(world, 200, 60, 2, player);
        enemy2 = new Enemy(world, 300, 60, 1, player);
        this.camera.setToOrtho(false, 10, 10);

        world.setContactListener(new WorldContactListener());
    }
    @Override
    public void show() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        debugRenderer.render(world, camera.combined);

        //game.getBatch().setProjectionMatrix(camera.combined.scl(Constants.PPM));        
        game.getBatch().begin();
        player.draw(game.getBatch());
        //for (Bullet bullet : bullets) {
        //    bullet.draw(game.getBatch());
        //}
        
        game.getBatch().end();
    }

    public void update(float dt){
        handleInput(dt);

        world.step(1/60f, 6, 2);
        //camera.position.x = player.body.getPosition().x;
        cameraUpdate();
        game.getBatch().setProjectionMatrix(camera.combined);
        player.update(dt);
        enemy.update(dt);
        enemy2.update(dt);

        for (int i = 0; i < bullets.size; i++) {
            Bullet bullet = bullets.get(i);
            bullet.update(dt);

                // Check if the projectile is off-screen and remove it
            if (bullet.toRemove) {
                bullets.removeIndex(i);
                world.destroyBody(bullet.getBody()); // Important: Destroy the Box2D body
                i--;
            }
        }
        //camera.update();
        
        renderer.setView(camera);
    }

    private void cameraUpdate(){
        Vector3 pos = camera.position;
        pos.x = player.body.getPosition().x;
        pos.y = player.body.getPosition().y;
        camera.position.set(pos);
        camera.update();

    }

    public void handleInput(float dt){
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            player.body.applyLinearImpulse(new Vector2(0,4f), player.body.getWorldCenter(), true);
        if(Gdx.input.isKeyPressed(Input.Keys.D) && player.body.getLinearVelocity().x <=3 )
            player.body.applyLinearImpulse(new Vector2(0.3f, 0), player.body.getWorldCenter(), true);
        if(Gdx.input.isKeyPressed(Input.Keys.A) && player.body.getLinearVelocity().x >=-3 )
            player.body.applyLinearImpulse(new Vector2(-0.3f, 0), player.body.getWorldCenter(), true);
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
            player.shoot();
        }
        
            
    }

    // This makes it only work for the players starting position, fix later
    private boolean isBulletOffScreen(Bullet bullet) {
        float playerX = player.body.getPosition().x; // Assuming you can get player's X position
        float playerY = player.body.getPosition().y; // Assuming you can get player's Y position
    
        float leftBound = playerX - viewportWidth / 2;
        float rightBound = playerX + viewportWidth / 2;
        float bottomBound = playerY - viewportHeight / 2;
        float topBound = playerY + viewportHeight / 2;
    
        // Convert bounds from meters to pixels (assuming bullet position is in pixels)
        leftBound *= Constants.PPM;
        rightBound *= Constants.PPM;
        bottomBound *= Constants.PPM;
        topBound *= Constants.PPM;
    
        // Check if bullet is outside the viewport centered on the player
        return bullet.getX() + bullet.getWidth() < leftBound ||
               bullet.getX() > rightBound ||
               bullet.getY() + bullet.getHeight() < bottomBound ||
               bullet.getY() > topBound;
    }
    

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void resize(int width, int height) {
        //viewport.update(width, height);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        debugRenderer.dispose();

    }
    
}
