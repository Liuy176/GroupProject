package com.mygdx.game;

import com.mygdx.helpers.Constants;
import com.mygdx.helpers.WorldContactListener;
import com.mygdx.helpers.WorldCreator;
import com.mygdx.sprites.Enemy;
import com.mygdx.sprites.EnemyBullet;
import com.mygdx.sprites.Player;
import com.mygdx.sprites.Bullet;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class EnemyGameScreen implements Screen{
    private MyGdxGame game;
    private OrthographicCamera camera;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Player player;

    private TextureAtlas atlas;

    private Array<Bullet> bullets;
    private Array<EnemyBullet> enemyBullets;
    private Array<Enemy> enemies;

    private float viewportWidth = 10;
    private float viewportHeight = 10;

    private float tileSize;
    private int mapWidthTiles;
    private int mapHeightTiles;
    private float mapWidth;
    private float mapHeight;
    private BitmapFont font;

    private Texture backgroundTexture;
    private float backgroundScaleX = 0;
    private float backgroundScaleY = 0;

    private float fade = 0f;
    public boolean startFade = false;
    private float fadeDuration = 3f; 
    private ShapeRenderer shapeRenderer;

    private int roundNumber;
    private Random random;


    public EnemyGameScreen(MyGdxGame game, int roundNumber){
        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        this.game = game;
        camera = new OrthographicCamera();
        //viewport = new FitViewport(800/Constants.PPM, 480/Constants.PPM, camera);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map2.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1/Constants.PPM);
        //camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        
        bullets = new Array<Bullet>();
        enemyBullets = new Array<EnemyBullet>();

        world = new World(new Vector2(0,-10), true);
        debugRenderer = new Box2DDebugRenderer();

        new WorldCreator(world, map);
        player = new Player(world, this);

        //
        random = new Random();
        enemies = new Array<Enemy>();
        int enemyCount = 1+roundNumber;

        for(int i = 0; i<enemyCount; i++ ){
            int enemyHealth = 60 + random.nextInt(61) + 60*(roundNumber/4);
            float enemySpeed = 1 + random.nextInt(3) +(roundNumber/4); 
            int x = 450 + random.nextInt(300);
            int y = 150;

            enemies.add(new Enemy(world, x,y, enemySpeed, enemyHealth, player, this));
        }
        //

        this.camera.setToOrtho(false, 10, 10);

        world.setContactListener(new WorldContactListener());

        tileSize = 16;
        mapWidthTiles = map.getProperties().get("width", Integer.class);
        mapHeightTiles = map.getProperties().get("height", Integer.class);
        mapWidth = mapWidthTiles * tileSize / Constants.PPM;
        mapHeight = mapHeightTiles * tileSize / Constants.PPM;

    }
    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1);
        backgroundTexture = new Texture("9.png");

        float mapPixelWidth = mapWidthTiles * tileSize;
        float mapPixelHeight = mapHeightTiles * tileSize;

        backgroundScaleX = mapPixelWidth / backgroundTexture.getWidth()/Constants.PPM;
        backgroundScaleY = mapPixelHeight / backgroundTexture.getHeight()/Constants.PPM;
        
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getBatch().begin();
        
        game.getBatch().draw(backgroundTexture, 0, 0, backgroundTexture.getWidth() * backgroundScaleX, backgroundTexture.getHeight() * backgroundScaleY);
        game.getBatch().setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        player.drawHealthBar(game.getBatch(), font);

        game.getBatch().end();

        renderer.render();
        debugRenderer.render(world, camera.combined);

        game.getBatch().setProjectionMatrix(camera.combined);
        //game.getBatch().setProjectionMatrix(camera.combined.scl(Constants.PPM));        
        game.getBatch().begin();
        player.draw(game.getBatch());
        //for (Bullet bullet : bullets) {
        //    bullet.draw(game.getBatch());
        //}
        for (Enemy enemy : enemies) {
            enemy.drawHealthBar(game.getBatch());
        }
        game.getBatch().end();

        if (startFade) {
            fade += delta / fadeDuration;
            if (fade > 1) {
                fade = 1;
                game.setScreen(new GameOverScreen(game)); 
            }
        }
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, fade);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.getBatch().setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        game.getBatch().begin();
        player.drawHealthBar(game.getBatch(), font);
        game.getBatch().end();
    }

    public void update(float dt){
        handleInput(dt);

        world.step(1/60f, 6, 2);
        //camera.position.x = player.body.getPosition().x;
        cameraUpdate();
        game.getBatch().setProjectionMatrix(camera.combined);
        player.update(dt);
        for (Enemy enemy : enemies) {
            enemy.update(dt);
        }

        for (int i = 0; i < bullets.size; i++) {
            Bullet bullet = bullets.get(i);
            bullet.update(dt);

                
                if (bullet.toRemove) {
                    bullets.removeIndex(i);
                    world.destroyBody(bullet.getBody()); 
                    i--;
                }
        }
        for (int j = 0; j < enemyBullets.size; j++) {
            EnemyBullet bullet = enemyBullets.get(j);
            bullet.update(dt);

                
                if (bullet.toRemove) {
                    enemyBullets.removeIndex(j);
                    world.destroyBody(bullet.getBody()); 
                    j--;
                }
        }
        //camera.update();
        
        renderer.setView(camera);
    }

    /*private void cameraUpdate(){
        Vector3 pos = camera.position;
        pos.x = player.body.getPosition().x;
        pos.y = player.body.getPosition().y;
        camera.position.set(pos);
        camera.update();

    }*/

    private void cameraUpdate(){
        if(!player.isDefeated){
            Vector3 position = camera.position;

            float camMinX = viewportWidth / 2;
            float camMaxX = mapWidth - viewportWidth / 2;
            float camMinY = viewportHeight / 2;
            float camMaxY = mapHeight - viewportHeight / 2;

            //making sure we can't see the area outside of the map when player comes closer to the edge of the map
            position.x = MathUtils.clamp(player.body.getPosition().x, camMinX, camMaxX);
            position.y = MathUtils.clamp(player.body.getPosition().y, camMinY, camMaxY);
            camera.position.set(position);
        }
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
    

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public void addEnemyBullet(EnemyBullet bullet) {
        enemyBullets.add(bullet);
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
        font.dispose();
        backgroundTexture.dispose();

    }
    
}
