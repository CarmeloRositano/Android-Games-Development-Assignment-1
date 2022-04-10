package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Player {

    public enum PlayerState { RUNNING, DYING, DEAD, SHOOTING;}

    private static final float GRAVITY = 70f;
    private static float MovementSpeed = 200.0f;
    private static float ConstantSpeed = 150.0f;

    ShapeRenderer shapeRenderer;

    PlayerState currentState;

    private TextureRegion currentFrame;
    Sprite sprite;
    Vector2 delta;

    boolean isShooting;
    boolean canJump;
    float dt;
    float stateTime;

    //Player - Walking
    Texture walkingTexture;
    private final Animation<TextureRegion> walkingAnimation;
    //Dying
    Texture dyingTexture;
    private final Animation<TextureRegion> dyingAnimation;
    //Shooting
    Texture shootingTexture;
    private final Animation<TextureRegion> shootingAnimation;
    ArrayList<PlayerProjectile> bullets;
    Sound shoot;

    /**
     * Constructs the player class. Initializing all variables and building all animations
     * (Walking, Dying, Shooting)
     * @param bullets
     */
    public Player(ArrayList<PlayerProjectile> bullets) {
        currentState = PlayerState.RUNNING;

        shapeRenderer = new ShapeRenderer();

        this.bullets = bullets;

        shoot = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.mp3"));

        sprite = new Sprite();
        sprite.setSize(256,256);
        delta = new Vector2();

        //Player Walking Texture and Animation Build
        int FrameCol = 3;
        int FrameRow = 6;
        walkingTexture = new Texture("player/moving.png");
        TextureRegion[][] walkTemp = TextureRegion.split(walkingTexture, walkingTexture.getWidth() / FrameCol,
                walkingTexture.getHeight() / FrameRow);
        TextureRegion[] walkingFrames = new TextureRegion[FrameCol * FrameRow];
        int index = 0;
        for (int i = 0; i < FrameRow; i++) {
            for (int j = 0; j < FrameCol; j++) {
                walkingFrames[index++] = walkTemp[i][j];
            }
        }
        walkingAnimation = new Animation(1f/30f, (Object[]) walkingFrames);

        //Player Dying Texture and Animation Build
        FrameCol = 5;
        FrameRow = 4;
        dyingTexture = new Texture("player/dying.png");
        TextureRegion[][] dyingTemp = TextureRegion.split(dyingTexture, dyingTexture.getWidth() / FrameCol,
                dyingTexture.getHeight() / FrameRow);
        TextureRegion[] dyingFrames = new TextureRegion[(FrameCol * FrameRow) - 2];
        index = 0;
        for (int i = 0; i < FrameRow; i++) {
            for (int j = 0; j < FrameCol; j++) {
                if(index < 18) {
                    dyingFrames[index++] = dyingTemp[i][j];
                }
            }
        }
        dyingAnimation = new Animation(1f/30f, (Object[]) dyingFrames);

        //player shooting Texture and Animation Build
        FrameCol = 3;
        FrameRow = 4;
        shootingTexture = new Texture("player/shooting.png");
        TextureRegion[][] shootingTemp = TextureRegion.split(shootingTexture, shootingTexture.getWidth() / FrameCol,
                shootingTexture.getHeight() / FrameRow);
        TextureRegion[] playerShootingFrames = new TextureRegion[(FrameCol * FrameRow) - 2];
        index = 0;
        for (int i = 0; i < FrameRow; i++) {
            for (int j = 0; j < FrameCol; j++) {
                if(index < 10) {
                    playerShootingFrames[index++] = shootingTemp[i][j];
                }
            }
        }
        shootingAnimation = new Animation (1f/30f, (Object[]) playerShootingFrames);

        isShooting = false;
        canJump = false;
        dt = 0.0f;
        stateTime = 0.0f;
    }

    /**
     * Updates self depending on its current state as well as the game state as well as self state.
     * Updates the animation so the player is in the correct animation.
     * @param gameState the state of the game to determine if enemy should be moving and how fast
     */
    public void updateCurrentState(MyGdxGame.GameState gameState) {
        if(gameState == MyGdxGame.GameState.PAUSED) return;
        stateTime += Gdx.graphics.getDeltaTime();

        switch (currentState) {
            case RUNNING:
                currentFrame = (TextureRegion) walkingAnimation.getKeyFrame(stateTime, true);
                sprite.setRegion(currentFrame);
                break;

            case DYING:
                currentFrame = (TextureRegion) dyingAnimation.getKeyFrame(stateTime, false);
                sprite.setRegion(currentFrame);
                if(dyingAnimation.isAnimationFinished(stateTime)) {
                    currentState = PlayerState.DEAD;
                }
                break;

            case DEAD:
                MovementSpeed = ConstantSpeed = 0;
                break;

            case SHOOTING:
                currentFrame = (TextureRegion) shootingAnimation.getKeyFrame(stateTime, true);
                sprite.setRegion(currentFrame);
                if(shootingAnimation.isAnimationFinished(stateTime)) {
                    currentState = PlayerState.RUNNING;
                    isShooting = false;
                }
                break;

        }
        sprite.setRegion(currentFrame);
    }

    /**
     * Translates sprite in relation to the given x, y value. Checks collision on the map and
     * keeps self within the viewport
     * @param x +1 if moving right -1 if moving left
     * @param y +1 if moving up -1 if moving down
     * @param collisionLayer the collision data that is given through map class
     * @param camera the camera to determine view port and where player is in relation
     */
    public void move(int x, int y, TiledMapTileLayer collisionLayer, Camera camera) {

        //If player is within viewport
        if (sprite.getX() < camera.position.x - camera.viewportWidth * 0.5f) {
            sprite.setPosition(camera.position.x - camera.viewportWidth * 0.5f, sprite.getY());
            delta.x = 0f;
        } else if (sprite.getX() > (camera.position.x + camera.viewportWidth * 0.5f) - sprite.getWidth()) {
            sprite.setPosition((camera.position.x + camera.viewportWidth * 0.5f) - sprite.getWidth(), sprite.getY());
            delta.x = 0f;
        } else {
            this.delta.x = x * MovementSpeed * dt;
        }

        //If player is touching the ground
        if(collidesBottom(collisionLayer)) {
            //Player jump
            if(y == 1 && currentState != PlayerState.DEAD && currentState != PlayerState.DYING) {
                //Mario Style arc jump
                stateTime = 0f;
                this.delta.y = (sprite.getY() - this.delta.y * dt) / 2;
            } else {
                this.delta.y = y * MovementSpeed * dt;
            }
        //Player is in the air (Applies gravity)
        } else {
            this.delta.y = (this.delta.y - GRAVITY * dt);
        }

        //Make sure player does not fall into ground
        if (sprite.getY() < 61) {
            sprite.setPosition(sprite.getX(), 61);
        }

        sprite.translate(this.delta.x, this.delta.y);
    }

    /**
     * checks if coordinates around self have any collision tiles and returns results
     * @param x x coordinates for spawn
     * @param y y coordinates for spawn
     * @param collisionLayer the collision data that is given through map class
     * @return if self is going to be blocked by any collision map tiles
     */
    private boolean isCellBlocked(float x, float y, TiledMapTileLayer collisionLayer) {
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
        return cell != null && cell.getTile() != null;
    }

    /**
     * checks cells around given coordinates on the given collisionlayer to determine if projectile
     * @param collisionLayer the collision data that is given through map class
     * @return if self is going to be blocked by any collision map tiles
     */
    public boolean collidesBottom(TiledMapTileLayer collisionLayer) {
        for(float step = 0; step < sprite.getWidth(); step += collisionLayer.getTileWidth() / 2f) {
            if(isCellBlocked(sprite.getX() + step, sprite.getY(), collisionLayer)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a custom hit box and returns it
     * @return  Rectangle that is the hit box
     */
    public Rectangle getHitBox() {
        return new Rectangle(sprite.getX() + sprite.getWidth() * 0.3f,
                sprite.getY(),
                sprite.getWidth() * 0.54f,
                sprite.getHeight() * 0.8f);
    }

    /**
     * Shoots projectile if the not already shooting or if there are 4 or more bullets already out.
     * Chances current state to shooting.
     */
    public void shoot() {
        if(isShooting) return;
        if(bullets.size() == 4) return;
        bullets.add(new PlayerProjectile(sprite.getX() + sprite.getWidth() * 0.8f, (sprite.getY()) + (sprite.getHeight() * 0.42f)));
        stateTime = 0f;
        currentState = PlayerState.SHOOTING;
        shoot.setVolume(shoot.play(), 0.2f);
        isShooting = true;
    }

    /**
     * Sets the current state to dying
     */
    public void setDying() {
        currentState = PlayerState.DYING;
        stateTime = 0f;
    }

    /**
     * Sets the current state to moving
     */
    public void setAlive() {
        currentState = PlayerState.RUNNING;
        stateTime = 0f;
    }

    /**
     * sets variables to base state for when the game is restarted
     */
    public void newGame() {
        MovementSpeed = 200.0f;
        ConstantSpeed = 150.0f;
    }

    /**
     * Dispose of variables
     */
    public void dispose() {
        walkingTexture.dispose();
        dyingTexture.dispose();
        shootingTexture.dispose();
        shapeRenderer.dispose();
        shoot.dispose();
    }

    /**
     * Getter for constant speed
     * @return ConstantSpeed
     */
    public static float getConstantSpeed() {
        return ConstantSpeed;
    }

}
