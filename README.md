# Android-Games-Development-Assignment-1
## Main Features
- **Main Menu:** when the game is launched a menu will appear with two buttons - one marked "PLAY" to play the game, another marked "EXIT" to return to the OS. The main menu’s background can be coloured in or left black. This screen may have a name for the game displayed as well.
- **Game Screen:** For the purposes of this assignment a game screen of 800 by 480 pixels is assumed. You will be given a series of spritesheets for the protagonist and spritesheets for the enemies, with some bonus explosions, projectiles, bombs and backgrounds. 
- **Player Death:** If the player hits an enemy or bomb the player will die using a provided animation and the player will stop. A button should appear labelled "RESTART” which will reset the game again from the start with the player at the far left side of the map.

## Additional Features
- **Pause Feature**
- **Sounds**
- **Looping background music**
- **More Levels**
- **Jump Action and associated threats** (Holes, rocks, etc)
- **Enemy Variations** (Movements and types)

## Specs
- Character Always Moves Right
- Fixed Camera
- The player can move a short distance forward (to the middle of the screen) and a short distance back (to the left edge of the screen)
- Player can shoot short range shots (1/3 of screen width) forward
- Enemies come from front or above player
	- **Flying:** Spawn from left or right. Move across top of screen until they move off screen. Drop bombs
	- **Ground:** Spawn from right of screen. Move towards player
- Enemy hits player, player loses a life
- Player can only shoot in forwards. Can only hit ground based enemies
