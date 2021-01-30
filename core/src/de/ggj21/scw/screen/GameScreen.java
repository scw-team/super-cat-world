package de.ggj21.scw.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import de.ggj21.scw.actor.Cat;
import de.ggj21.scw.actor.GameActor;

import java.util.ArrayList;
import java.util.List;

public class GameScreen extends ScreenAdapter {

    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private SpriteBatch spriteBatch;

    private final List<GameActor> actors = new ArrayList<>();

    @Override
    public void show() {
        final TiledMap map = new TmxMapLoader().load("levels/level1.tmx");
        final float unitScale = 1 / 16f;
        mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 60, 34);
        camera.update();
        spriteBatch = new SpriteBatch();
        this.actors.add(new Cat(0, 600));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mapRenderer.setView(camera);
        mapRenderer.render();
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
