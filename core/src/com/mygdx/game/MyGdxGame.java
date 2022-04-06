package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

public class MyGdxGame extends ApplicationAdapter {

	public enum GameState { MAIN_MENU, PLAYING, PAUSED, COMPLETE }

	GameState gameState;

	TiledGameMap gameMap;

	//Map and Rendering
	SpriteBatch batch;
	SpriteBatch uiBatch;
	OrthographicCamera camera;

	//Game world Objects
	MapLayer objectLayer;
	Vector2 endMapLocation;

	//Player
	Player player;

	//Bullet
	ArrayList<PlayerProjectile> bullets;

	//Background Textures
	Texture backgroundGround;

	//Storage class for collision
	Rectangle tileRectangle;

	//UI textures
	Texture buttonSquareTexture;
	Texture buttonSquareDownTexture;
	Texture buttonLongTexture;
	Texture buttonLongDownTexture;

	//UI Buttons
	Button moveLeftButton;
	Button moveRightButton;
	Button moveUpButton;
	Button shootButton;
	Button restartButton;

	//Just use this to only restart when the restart button is released instead of immediately as it's pressed
	boolean restartActive;

	@Override
	public void create () {

		//Camera
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w * 1.75f, h * 1.75f);
		camera.update();

		//Rendering
		bullets = new ArrayList<PlayerProjectile>();
		batch = new SpriteBatch();
		uiBatch = new SpriteBatch();
		player = new Player(bullets);
		gameMap = new TiledGameMap();

		//Object Layer
		objectLayer = gameMap.tiledMap.getLayers().get("Objects");

		//UI Textures
		buttonSquareTexture = new Texture("GUI/buttonSquare_blue.png");
		buttonSquareDownTexture = new Texture("GUI/buttonSquare_beige_pressed.png");
		buttonLongTexture = new Texture("GUI/buttonLong_blue.png");
		buttonLongDownTexture = new Texture("GUI/buttonLong_beige_pressed.png");

		//Buttons
		float buttonSize = h * 0.5f;
		moveLeftButton = new Button(0.0f, 0.0f, w * 0.2f, h * 0.5f, buttonSquareTexture, buttonSquareDownTexture);
		moveRightButton = new Button(w * 0.2f , 0.0f, w * 0.2f, h * 0.5f, buttonSquareTexture, buttonSquareDownTexture);
		moveUpButton = new Button(0.0f, h * 0.5f, w * 0.4f, h * 0.5f, buttonSquareTexture, buttonSquareDownTexture);
		shootButton = new Button(w * 0.6f, 0.0f, w * 0.4f, h * 1, buttonSquareTexture, buttonSquareDownTexture);
		restartButton = new Button(w/2 - buttonSize*2, h * 0.2f, buttonSize * 4, buttonSize, buttonLongTexture, buttonLongDownTexture);

		//Collision
		tileRectangle = new Rectangle();

		//Map Textures
		backgroundGround = new Texture("background/background_00.png");

		newGame();
	}

	@Override
	public void render () {

		player.dt = Gdx.graphics.getDeltaTime();

		//Game World
		update();

		//Clear the screen every frame before drawing.
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gameMap.render(camera);

		//Player
		player.updateCurrentPlayerState();

		//Apply camera and draw player
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		player.playerSprite.draw(batch);
		batch.end();

		//Render Bullets
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).draw(batch);
		}

		//Draw UI
		uiBatch.begin();
		switch(gameState) {
			//if gameState is Running: Draw Controls
			case PLAYING:
				moveLeftButton.draw(uiBatch);
				moveRightButton.draw(uiBatch);
				moveUpButton.draw(uiBatch);
				shootButton.draw(uiBatch);
				uiBatch.setColor(1, 1, 1, 0.3f);
				break;
			case COMPLETE:
				restartButton.draw(uiBatch);
				break;
		}
		uiBatch.end();
	}


	public void update() {

		//Touch Input Info
		boolean checkTouch = Gdx.input.isTouched();
		int touchX = Gdx.input.getX();
		int touchY = Gdx.input.getY();

		switch(gameState) {

			case PLAYING:

				//Collision layer build
				MapLayer collisionLayer = gameMap.tiledMap.getLayers().get("collision");
				TiledMapTileLayer tileLayer = (TiledMapTileLayer) collisionLayer;

				//Update Player Bullets
				for (int i = 0; i < bullets.size(); i++) {
					bullets.get(i).projectileMovement(player.dt);
					if(bullets.get(i).shouldRemove()) {
						bullets.remove(i);
						i--;
					}
				}

				//Poll user for input
				moveLeftButton.update(checkTouch, touchX, touchY);
				moveRightButton.update(checkTouch, touchX, touchY);
				moveUpButton.update(checkTouch, touchX, touchY);
				shootButton.update(checkTouch, touchX, touchY);

				int moveX = 0;
				int moveY = 0;
				boolean isShoot = false;
				if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || moveLeftButton.isDown) {
					moveLeftButton.isDown = true;
					moveX -= 1;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || moveRightButton.isDown) {
					moveRightButton.isDown = true;
					moveX += 1;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.UP) || moveUpButton.isDown) {
					moveUpButton.isDown = true;
					moveY += 1;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || shootButton.isDown) {
					player.shoot();
					shootButton.isDown = true;
					player.shoot();
				}

				//Character and Camera Movement
				player.movePlayer(moveX, moveY, tileLayer, camera);
				camera.update();

		}

	}

	public void newGame() {
		gameState = GameState.PLAYING;

		player.dt = 0.0f;

		//Set player and camera starting location
		RectangleMapObject playerObject = (RectangleMapObject) objectLayer.getObjects().get("Player");
		player.playerSprite.setCenter(playerObject.getRectangle().x, (playerObject.getRectangle().y + (playerObject.getRectangle().getHeight() * 1.12f)));
		camera.position.x = player.playerSprite.getX() + player.playerSprite.getWidth()/2;
		camera.position.y = player.playerSprite.getY() * 7.92f; //TODO optimize camera height when setting up world
		camera.update();

		restartActive = false;
	}

	@Override
	public void dispose () {
		batch.dispose();
		player.playerTexture.dispose();
		buttonSquareTexture.dispose();
		buttonSquareDownTexture.dispose();
		buttonLongTexture.dispose();
		buttonLongDownTexture.dispose();
	}
}
