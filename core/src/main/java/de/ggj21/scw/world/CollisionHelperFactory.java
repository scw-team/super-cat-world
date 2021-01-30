package de.ggj21.scw.world;

public interface CollisionHelperFactory {

    CollisionHelper getHelperForActor(float actorWidth, float actorHeight);
}
