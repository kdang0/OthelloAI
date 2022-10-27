# OthelloAI

This school project consists of developing and implementing a computer program that plays Othello. The project exemplifies the minimax algorithm and alpha-beta pruning.

# What is Othello?

Othello is a two player game (one of them being your computer program). The two players take turns putting "discs" (or pieces, stones or marks) on a board. The classical game board is an 8 by 8 grid. At the start of the game there are four discs in the center of the grid. Blue in the top-right and bottom-left. Orange in the top-left and bottom-right. (See Figure 1 for initial configuration.) Players take turns placing discs on the board until the board is full or no legal moves remain. The object is to catch opponent's discs between two of your discs to flip them to your color. At the end of the game, whoever has the most discs on the board wins. 

Please look for information online about Othello and its rules. A good website to play the game and learn about the Othello rules is:  https://www.eothello.com/Links to an external site.. See 'How to play Othello" on that page. Their website states: "We recommend Brian Rose's book "Othello: a minute to learn... a lifetime to master"Links to an external site.. Brian is the 2001 World Othello Champion and his is the most comprehensive book on Othello strategy ever published in English. "

# Live Demo

Our Othello program playing against another team's Othello program

https://user-images.githubusercontent.com/73298064/198175083-d9bdd959-652b-4f6f-baba-0d4cd0f428fd.mp4

# Utility Function
The utility function chosen was either maximizing the player agent’s tile worth or mobility and it was done so by taking the max value from 1. the summation of the player agent’s number of stable discs, tile worth, and number of remaining moves and 2. taking the max value from agent player moves and remaining tile pieces. This causes the agent to be greedy in the sense that it will try to capitalize on the number of stable discs, for which the value of these stable discs are significant. The exact function is shown below:

> final float h1 = 2.0f * (PlayerStable[4] + (56 - OpponentStable[4])) + (PlayerTileWorth - OpponentTileWorth) + (PlayerMoves - OpponentMoves);
> 
> final float h2 = Math.max(2.5f*(PlayerMoves - OpponentMoves), 2.5f*(PlayerTiles - OpponentTiles));
> 
> return Math.max(h1,  h2);

