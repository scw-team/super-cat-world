package de.ggj21.scw.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
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
import de.ggj21.scw.world.actor.Tonno;
import de.ggj21.scw.world.actor.effect.StatusEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class GameWorld {

    private static final int[] BG_LAYERS = {0};
    private static final int[] MID_LAYERS = {1};
    private static final int[] FG_LAYERS = new int[]{2, 3};
    private static final Logger LOG = LogManager.getLogger(GameWorld.class);

    private static final float UNIT_SCALE = 1 / 24f;
    public static final float VIEWPORT_SCALE = 3 / 4f;
    private static final int VIEWPORT_WIDTH = 60;
    private static final int VIEWPORT_HEIGHT = 34;
    public static final String MAP_PROPERTY_TYPE_NAME = "type";


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
    private final float mapWidth;

    private LevelState state = LevelState.Running;


    public enum ObjectType {
        Wall("wall"),
        Cat("cat"),
        Tonno("fish");

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
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                final RectangleMapObject rectangleObject = (RectangleMapObject) object;
                if (ObjectType.Wall.key.equals(rectangleObject.getProperties().get("type"))) {
                    final Rectangle boundingBox = rectangleObject.getRectangle();
                    wallsAndPlatforms.add(boundingBox);
                    LOG.debug("Wall at: {}", boundingBox);
                }
            }

            final MapProperties properties = object.getProperties();
            // debug output
            final Iterator<String> keyIterator = properties.getKeys();
            while (keyIterator.hasNext()) {
                final String key = keyIterator.next();
                LOG.debug("Object {} has property {} = {}", object.getName(), key, properties.get(key));
            }
            if (ObjectType.Tonno.key.equals(properties.get(MAP_PROPERTY_TYPE_NAME))) {
                final Vector2 start = getObjectPosition(properties);
                LOG.debug("Fish can at position {}", start);
                this.actors.add(new Tonno(start, getCollisionHelperFactory(), soundManager, UNIT_SCALE));
            }
        }
        mapWidth = ((TiledMapTileLayer) map.getLayers().get("tiles")).getWidth() * 16 * UNIT_SCALE;


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
        final float y = properties.get("y", Float.class);
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
            public CollisionHelper getHelperForActor(GameActor actor) {
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
                        boolean result = false;
                        final Rectangle actorTargetBoundingBox = new Rectangle(desiredEnd.x + actor.getXOffset(), desiredEnd.y + actor.getYOffset(), actor.getWidth(), actor.getHeight());
                        for (Rectangle obstacle : wallsAndPlatforms) {
                            if (Intersector.overlaps(obstacle, actorTargetBoundingBox)) {
                                LOG.trace("Collision with environment detected");
                                result = true;
                            }
                        }
                        for (GameActor otherActor : actors) {
                            if (otherActor.equals(actor)) {
                                continue;
                            }
                            if (Intersector.overlaps(otherActor.getBoundingBox(), actorTargetBoundingBox)) {
                                LOG.debug("Collision with actor detected: {}", otherActor);
                                otherActor.interactWith(cat);
                                cat.interactWith(otherActor);
                                if (otherActor instanceof Pixel) {
                                    state = LevelState.Won;
                                }
                            }
                        }
                        return result;
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
        if (state == LevelState.Running && cat.isDead()) {
            LOG.info("Game loss");
            soundManager.playSound(SoundManager.Sounds.Death);
            state = LevelState.Lost;
        }

        if (state == LevelState.Running) {
            final ListIterator<GameActor> it = actors.listIterator();
            while (it.hasNext()) {
                final GameActor a = it.next();
                if (a.isDead()) {
                    it.remove();
                } else {
                    a.update(delta);
                }
            }
        }
    }

    public void render() {
        float cameraCenter = cat.getPosition().x * UNIT_SCALE;
        LOG.trace("Camera center is {} and map width is {}", cameraCenter, mapWidth);

        if (cameraCenter < VIEWPORT_WIDTH / 2f) {
            cameraCenter = VIEWPORT_WIDTH / 2f;
        } else if (cameraCenter > mapWidth - VIEWPORT_WIDTH / 2f) {
            cameraCenter = mapWidth - VIEWPORT_WIDTH / 2f;
        }

        camera.position.x = (cameraCenter - VIEWPORT_WIDTH) * 0.3f + VIEWPORT_WIDTH;
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render(BG_LAYERS);

        camera.position.x = (cameraCenter - VIEWPORT_WIDTH) * 0.6f + VIEWPORT_WIDTH;
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render(MID_LAYERS);

        LOG.trace("Camera position before: {}; and after: {}", camera.position, cat.getPosition());
        camera.position.x = cameraCenter;
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render(FG_LAYERS);
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        for (final GameActor a : actors) {
            a.render(spriteBatch);
        }
        spriteBatch.end();

        if (LOG.isDebugEnabled()) {
            Gdx.gl20.glEnable(GL20.GL_BLEND);
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 0, 0, 0.3f);
            for (final GameActor a : actors) {
                final Rectangle boundingBox = a.getBoundingBox();
                shapeRenderer.rect(boundingBox.x * UNIT_SCALE, boundingBox.y * UNIT_SCALE, boundingBox.width * UNIT_SCALE, boundingBox.height * UNIT_SCALE);
            }
            for (final Rectangle r : wallsAndPlatforms) {
                shapeRenderer.rect(r.x * UNIT_SCALE, r.y * UNIT_SCALE, r.width * UNIT_SCALE, r.height * UNIT_SCALE);
            }
            shapeRenderer.end();
            Gdx.gl20.glDisable(GL20.GL_BLEND);
        }

        renderStatusEffects(cat.getStatusEffects());
    }

    private void renderStatusEffects(List<StatusEffect> statusEffects) {
        spriteBatch.begin();
        for (int i = 0; i < statusEffects.size(); i++) {
            spriteBatch.draw(statusEffects.get(i).getIcon(),
                    camera.position.x - VIEWPORT_WIDTH / 2f + 0.5f,
                    getCameraHeight() * UNIT_SCALE * VIEWPORT_SCALE - (i + 1) - 0.5f, 1, 1);
        }
        spriteBatch.end();
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < statusEffects.size(); i++) {
            if (statusEffects.get(i).isPositive()) {
                shapeRenderer.setColor(0, 1, 0, 0.5f);
            } else {
                shapeRenderer.setColor(1, 0, 0, 0.5f);
            }
            shapeRenderer.rect(
                    camera.position.x - VIEWPORT_WIDTH / 2f + 2,
                    getCameraHeight() * UNIT_SCALE * VIEWPORT_SCALE - (i + 1) - 0.5f + 0.2f,
                    statusEffects.get(i).getRemainingDuration(),
                    0.6f
            );
        }
        shapeRenderer.end();
        Gdx.gl20.glDisable(GL20.GL_BLEND);
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
