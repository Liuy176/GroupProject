package com.mygdx.helpers;
import com.mygdx.sprites.Border;
import com.mygdx.sprites.Ground;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.sprites.Player;

public class WorldCreator {
    public WorldCreator(World world, TiledMap map){
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixture = new FixtureDef();
        Body body;
        Fixture fix;



        for(MapObject object: map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            new Ground(world, map, rect);
        }

        for (MapObject object : map.getLayers().get(2).getObjects().getByType(PolygonMapObject.class)) {
            PolygonMapObject polyObject = (PolygonMapObject) object;
            Polygon polygon = polyObject.getPolygon();

            PolygonShape polygonShape = new PolygonShape();
            float[] vertices = polygon.getTransformedVertices();
            float[] worldVertices = new float[vertices.length];
            //Vector2[] worldVertices = new Vector2[vertices.length / 2];

            for (int i = 0; i < vertices.length; ++i) {
                worldVertices[i] = vertices[i] / Constants.PPM;
            }
            
            polygonShape.set(worldVertices);

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(0 / Constants.PPM, 0 / Constants.PPM);

            body = world.createBody(bodyDef);
            fixture.shape = polygonShape;
            fixture.filter.categoryBits = Constants.CATEGORY_GROUND;
            fixture.filter.maskBits = Constants.CATEGORY_PLAYER | Constants.CATEGORY_ENEMY | Constants.CATEGORY_BULLET | Constants.CATEGORY_ENEMY_BULLET;
    
            fix = body.createFixture(fixture);
            fix.setUserData("ground");
            

            polygonShape.dispose();
        }
        for(MapObject object: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            new Border(world, map, rect);
        }
    }
}

