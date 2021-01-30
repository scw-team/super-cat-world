package de.ggj21.scw.world;

public interface CollisionHelperFactory {

    /**
     * @param actorWidth   The width of the actor body in world coordinates.
     * @param actorHeight  The height of the actor body in world coordinates.
     * @param actorXOffset The X offset in world coordinates from the actor's position to its leftmost body part.
     * @param actorYOffset The Y offset in world coordinates from the actor's position to its lowest body part.
     * @return A collision helper for the specified actor parameters.
     */
    CollisionHelper getHelperForActor(float actorWidth, float actorHeight, float actorXOffset, float actorYOffset);
}
