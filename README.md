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

# Evaluation Function
The evaluation function is the same as the utility function shown above

# Heuristics & Strategies
The heuristics we considered when implementing our agent are as followed:
- Mobility:  The number of legal moves the agent can make against its opponent throughout the course of the game. Mobility is essential as it will allow the agent to maximize its chances of obtaining discs of higher worth and mitigating the number of moves the opponent can make. 
- Stability: The number of stable discs the agent has to capitalize on the number of discs on the board. Stable discs we considered are corners and any neighboring discs of the same color along the edge. Stability is essential as it ensures the disc will not be taken from the opponent.
- Tile-Worth/Tile-Weight: The value when placing a disc on a specific coordinate of the board. For instance, if the agent was to place a disc on a C-Square, adjacent edge squares, or X-square, from the corners diagonally adjacent, it allows the opponent to take advantage as they will have a higher chance of capitalizing the corner. Having a disc on a corner adds more stability for the player in terms of the number of discs they will have on the board. To prevent the agent from making a move like this, we made it so that these coordinates on the board are of negative value. 

In addition to these heuristics, we use several strategies to improve the efficacy and efficiency of the program. Some of our strategies include:
- Depth Iterative Search based on Time: instead of using a depth limit, our iterative alpha-beta pruning search will repeatedly search with a larger depth limit. When we are close to running out of time, the search will abort and the results of the previous search (which we were able to complete) will be used. 
- Minimal Memory Use: to try to make our program run as fast as possible, we try to limit the amount of memory used—especially in our searches. For example, we use a byte array to represent our board (instead of something like a string array), and then convert between our byte representation and the referee’s string representation during communications. 
- Code Efficiency: to try to make our code run faster, we try to limit the amount of work needed for any task. To accomplish this, we use a variety of strategies (such as short circuiting booleans), processing the number of tiles owned by each player during player moves, using queues for fast enqueue and dequeue operations during search, and more. 
- Search Tree Ordering: when searching for the best possible move, we store the action taken and the heuristic of the resulting state in priority queues. This allows us to process the best/worst possible actions (depending on if the node is a max node or a min node)  in order with little additional work. 

# Results
- The agent program was tested against past versions of itself. The team made various versions of the agent as we changed the heuristics and weight for the agent.
  - Each new evaluation or heuristic was typically tested against multiple previous iterations, in case we accidentally created a heuristic that could beat our previous one, but led to the AI still picking worse moves overall as it will not consistently  make an optimal move rather, it will make moves that will be good enough to counter the previous iteration of the AI; pertains to a rock-paper-scissors scenario.
  - To evaluate the effectiveness of the AI, some of the test games were analyzed using WZebra, a software for Othello, which can analyze a board state far in advance to calculate which is the best move available.
# Strengths/Weaknesses: 
- Strengths
  - The AI overall plays well past the first four to five moves, prioritizing on leaving the opponent with few options and focusing on acquiring stable discs, especially corners.
  - The AI can prioritize different things at different times, so at the start of the game it does not see the first moves available to it as all being equal because none of them allow it to obtain stable discs in the near future.
- Weaknesses
  - The AI does not always play the first four to five moves optimally. This is in part because we struggled to create a heuristic that encouraged the AI to hold on to the center spaces early on without getting greedy and grabbing lots of tiles at the same time.
  - Late game, the values of corners and the X-squares adjacent to them are different from what they were early on or in the mid-game. Our AI does not account for this and does not always make the optimal move regarding these spaces late game. It strongly prefers to avoid X-squares, even when taking an X-square would leave the opponent with a single bad move they had no option but to take.

## Discussion of Heuristics
The evaluation function looks at the game in a couple different ways to determine the best course of action. If one of the pieces of the evaluation function does not clearly indicate a best move, then another part of the function usually will. This is extremely useful, as some measures of how well the AI is doing do not apply at all points in the game. For the terminal state, we want to have as many tiles as possible, but for the early game it is typically better to minimize the number of tiles we have to reduce the moves available to the opponent. Leading up to the end of the game, it’s a good idea to begin gathering stable discs. This is also a poor heuristic early on though, as no stable discs can be obtained by either player until a corner square has been filled. By having an evaluation function that considers multiple ways to evaluate the board state, we can give the AI different behavior for different situations. This allows the AI to prioritize getting stable discs late game and grabbing as many tiles as it can, while also letting the AI make good moves in the start of the game to reduce the options available to the opponent.

## Contributors

- Alex Friedman
- Daniel Stusalitus
- Kevin Dang




