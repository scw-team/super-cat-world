package de.ggj21.scw.world.actor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.ggj21.scw.world.CollisionHelper;

public interface GameActor {

    void update(float delta);

    void render(SpriteBatch batch);

}
