package de.ggj21.scw.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.ggj21.scw.SuperCatWorld;
import de.ggj21.scw.actor.Cat;
import de.ggj21.scw.actor.GameActor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameScreen extends ScreenAdapter {
    private static final Logger LOG = LogManager.getLogger(GameScreen.class);

    private final SuperCatWorld game;

    private OrthographicCamera camera;
    private Viewport viewport;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch spriteBatch;

    private final List<GameActor> actors = new ArrayList<>();


    public GameScreen(SuperCatWorld game) {
        this.game = game;
    }

    @Override
    public void show() {
        final TiledMap map = new TmxMapLoader().load("levels/level1.tmx");
        final float unitScale = 1 / 16f;
        mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 60, 34);
        camera.update();
        viewport = new FitViewport(60, 34, camera);
        spriteBatch = new SpriteBatch();

        final MapObjects objects = map.getLayers().get("objects").getObjects();
        for (final MapObject o : objects) {
            final MapProperties properties = o.getProperties();
            if ("cat".equals(properties.get("type"))) {
                final Vector2 start = new Vector2(
                        properties.get("x", Float.class),
                        properties.get("y", Float.class));
                this.actors.add(new Cat(start, unitScale));
            }

            // debug output
            final Iterator<String> keyIterator = properties.getKeys();
            while (keyIterator.hasNext()) {
                final String key = keyIterator.next();
                LOG.info("Object {} has property {} = {}", o.getName(), key, properties.get(key));
            }

        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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

    @Override
    public void hide() {
        mapRenderer.dispose();
        spriteBatch.dispose();
        actors.clear();
    }
}
