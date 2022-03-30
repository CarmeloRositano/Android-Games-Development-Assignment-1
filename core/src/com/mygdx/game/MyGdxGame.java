package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import org.w3c.dom.Text;


public class MyGdxGame extends ApplicationAdapter {

	public enum GameState { PLAYING, COMPLETE, PAUSED }
	public enum PlayerState {IDLE, MOVING, DYING, SHOOTING }

	public static final float MOVEMENT_SPEED = 200.0f;

	GameState gameState = GameState.PLAYING;

	//Map and rendering
	SpriteBatch WalkingBatch;
	SpriteBatch DryingBatch;
	SpriteBatch ShootingBatch;
	SpriteBatch uiBatch;
	OrthographicCamera camera;

	//Game Clock
	float dt;

	//Player Character
	boolean isAlive;
	boolean isShooting;
	boolean isMoving;

	//Texture
	Texture playerTexture;

	Texture playerWalkingTexture;
	private TextureRegion[] playerWalkingFrames;
	private Animation playerWalkingAnimation;

	Texture playerDyingTexture;
	private TextureRegion[] playerDyingFrames;
	private Animation playerDyingAnimation;

	Texture playerShootingTexture;
	private TextureRegion[] playerShootingFrames;
	private Animation playerShootingAnimation;

	private TextureRegion currentFrame;
	private TextureRegion currentFrame2;
	private float stateTime;

	//UI Buttons
	Button moveLeftButton;
	Button moveRightButton;
	Button jumpButton;

	Boolean restartActive;

	@Override
	public void create () {

		isAlive = true;
		isShooting = false;
		isMoving = false;

		int index;

		//Player Walking Texture Build
		int walkingFrameCol = 3;
		int WalkingFrameRow = 6;
		playerWalkingTexture = new Texture(Gdx.files.internal("player/moving.png"));
		WalkingBatch = new SpriteBatch();
		TextureRegion[][] walkTemp = TextureRegion.split(playerWalkingTexture, playerWalkingTexture.getWidth() / walkingFrameCol, playerWalkingTexture.getHeight() / WalkingFrameRow);
		playerWalkingFrames = new TextureRegion[walkingFrameCol * WalkingFrameRow];
		index = 0;
		for (int i = 0; i < WalkingFrameRow; i++) {
			for (int j = 0; j < walkingFrameCol; j++) {
				playerWalkingFrames[index++] = walkTemp[i][j];
			}
		}
		playerWalkingAnimation = new Animation(0.033f, playerWalkingFrames);

		//Player Shooting Texture Build
		int DyingFrameCol = 3;
		int DyingFrameRow = 4;
		playerDyingTexture = new Texture(Gdx.files.internal("player/shooting.png"));
		DryingBatch = new SpriteBatch();
		TextureRegion[][] dieTemp = TextureRegion.split(playerDyingTexture, playerDyingTexture.getWidth() / DyingFrameCol, playerDyingTexture.getHeight() / DyingFrameRow);
		playerDyingFrames = new TextureRegion[DyingFrameCol * DyingFrameRow];
		index = 0;
		for (int i = 0; i < DyingFrameRow; i++) {
			for (int j = 0; j < DyingFrameCol; j++) {
				playerDyingFrames[index++] = dieTemp[i][j];
			}
		}
		playerDyingAnimation = new Animation(0.033f, playerDyingFrames);

		//Player Dying Texture Build
		int ShootingFrameCol = 5;
		int ShootingFrameRow = 4;
		playerShootingTexture = new Texture(Gdx.files.internal("player/dying.png"));
		ShootingBatch = new SpriteBatch();
		TextureRegion[][] shootTemp = TextureRegion.split(playerShootingTexture, playerShootingTexture.getWidth() / ShootingFrameCol, playerShootingTexture.getHeight() / ShootingFrameRow);
		playerShootingFrames = new TextureRegion[ShootingFrameCol * ShootingFrameRow];
		index = 0;
		for (int i = 0; i < ShootingFrameRow; i++) {
			for (int j = 0; j < ShootingFrameCol; j++) {
				playerShootingFrames[index++] = shootTemp[i][j];
			}
		}
		playerShootingAnimation = new Animation(0.033f, playerShootingFrames);

		stateTime = 0.0f;
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		stateTime += Gdx.graphics.getDeltaTime();

		currentFrame = (TextureRegion) playerWalkingAnimation.getKeyFrame(stateTime, true);

		WalkingBatch.begin();
		WalkingBatch.draw(currentFrame,1,1);
		WalkingBatch.end();
	}
	
	@Override
	public void dispose () {

	}
}
