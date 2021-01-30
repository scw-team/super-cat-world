package de.ggj21.scw.world;

import com.badlogic.gdx.math.Vector2;

public interface CollisionHelper {

    /**
     * Checks a position change for collision with other world objects.
     *
     * @param start      Where the actor has started.
     * @param desiredEnd Where the actor wants to go.
     * @return If free of collision, desiredEnd will be returned. Otherwise, the closest collision-free point on the way there.
     */
    Vector2 resolve(Vector2 start, Vector2 desiredEnd);

}
