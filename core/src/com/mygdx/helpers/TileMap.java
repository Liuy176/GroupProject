package com.mygdx.helpers;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.EnemyGameScreen;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.math.Vector2;

import java.util.Vector;

public class TileMap {
    
    private TiledMap tiledMap;
    private EnemyGameScreen enemyScreen;

    public TileMap(EnemyGameScreen gameScreen){
        this.enemyScreen = gameScreen;
    }

    public OrthogonalTiledMapRenderer  setupMap() {
        tiledMap = new TmxMapLoader().load("map1.tmx");
        parseMapObjects(tiledMap.getLayers().get("Objects").getObjects());
        return new OrthogonalTiledMapRenderer(tiledMap);
    }

    private void parseMapObjects(MapObjects mapObjects){
        for(MapObject object : mapObjects){
            if(object instanceof PolygonMapObject){
                createStaticBody((PolygonMapObject) object);
            }
        }
    }

    private void createStaticBody(PolygonMapObject polygonObj) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = enemyScreen.getWorld().createBody(bodyDef);
        Shape shape = createPolygonShape(polygonObj);
        body.createFixture(shape, 1000);
        shape.dispose();
    }

    private Shape createPolygonShape(PolygonMapObject obj){
        float[] vertices = obj.getPolygon().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for(int i = 0; i<vertices.length/2; i++){
            Vector2 current = new Vector2(vertices[i*2]/Constants.PPM, vertices[i*2 +1]/Constants.PPM);
            worldVertices[i] = current;
        }

        PolygonShape shape = new PolygonShape();
        shape.set(worldVertices);
        return shape;
    }
}
