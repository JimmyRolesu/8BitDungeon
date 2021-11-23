Welcome to my Android Project!

In this folder you will find an: APK of the final app; A PDF Report detailing the project; a video demonstration; and the project source folder.

The main code is located in: \AndroidCW\app\src\main\java

Map Files are located within \AndroidCW\app\src\main\assets and other resources are located in the \AndroidCW\app\src\res folder

Java Classes:

Main_Menu -> Main Menu screen (layout in XML)
Info -> How-to-Play screen (layout in XML)
MainActivity -> Gameplay Activity, deals with accelerometer and sounds
GameView -> A surface view that displays all visuals to user. Also deals with game logic
Block -> A block object class
Link -> Makes a player object that can be controlled
Enemy -> An abstract class for making moving AI enemies
Keese -> Bat, inherits from Enemy
Stalfos -> Skeleton, inherits from Enemy
Collectible -> Abstract class for making collectible objects
Rupee -> Score counter, inherits from collectible
Triforce -> Game ender, inherits from collectible
Sprite -> Updates player sprite sheet
CollectibleSprite -> Updates object sprites
EnemySprite -> Updates enemy sprites

HOW TO PLAY:

-Click START button to enter game
-Move player by tilting phone
-Avoid enemies
-Collect rupees for score
-Reach staircase to go to next level
-Touch Triforce to end game.
