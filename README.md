Original Project Proposal:
    The name of the project will be “Kitten vs Aliens”. The application will be a game that centers around using the microphone to control the player. The main idea is that the volume in which the microphone will pick up decides the horizontal position of the “Kitten” that the player controls. The game will be a parody of the Alien Invader game, where the kitten will shoot at incoming “Aliens” sprites that are coming down the screen. The score increases with each defeat of the alien. The game ends when an alien arrives at the bottom of the screen. The speed and generation of the aliens will speed up as the game progresses.
	The game will maintain a leaderboard, which will be utilizing SharedPreferences to keep data persistent over the runs of the application and use a RecyclerView with custom adaptor to hold the leaderboard entries. The game will contain at least 3 activities, the main screen, the leaderboard, and game activity. There will be a pregame screen for the player to enter their name and choose the difficulty, which will be from a spinner implementation. I will be using the RoboHash API to generate random alien and kitten sprites. I will using the microphone as the player input. The sprites will be moved with animation and a custom view, using a timer.

1pts: 
Use of SharedPreferences
Use of ListView/RecyclerView with custom adapter
Use of three or more Activities
Use of a spinner

2pt:
Use of an HTTP API. Use of the class server fulfills this requirement, but here's a list that might give you some ideas: https://github.com/toddmotto/public-apis.
Use of a hardware feature: camera, GPS, microphone, orientation, etc.
Using animation and a custom View.
Using a Timer or other Async task

12 Points Total

Modified Proposal: 12/6/18
Removed spinner implementation, different difficulties are hard to balance with multiple variables contribution to play session.
Used Picasso to load images fron RoboHash

11 Points Total

