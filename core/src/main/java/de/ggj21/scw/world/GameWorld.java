package de.ggj21.scw.world;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.ggj21.scw.SoundManager;
import de.ggj21.scw.world.actor.Cat;
import de.ggj21.scw.world.actor.GameActor;
import de.ggj21.scw.world.actor.Pixel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameWorld {

    private static final Logger LOG = LogManager.getLogger(GameWorld.class);

    private static final float UNIT_SCALE = 1 / 16f;
    private static final float VIEWPORT_SCALE = 1 / 2f;
    private static final int VIEWPORT_WIDTH = 60;
    private static final int VIEWPORT_HEIGHT = 34;

    private final List<Rectangle> wallsAndPlatforms;
    private final List<GameActor> actors = new ArrayList<>();

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;
    private final InputProcessor inputProcessor;
    private final Cat cat;
    private final SoundManager soundManager;

    private LevelState state = LevelState.Running;


    public enum ObjectType {
        Wall("wall"),
        Cat("cat");

        String key;

        ObjectType(String key) {
            this.key = key;
        }
    }

    public GameWorld(TiledMap map, SoundManager soundManager) {
        this.soundManager = soundManager;
        wallsAndPlatforms = new ArrayList<>();
        final MapLayer objectLayer = map.getLayers().get("objects");
        final MapObjects objects = objectLayer.getObjects();
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
            if (ObjectType.Wall.key.equals(rectangleObject.getProperties().get("type"))) {
                final Rectangle boundingBox = rectangleObject.getRectangle();
                wallsAndPlatforms.add(boundingBox);
                LOG.debug("Wall at: {}", boundingBox);
            }
        }


        final Cat cat = getCat(soundManager, objects);
        this.cat = cat;
        this.actors.add(cat);
        this.inputProcessor = cat.getInputProcessor();

        this.actors.add(getPixel(soundManager, objects));


        mapRenderer = new OrthogonalTiledMapRenderer(map, UNIT_SCALE);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.update();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    private Cat getCat(SoundManager soundManager, MapObjects objects) {
        final MapObject o = objects.get("cat");
        final MapProperties properties = o.getProperties();
        final Vector2 start = getObjectPosition(properties);
        final Cat cat = new Cat(start, getCollisionHelperFactory(), soundManager, UNIT_SCALE);
        // debug output
        final Iterator<String> keyIterator = properties.getKeys();
        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();
            LOG.info("Object {} has property {} = {}", o.getName(), key, properties.get(key));
        }
        return cat;
    }

    private Vector2 getObjectPosition(MapProperties properties) {
        final float y = getCameraHeight() - properties.get("y", Float.class);
        final Vector2 start = new Vector2(properties.get("x", Float.class), y);
        return start;
    }

    private Pixel getPixel(SoundManager soundManager, MapObjects objects) {
        final MapObject o = objects.get("pixel");
        final MapProperties properties = o.getProperties();
        final Vector2 start = getObjectPosition(properties);
        LOG.debug("Added pixel");
        return new Pixel(start, getCollisionHelperFactory(), soundManager, UNIT_SCALE);
    }

    private CollisionHelperFactory getCollisionHelperFactory() {
        return new CollisionHelperFactory() {
            @Override
            public CollisionHelper getHelperForActor(float actorWidth, float actorHeight, float actorXOffset, float actorYOffset) {
                return new CollisionHelper() {
                    @Override
                    public Vector2 resolve(Vector2 start, Vector2 desiredEnd) {
                        if (checkForConflict(desiredEnd)) {
                            return start;
                        } else {
                            return desiredEnd;
                        }
                    }

                    private boolean checkForConflict(Vector2 desiredEnd) {
                        final Rectangle actor = new Rectangle(desiredEnd.x + actorXOffset, desiredEnd.y + actorYOffset, actorWidth, actorHeight);
                        for (Rectangle obstacle : wallsAndPlatforms) {
                            if (Intersector.overlaps(obstacle, actor)) {
                                LOG.trace("Collision detected");
                                return true;
                            }
                        }
                        return false;
                    }
                };
            }
        };
    }

    public Viewport getViewport() {
        return viewport;
    }

    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

    public LevelState getState() {
        return state;
    }

    public void update(float delta) {
        for (final GameActor a : actors) {
            a.update(delta);
        }

        if (state == LevelState.Running && cat.getPosition().y < 0) {
            LOG.info("Game loss");
            soundManager.playSound(SoundManager.Sounds.Death);
            state = LevelState.Lost;
        } else {

        }
    }

    public void render() {
        camera.position.x = (cat.getPosition().x * UNIT_SCALE - VIEWPORT_WIDTH) * 0.3f + VIEWPORT_WIDTH;
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render(new int[]{0});

        LOG.trace("Camera position before: {}; and after: {}", camera.position, cat.getPosition());
        camera.position.x = cat.getPosition().x * UNIT_SCALE;
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render(new int[]{1, 2});
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        for (final GameActor a : actors) {
            a.render(spriteBatch);
        }
        spriteBatch.end();
    }

    private boolean checkIntersection(GameActor a, GameActor b) {
        return false;
    }

    public void dispose() {
        mapRenderer.dispose();
        actors.clear();
        wallsAndPlatforms.clear();
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }

    private static float getCameraWidth() {
        return VIEWPORT_WIDTH / (UNIT_SCALE * VIEWPORT_SCALE);
    }

    private static float getCameraHeight() {
        return VIEWPORT_HEIGHT / (UNIT_SCALE * VIEWPORT_SCALE);
    }

    public enum LevelState {
        Running,
        Won,
        Lost;
    }

}
