package de.ggj21.scw.world;

import de.ggj21.scw.world.actor.GameActor;

public interface CollisionHelperFactory {

    /**
     * @param actor The actor for which collisions shall be checked.
     * @return A collision helper for the specified actor parameters.
     */
    CollisionHelper getHelperForActor(GameActor actor);
}
