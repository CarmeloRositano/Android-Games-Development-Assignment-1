package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GroundEnemy{

    public enum EnemyState { MOVING, DYING, DEAD }
    private float constantSpeed = 100f;

    EnemyState enemyState;
    Sprite sprite;
    Vector2 delta;

    //Enemy Walking
    private final Texture walkingTexture = new Texture("ground_enemy/moving.png"); //Set here to stop lag from loading texture from drive
    private final Animation<TextureRegion> walkingAnimation;

    //Enemy Dying
    private final Texture dyingTexture = new Texture("ground_enemy/dying.png"); //Set here to stop lag from loading texture from drive
    private final Animation<TextureRegion> dyingAnimation;
    float stateTime;

    /**
     * Creates a GroundEnemy. sets its state to MOVING and initializes all variables.
     * Builds all animations that are used (Walking and Dying) and sets sprite position
     * @param x x coordinates for spawn
     * @param y y coordinates for spawn
     */
    public GroundEnemy(float x, float y) {
        enemyState = EnemyState.MOVING;
        stateTime = 0.0f;

        sprite = new Sprite();
        delta = new Vector2();

        //Walking
        int frameCol = 6;
        int frameRow = 3;

        sprite.setSize(walkingTexture.getWidth() / (float) frameCol * 0.7f
                , walkingTexture.getHeight() / (float) frameRow * 0.7f);

        TextureRegion[][] walkTemp = TextureRegion.split(walkingTexture, walkingTexture.getWidth() / frameCol,
                walkingTexture.getHeight() / frameRow);
        TextureRegion[] walkingFrames = new TextureRegion[frameCol * frameRow];
        int index = 0;
        for (int i = 0; i < frameRow; i++) {
            for (int j = 0; j < frameCol; j++) {
                walkingFrames[index++] = walkTemp[i][j];
            }
        }
        walkingAnimation = new Animation(1f/30f, (Object[]) walkingFrames);
        //Dying
        frameCol = 6;
        frameRow = 5;
        TextureRegion[][] dyingTemp = TextureRegion.split(dyingTexture, dyingTexture.getWidth() / frameCol,
                dyingTexture.getHeight() / frameRow);
        TextureRegion[] dyingFrames = new TextureRegion[(frameCol * frameRow) - 4];
       index = 0;
        for (int i = 0; i < frameRow; i++) {
            for (int j = 0; j < frameCol; j++) {
                if(index < 26)
                dyingFrames[index++] = dyingTemp[i][j];
            }
        }
        dyingAnimation = new Animation(1f/30f, (Object[]) dyingFrames);

        sprite.setPosition(x, y);

    }

    /**
     * Updates self depending on its current state as well as the game state. Updates the
     * animation so the enemy is in the correct animation.
     * @param gameMap game map to determine speed
     * @param gameState the state of the game to determine if enemy should be moving and how fast
     */
    public void updateCurrentState(TiledGameMap gameMap, MyGdxGame.GameState gameState) {
        if(gameState == MyGdxGame.GameState.PAUSED) return;
        stateTime += Gdx.graphics.getDeltaTime();

        switch (enemyState) {
            case MOVING:
                TextureRegion currentFrame = (TextureRegion) walkingAnimation.getKeyFrame(stateTime, true);
                sprite.setRegion(currentFrame);
                break;

            case DYING:
                currentFrame = (TextureRegion) dyingAnimation.getKeyFrame(stateTime, false);
                if(dyingAnimation.isAnimationFinished(stateTime)) {
                    enemyState = EnemyState.DEAD;
                    //make enemy move at the speed of the ground when dead
                    constantSpeed = ((Player.getConstantSpeed() + (gameMap.timeElapsed)) * 0.05f) / Gdx.graphics.getDeltaTime();
                    break;
                }
                sprite.setRegion(currentFrame);
                break;

            case DEAD:
                //make enemy move at the speed of the ground when dead
                constantSpeed = ((Player.getConstantSpeed() + (gameMap.timeElapsed)) * 0.05f) / Gdx.graphics.getDeltaTime();
                break;
        }
    }

    /**
     * Moves self at a constant speed and moves self down slightly when dying to give appearance
     * of being on ground
     * @param dt Delta Time to give smoother movement
     */
    public void move(float dt) {
        this.delta.x = -constantSpeed * dt;
        sprite.translate(delta.x, delta.y);
        if(enemyState == EnemyState.DYING) {
            sprite.translateY(-0.8f);
        }

    }

    /**
     * Creates a custom hit box and returns it
     * @return  Rectangle that is the hit box
     */
    public Rectangle getHitBox() {
        return new Rectangle(sprite.getX() + sprite.getWidth() * 0.15f,
                sprite.getY(),
                sprite.getWidth() * 0.7f,
                sprite.getHeight() * 0.6f);
    }

    /**
     * Draws the sprite to screen on the provided Batch
     * @param batch The Batch to draw the text to screen
     */
    public void draw(SpriteBatch batch) {
        batch.begin();
        sprite.draw(batch);
        batch.end();
    }

    /**
     * Sets the current state to dying
     */
    public void setDying() {
        enemyState = EnemyState.DYING;
        stateTime = 0f;
    }

    /**
     * Sets the current state to moving
     */
    public void setAlive() {
        enemyState = EnemyState.MOVING;
        stateTime = 0f;
        constantSpeed = 100f;
    }

    /**
     * Dispose of variables
     */
    public void dispose() {
        dyingTexture.dispose();
        walkingTexture.dispose();
    }

}
