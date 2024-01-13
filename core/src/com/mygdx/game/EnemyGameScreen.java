package com.mygdx.game;

import com.mygdx.helpers.Constants;
import com.mygdx.helpers.TileMap;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.objects.player.Player;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;



public class EnemyGameScreen extends ScreenAdapter{
    private MyGdxGame game;
    
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private Viewport gamePort;

    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private TileMap tileMap;

    private Player player;
    


    public EnemyGameScreen(OrthographicCamera camera, MyGdxGame game) {
        this.game = game;
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0,-25f), true);
        this.box2DDebugRenderer = new Box2DDebugRenderer();

        this.tileMap = new TileMap(this);
        this.orthogonalTiledMapRenderer = tileMap.setupMap();
        this.gamePort = new FitViewport(game.V_WIDTH, game.v_HEIGHT, camera);
        this.camera.setToOrtho(false, 100, 100);
    }

    private void update() {
        world.step(1/60f, 6, 2);
        cameraUpdate();

        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);
        player.update();

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            // implement functionality to pause the game later
        }
    }

    private void cameraUpdate() {
        Vector3 position = camera.position;
        position.x = Math.round(player.getBody().getPosition().x * Constants.PPM * 10) / 10f;
        position.y = Math.round((player.getBody().getPosition().y +0.7)* Constants.PPM * 10) / 10f;
        camera.position.set(position);
        //camera.position.set(new Vector3(0,0,0));
        camera.update();
    }

    @Override
    public void render(float delta) {
        this.update();

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        orthogonalTiledMapRenderer.render();


        box2DDebugRenderer.render(world, camera.combined.scl(Constants.PPM));

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        // here we render objects

        batch.end();

    }

    public World getWorld() {
        return world;
    }

    public void setPlayer(Player player){
        this.player = player;
    }


    public void dispose(){
        tileMap.dispose();
        orthogonalTiledMapRenderer.dispose();
        world.dispose();
        box2DDebugRenderer.dispose();
    }
}
