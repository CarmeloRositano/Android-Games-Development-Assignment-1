package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;

import org.w3c.dom.Text;

public class Player {

    public enum PlayerState { RUNNING, DYING, DEAD, SHOOTING }

    private static float MOVEMENT_SPEED = 200.0f;
    private static float CONSTANT_SPEED = 150.0f;
    private static final float GRAVITY = 70f;

    boolean canJump;
    boolean playerAlive;

    PlayerState currentPlayerState;

    Sprite playerSprite = new Sprite();

    Texture playerTexture;
    Vector2 playerDelta;
    Rectangle playerDeltaRectangle;

    Texture playerWalkingTexture;
    private TextureRegion[] playerWalkingFrames;
    private Animation playerWalkingAnimation;

    Texture playerDyingTexture;
    private TextureRegion[] playerdyingFrames;
    private Animation playerDyingAnimation;

    //Game Clock
    float dt;
    float stateTime;
    private TextureRegion currentFrame;

    public Player() {
        currentPlayerState = PlayerState.RUNNING;

        playerSprite.setSize(256,256);
        playerDelta = new Vector2();
        playerDeltaRectangle = new Rectangle(playerSprite.getX(), playerSprite.getY(), playerSprite.getWidth(), playerSprite.getHeight());

        //Player Walking Texture Build
        int walkingFrameCol = 3;
        int WalkingFrameRow = 6;
        playerWalkingTexture = new Texture("player/moving.png");
        TextureRegion[][] walkTemp = TextureRegion.split(playerWalkingTexture, playerWalkingTexture.getWidth() / walkingFrameCol, playerWalkingTexture.getHeight() / WalkingFrameRow);
        playerWalkingFrames = new TextureRegion[walkingFrameCol * WalkingFrameRow];
        int index = 0;
        for (int i = 0; i < WalkingFrameRow; i++) {
            for (int j = 0; j < walkingFrameCol; j++) {
                playerWalkingFrames[index++] = walkTemp[i][j];
            }
        }
        playerWalkingAnimation = new Animation(1f/30f, playerWalkingFrames);

        //Player dying texture build
        int dyingFrameCol = 5;
        int dyingFrameRow = 4;
        playerDyingTexture = new Texture("player/dying.png");
        TextureRegion[][] dyingTemp = TextureRegion.split(playerDyingTexture, playerDyingTexture.getWidth() / dyingFrameCol, playerDyingTexture.getHeight() / dyingFrameRow);
        playerdyingFrames = new TextureRegion[(dyingFrameCol * dyingFrameRow) - 2];
        index = 0;
        for (int i = 0; i < dyingFrameRow; i++) {
            for (int j = 0; j < dyingFrameCol; j++) {
                if(index < 18) {
                    playerdyingFrames[index++] = dyingTemp[i][j];
                }
            }
        }
        playerDyingAnimation = new Animation(0.033f, playerdyingFrames);

        updateCurrentPlayerState();

        canJump = false;
        playerAlive = true;
        dt = 0.0f;
        stateTime = 0.0f;
    }

    //Updates the currentPlayerState to determine what animation that player sprite should be in
    public void updateCurrentPlayerState() {

        stateTime += Gdx.graphics.getDeltaTime();


        switch (currentPlayerState) {
            case RUNNING:
                currentFrame = (TextureRegion) playerWalkingAnimation.getKeyFrame(stateTime, true);
                break;

            case DYING:
                currentFrame = (TextureRegion) playerDyingAnimation.getKeyFrame(stateTime, false);
                playerSprite.setRegion(currentFrame);
                if(playerDyingAnimation.isAnimationFinished(stateTime)) {
                    currentPlayerState = PlayerState.DEAD;
                }
                break;

            case DEAD:
                MOVEMENT_SPEED = CONSTANT_SPEED = 0;
                break;

            case SHOOTING:

                break;

        }
        playerSprite.setRegion(currentFrame);
    }

    //Moves the player
    public void movePlayer(int x, int y, TiledMapTileLayer collisionLayer) {

        //Determine player position in view port
        playerDeltaRectangle.x += (x * MOVEMENT_SPEED * dt);

        //Make player not move out of bounds
        if(playerDeltaRectangle.x <= -(Gdx.graphics.getWidth() / 2) || playerDeltaRectangle.x >= Gdx.graphics.getWidth() / 2) {
            this.playerDelta.x = (CONSTANT_SPEED * dt);
        } else {
            this.playerDelta.x = ((x * MOVEMENT_SPEED * dt) + CONSTANT_SPEED * dt);
        }

        if(collidesBottom(collisionLayer)) {
            if(y == 1 && currentPlayerState != PlayerState.DEAD && currentPlayerState != PlayerState.DYING) {
                //Mario Style arc jump
                this.playerDelta.y = (playerSprite.getY() - this.playerDelta.y * dt) / 2;
            } else {
                this.playerDelta.y = ((y * MOVEMENT_SPEED * dt));
            }
        } else {
            this.playerDelta.y = (this.playerDelta.y - GRAVITY * dt);
        }

        playerSprite.translate(this.playerDelta.x, this.playerDelta.y);

        if (playerSprite.getY() < 61) {
            playerSprite.setPosition(playerSprite.getX(), 61);
            //TODO Fix issue where player character would fall into ground after jump. Now has hard coded possition (61)
        }
    }

    private boolean isCellBlocked(float x, float y, TiledMapTileLayer collisionLayer) {
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
        return cell != null && cell.getTile() != null;
    }

    public boolean collidesBottom(TiledMapTileLayer collisionLayer) {
        for(float step = 0; step < playerSprite.getWidth(); step += collisionLayer.getTileWidth() / 2) {
            if(isCellBlocked(playerSprite.getX() + step, playerSprite.getY(), collisionLayer)) {
                return true;
            }
        }
        return false;
    }

    //Getters and Setters

    public PlayerState getCurrentPlayerState() { return currentPlayerState; }
    public TextureRegion getCurrentFrame() { return currentFrame; }

    public void setCurrentFrame(TextureRegion currentFrame) {
        this.currentFrame = currentFrame;
    }
    public Animation getPlayerWalkingAnimation() {
        return playerWalkingAnimation;
    }

    public void setPlayerWalkingAnimation(Animation playerWalkingAnimation) {
        this.playerWalkingAnimation = playerWalkingAnimation;
    }

    public static float getConstantSpeed() {
        return CONSTANT_SPEED;
    }

    public Vector2 getPlayerDelta() {
        return playerDelta;
    }

    public void setPlayerDeltaX(float playerDeltaX) {
        this.playerDelta.x = playerDeltaX;
    }

    public void setPlayerDeltaY(float playerDeltaY) {
        this.playerDelta.y = playerDeltaY;
    }

    public static float getGRAVITY() {
        return GRAVITY;
    }

}
