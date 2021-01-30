package de.ggj21.scw.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.ggj21.scw.world.actor.Cat;
import de.ggj21.scw.world.actor.GameActor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameWorld {

    private static final Logger LOG = LogManager.getLogger(GameWorld.class);

    private static final float UNIT_SCALE = 1 / 16f;

    private final List<Rectangle> wallsAndPlatforms;
    private final List<GameActor> actors = new ArrayList<>();

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final SpriteBatch spriteBatch;


    public enum ObjectType {
        Wall("wall"),
        Cat("cat");

        String key;

        ObjectType(String key) {
            this.key = key;
        }
    }

    public GameWorld(TiledMap map) {
        wallsAndPlatforms = new ArrayList<>();
        final MapObjects objects = map.getLayers().get("objects").getObjects();
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
            if (ObjectType.Wall.key.equals(rectangleObject.getProperties().get("type"))) {
                final Rectangle boundingBox = rectangleObject.getRectangle();
                wallsAndPlatforms.add(boundingBox);
                LOG.debug("Wall at: {}", boundingBox);
            }
        }


        final MapObject o = objects.get("cat");
        final MapProperties properties = o.getProperties();
        final Vector2 start = new Vector2(
                properties.get("x", Float.class),
                properties.get("y", Float.class));
        this.actors.add(new Cat(start, UNIT_SCALE,
                new CollisionHelperFactory() {
                    @Override
                    public CollisionHelper getHelperForActor(float actorWidth, float actorHeight) {
                        return new CollisionHelper() {
                            @Override
                            public Vector2 resolve(Vector2 start, Vector2 desiredEnd) {
                                return desiredEnd;
                            }
                        };
                    }
                }));

        // debug output
        final Iterator<String> keyIterator = properties.getKeys();
        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();
            LOG.info("Object {} has property {} = {}", o.getName(), key, properties.get(key));
        }

        mapRenderer = new OrthogonalTiledMapRenderer(map, UNIT_SCALE);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 60, 34);
        camera.update();
        viewport = new FitViewport(60, 34, camera);
        spriteBatch = new SpriteBatch();
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void render(float delta) {
        mapRenderer.setView(camera);
        mapRenderer.render();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        for (final GameActor a : actors) {
            a.update(delta);
            a.render(spriteBatch);
        }
        spriteBatch.end();
    }

    public void dispose() {
        mapRenderer.dispose();
        spriteBatch.dispose();
        actors.clear();
        wallsAndPlatforms.clear();
    }

}
