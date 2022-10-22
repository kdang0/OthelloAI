package com.ahfriedman.othelloai.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.ahfriedman.othelloai.OthelloAI;
import com.ahfriedman.othelloai.searching.IterativeSearch.EvaluatedAction;
import com.ahfriedman.othelloai.utils.APQ;

/**
 * Used to manage the state related processes
 */
public class State {

	/**
	 * Used to represent a space is clear
	 */
	public static final byte CLR = 0;

	/**
	 * Used to represent a space is owned by agent 1 (blue)
	 */
	public static final byte AG1 = 1;

	/**
	 * Used to represent a space is owned by agent 2 (orange) 
	 */
	public static final byte AG2 = 2; 

	/**
	 * Initial state of a board
	 */
	public static final byte[][] INITIAL_BOARD  = new byte[][] {
		// A | B | C | D | E | F | G | H
		{CLR,CLR,CLR,CLR,CLR,CLR,CLR,CLR}, // 8
		{CLR,CLR,CLR,CLR,CLR,CLR,CLR,CLR}, // 7
		{CLR,CLR,CLR,CLR,CLR,CLR,CLR,CLR}, // 6
		{CLR,CLR,CLR,AG2,AG1,CLR,CLR,CLR}, // 5
		{CLR,CLR,CLR,AG1,AG2,CLR,CLR,CLR}, // 4
		{CLR,CLR,CLR,CLR,CLR,CLR,CLR,CLR}, // 3
		{CLR,CLR,CLR,CLR,CLR,CLR,CLR,CLR}, // 2
		{CLR,CLR,CLR,CLR,CLR,CLR,CLR,CLR}, // 1
	};


	/**
	 * Used to store weights for the preference of different tiles 
	 */
	private static final float B1 = 50.0f;
	private static final float D1 = 0.5f;
	private static final float C1 = -2.0f;
	private static final float X1 = -10.0f;

	public static final float[][] STATE_WEIGHTS  = new float[][] {
	  // A | B | C | D | E | F | G | H
		{B1,C1,D1,D1,D1,D1,C1,B1}, // 8
		{C1,X1,D1,D1,D1,D1,X1,C1}, // 7
		{D1,D1,D1,D1,D1,D1,D1,D1}, // 6
		{D1,D1,D1,D1,D1,D1,D1,D1}, // 5
		{D1,D1,D1,D1,D1,D1,D1,D1}, // 4
		{D1,D1,D1,D1,D1,D1,D1,D1}, // 3
		{C1,X1,D1,D1,D1,D1,X1,C1}, // 2
		{B1,C1,D1,D1,D1,D1,C1,B1}, // 1
	};

	/**
	 * Stores the representation of the board
	 */
	private byte[][] board;

	/**
	 * The current player
	 */
	private byte player; 

	/**
	 * In the initial state, both players have 2 tiles, and hence a value of 2*d1
	 */
	private byte PlayerTiles = 2; 
	private byte OpponentTiles = 2; 

	private float PlayerTileWorth = 2 * D1;
	private float OpponentTileWorth = 2 * D1;

	/**
	 * Creates a new state based on the initial board, and player 1 as the first player 
	 */
	public State() {
		this.board = clone(INITIAL_BOARD);
		this.player = AG1; 
	}

	/**
	 * Constructs a new state given a board and a player. 
	 * This is private so that way it cant get messed up by a user
	 * @param player The player
	 * @param board The board 
	 */
	private State(byte player, byte[][] board)
	{
		this.board = board; 
		this.player = player; 
	}

	/**
	 * Clones the current state, and returns it. 
	 */
	public State clone() {
		final State ans = new State(this.player, clone(this.board));
		ans.PlayerTiles = this.PlayerTiles;
		ans.OpponentTiles = this.OpponentTiles; 

		ans.PlayerTileWorth = this.PlayerTileWorth;
		ans.OpponentTileWorth = this.OpponentTileWorth; 

		return ans; 
	}

	/**
	 * Helper function used to move the player 
	 * @param player The player 
	 * @param action The location to move
	 * @return The state after the player takes the move
	 */
	public State move(byte player, byte[] action)
	{
		final byte r = action[0]; 
		final byte c = action[1]; 
		
		if(board[r][c] != CLR)
		{
			boolean wasOpponent = player == OthelloAI.OPPONENT;
			
			System.out.println((wasOpponent ? "Opponent" : "We") + " tried to move to an already occupied space: " + byteCoordinatesToMoveString(action));
			System.out.println((wasOpponent ? "We win." : "Opponent wins."));
			return null; //System.exit(0);
		}

		//We need to assign boolean values first otherwise short circuiting could end moves early
		boolean m1 = moveHelper(player, r,  c, (byte) -1, (byte) -1);
		boolean m2 = moveHelper(player, r,  c, (byte) -1, (byte)  0);
		boolean m3 = moveHelper(player, r,  c, (byte) -1, (byte)  1);
		boolean m4 = moveHelper(player, r,  c, (byte)  0, (byte) -1);
		boolean m5 = moveHelper(player, r,  c, (byte)  0, (byte)  1);
		boolean m6 = moveHelper(player, r,  c, (byte)  1, (byte) -1);
		boolean m7 = moveHelper(player, r,  c, (byte)  1, (byte)  0);
		boolean m8 = moveHelper(player, r,  c, (byte)  1, (byte)  1);
		
		if(!m1 && !m2 && !m3 && !m4 && !m5 && !m6 && !m7 && !m8)
		{
			boolean wasOpponent = player == OthelloAI.OPPONENT;
			System.out.println((wasOpponent ? "Opponent" : "We") + " tried to a move to a loction where no pieces would be trapped.");
			System.out.println((wasOpponent ? "We win." : "Opponent wins."));
			return null;// System.exit(0);
		}

		//Swap the player. Should have same effect as returning pass
		this.player = (this.player == AG1) ? AG2 : AG1;

		return this; 
	}

	/**
	 * Gets list of the possible moves a given player can make
	 * @param player The player to check for possible moves
	 * @return A list of moves the player can make 
	 */
	public List<byte[]> getMoves(byte player) {
		final List<byte[]> moves = new ArrayList<byte[]>();//Arraylists and LinkedLists have about the same performance here

		for(byte r = 0; r < board.length; r++)
		{
			for(byte c = 0; c < board.length; c++)
			{
				if(canMove(player, r, c))
					moves.add(new byte[] {r, c});
			}
		}
		return moves; 
	}
	
	public void getMoves(APQ<EvaluatedAction> apq, byte player) {
		for(byte r = 0; r < board.length; r++)
		{
			for(byte c = 0; c < board.length; c++)
			{
				if(canMove(player, r, c))
				{
					final byte[] action = new byte[] {r, c};
					apq.insert(new EvaluatedAction(action, Game.result(this, action)));
				}
			}
		}
	}

	/**
	 * Used to check if a player can move at a given location
	 * @param player The player
	 * @param r The r coordinate
	 * @param c The c coordinate
	 * @return true if the player can move at r,c; false otherwise. 
	 */
	public boolean canMove(byte player, byte r, byte c)
	{
		if(board[r][c] != CLR)
			return false; 

		//Short circuiting ensures that we only process these until we get to the first one that is true
		//Order based on cache
		return 
				canMoveHelper(player, r,  c, (byte)  0, (byte) -1) ||
				canMoveHelper(player, r,  c, (byte)  0, (byte)  1) ||
				canMoveHelper(player, r,  c, (byte) -1, (byte) -1) || 
				canMoveHelper(player, r,  c, (byte) -1, (byte)  0) ||
				canMoveHelper(player, r,  c, (byte) -1, (byte)  1) ||
				canMoveHelper(player, r,  c, (byte)  1, (byte) -1) ||
				canMoveHelper(player, r,  c, (byte)  1, (byte)  0) ||
				canMoveHelper(player, r,  c, (byte)  1, (byte)  1);

	}

	/**
	 * Helper function to determine if a player can move from a tile in a specific direction
	 * @param player The given player
	 * @param x The x coordinate of the tile the player would like to move
	 * @param y The y coordinate of the tile the player would like to move
	 * @param vr The x component of velocity (direction) the player would like to move
	 * @param vc The y component of velocity (direction) the player would like to move
	 * @return True if the given player can move at x, y; false otherwise. 
	 */
	public boolean canMoveHelper(byte player, byte x, byte y, byte vr, byte vc)
	{
		//If our velocity is 0, return false to prevent an infinite loop. 
		if(vr == 0 && vc == 0)
			return false; 

		//Determine the first adjacent tile to check. 
		byte r = (byte) (x + vr); 
		byte c = (byte) (y + vc); 

		//Make sure the tile is in bounds. 
		boolean inBounds = (r >= 0 && c >= 0 && r < board.length && c < board.length); 

		//if the tile is not, return false. 
		if(!inBounds)
			return false; 

		//Ensure the adjacent tile is the other player. 
		if(board[r][c] == player || board[r][c] == CLR) 
			return false; 

		//Increment r and c by the velocity again 
		r += vr; 
		c += vc; 

		//While r and c are still in bounds, 
		while (r >= 0 && c >= 0 &&  r < board.length && c < board.length)
		{
			//If we find our player again, return true as we can move. 
			if(board[r][c] == player)
				return true; 
			if(board[r][c] == CLR) //If we find a clear space, return false. 
				return false; 

			r += vr; 
			c += vc; 
		}
		return false; //We ran out of bounds, return false
	}

	/**
	 * Helper function for a player moving in a direction. 
	 * @param player The given player
	 * @param x The x coordinate of the tile the player would like to move
	 * @param y The y coordinate of the tile the player would like to move
	 * @param vr The x component of velocity (direction) the player would like to move
	 * @param vc The y component of velocity (direction) the player would like to move
	 * @return True if the given player can move at x, y; false otherwise. 
	 */
	public boolean moveHelper(byte player, byte x, byte y, byte vr, byte vc)
	{
		//If our velocity is 0, return false to prevent an infinite loop. 
		if(vr == 0 && vc == 0)
			return false; 

		//Determine the first adjacent tile to check. 
		byte r = (byte) (x + vr); 
		byte c = (byte) (y + vc); 

		//Make sure the tile is in bounds. 
		boolean inBounds = (r >= 0 && c >= 0 && r < board.length && c < board.length); 

		//if the tile is not, return false. 
		if(!inBounds)
			return false; 

		//Ensure the adjacent tile is the other player. 
		if(board[r][c] == player || board[r][c] == CLR) 
			return false; 

		//Increment r and c by the velocity again 
		r += vr; 
		c += vc; 

		//While r and c are still in bounds, 
		while (r >= 0 && c >= 0 &&  r < board.length && c < board.length)
		{
			//If the board at r, c is the player, 
			if(board[r][c] == player)
			{

				//If the board at [x][y] is not the player, then claim x, y as the player's
				if(board[x][y] != player)
				{
					if(player == OthelloAI.AGENT_PLAYER)
					{
						PlayerTiles++;
						PlayerTileWorth += STATE_WEIGHTS[x][y];
					}
					else
					{
						OpponentTiles++; 
						OpponentTileWorth += STATE_WEIGHTS[x][y];
					}
				}

				board[x][y] = player; 

				//Back track r,c until it is the same as x,y
				while(x != r || y != c)
				{ 

					//Keep track of the tiles that belong to each player
					if(board[r][c] != player)
					{
						if(player == OthelloAI.AGENT_PLAYER)
						{
							PlayerTiles++;
							PlayerTileWorth += STATE_WEIGHTS[r][c];

							OpponentTiles--; 
							OpponentTileWorth -= STATE_WEIGHTS[r][c];
						}
						else if(player == OthelloAI.OPPONENT) //Should just be else
						{
							PlayerTiles--;
							PlayerTileWorth -= STATE_WEIGHTS[r][c];

							OpponentTiles++; 
							OpponentTileWorth += STATE_WEIGHTS[r][c];
						}
						board[r][c] = player;
					}

					r -= vr;
					c -= vc; 

				}

				return true; 
			}

			if(board[r][c] == CLR)
				return false; 

			r += vr; 
			c += vc; 
		}
		return false; 
	}


	/**
	 * Converts a byte[] representation of an action to the string representation used by the referee. 
	 * @param b The byte[] representation of the action. 
	 * @return The string representation of the action. 
	 */
	public static final String byteCoordinatesToMoveString(byte[] b)
	{
		return ((char) (65 + b[1])) + " " + (8 - b[0]);
	}

	/**
	 * Given a string representation of an action, this function will convert it to the byte[]
	 * definition used by the program 
	 * @param letter The string A-H that represents the action
	 * @param number The string 1-8 that represents the action
	 * @return The byte[] representation of the action used by the program
	 */
	public static final byte[] StringToByteCoordinates(String letter, String number)
	{
		final byte n = (byte) (8 - Byte.parseByte(number));
		if(n < 0 || n >= 8)
			return null;

		if(letter.length() != 1)
			return null; 

		final byte l = (byte) ((byte) letter.charAt(0) - 65); 

		if(l < 0 || l > 7)
			return null;

		return new byte[] {n, l}; 
	}

	/**
	 * Prints the avaliable moves for a given player. 
	 * @param player The player to print avaliable moves for 
	 */
	public void displayMoves(byte player)
	{

		for(byte r = 0; r < board.length; r++)
		{
			for(byte c = 0; c < board.length; c++)
			{
				if(c == 0)
					System.out.print((8 - r) + "|");
				if(canMove(player, r, c))
					System.out.print("X");
				else
					System.out.print(board[r][c]);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void display()
	{

		for(byte r = 0; r < board.length; r++)
		{
			for(byte c = 0; c < board.length; c++)
			{
//				if(c == 0)
//					System.out.print((8 - r) + "|");
//				else
					System.out.print(board[r][c]);
			}
			System.out.println();
		}
		System.out.println();
	}


	//TODO: DO BETTER!, STORE THIS AND UTILITY SOMEHWERE?; Try using byte to conserve memory
	/**
	 * Calculates the utility/evaluation function. Currently, we use the same function
	 * for utility and evaluation as they can both be used on any state. 
	 * @return The evaluation of the current state
	 */
	public float getEvaluation()
	{
		final int PlayerMoves = getMoves(OthelloAI.AGENT_PLAYER).size();
		final int OpponentMoves = getMoves(OthelloAI.OPPONENT).size();

		final byte[] PlayerStable = getStableDiscCount(OthelloAI.AGENT_PLAYER);
		final byte[] OpponentStable = getStableDiscCount(OthelloAI.OPPONENT);

//		final float progress = (PlayerTiles + OpponentTiles) / 64.0f; 
		
		//Number of pieces involved in move 
		//Keep list of open spaces to not brute force moves
		//TODO: Try to consider Semistable discs?

		final float h1 = 2.0f * (PlayerStable[4] + (56 - OpponentStable[4])) + (PlayerTileWorth - OpponentTileWorth) + (PlayerMoves - OpponentMoves);
		final float h2 = Math.max(2.5f*(PlayerMoves - OpponentMoves), 2.5f*(PlayerTiles - OpponentTiles));
		return Math.max(h1,  h2);
	}

	/**
	 * This is used to pass the turn from one player to the other
	 * @return returns the current state so that way it can be chained. 
	 */
	public State pass() {
		this.player = (this.player == AG1) ? AG2 : AG1; 
		return this; 
	}

	/**
	 * Currently unused; our evaluation function is able to calculate out the utility for terminal states. 
	 */
//	public float getUtility(byte player)
//	{
//		byte PlayerTiles = 0; 
//		byte OpponentTiles = 0; 
//
//		for(byte r = 0; r < board.length; r++)
//			for(int c = 0; c < board.length; c++)
//				if(board[r][c] == OthelloAI.AGENT_PLAYER)
//					PlayerTiles++; 
//				else if(board[r][c] != CLR)
//					OpponentTiles++;
//		return PlayerTiles - OpponentTiles; 
//	}

	/**
	 * Gets the number of stable discs for a given player. 
	 * @param player The byte representation of the player (AG1 or AG2)
	 * @return The number of stable discs owned by the player
	 */
	public byte[] getStableDiscCount(byte player)
	{
		//Running count 
		byte[] ans = new byte[5]; 

		//Tracks the stable discs
		final boolean[][] stable = new boolean[board.length][board.length];
		//Tracks the visited discs 
		final boolean[][] visited = new boolean[board.length][board.length];

		//Used for the search queue, and adds all the corners to the list 
		final LinkedList<byte[]> queue = new LinkedList<byte[]>(); //ArrayLists and LinkedLists both have approximately the same efficiency here
		queue.add(new byte[] {0, 0});
		queue.add(new byte[] {0, 7});
		queue.add(new byte[] {7, 0});
		queue.add(new byte[] {7, 7});

		//Mark the corners as visited
		visited[0][0] = true;
		visited[0][7] = true; 
		visited[7][0] = true;
		visited[7][7] = true; 

		//While our queue is not empty
		while(!queue.isEmpty())
		{
			//Remove the first element from the queue
			final byte[] coord = queue.removeFirst();

			//Separate out the byte[] into r and c
			final byte r = coord[0];
			final byte c = coord[1]; 

			//Ensure coordinate is in bounds 
			if(r < 0 || c < 0 || r >= board.length || c >= board.length)
				continue; 

			//If the coordinate is not the same type as the player, then continue as we know
			//the current disc cannot be stable for the player
			if(this.board[r][c] != player)
				continue; 

			//Booleans to determine bound checking in each direction. 
			final boolean cellNorth = (c - 1 >= 0);
			final boolean cellSouth = (c + 1 < board.length);

			final boolean cellWest = (r - 1 >= 0);
			final boolean cellEast = (r + 1 < board.length);

			final boolean cellNorthWest = cellNorth && cellWest; 
			final boolean cellSouthEast = cellSouth && cellEast;

			final boolean cellNorthEast = cellNorth && cellEast;
			final boolean cellSouthWest = cellSouth && cellWest; 

			
			int stableDirCount = 0; 
			
			//Checks if the disc is stable vertically
			final boolean stableVertical = (!cellNorth || stable[r][c - 1]) || (!cellSouth || stable[r][c + 1]);

			//If not, we cannot be stable, so continue
			if(stableVertical)
				stableDirCount++;

			//Checks if the disc is stable horizontally
			final boolean stableHorizontal = (!cellWest || stable[r - 1][c]) || (!cellEast || stable[r + 1][c]);

			//If not, we cannot be stable, so continue
			if(stableHorizontal)
				stableDirCount++; 

			//Check if we are stable on the angles
			final boolean stableNWSE = (!cellNorthWest || stable[r - 1][c - 1]) || (!cellSouthEast || stable[r + 1][c + 1]);

			//If not, we cannot be stable, so continue
			if(stableNWSE)
				stableDirCount++; 

			//Checks if we are stable on the other angle
			final boolean stableNESW = (!cellNorthEast || stable[r + 1][c - 1]) || (!cellSouthWest || stable[r - 1][c + 1]);

			//If not, we cannot be stable, so continue
			if(stableNESW)
				stableDirCount++; 

			//Increment our counter, mark stable as true
			//ans++; 
			ans[stableDirCount] = (byte) (ans[stableDirCount] + 1);
			
			if(stableDirCount != 4)
				continue; 
			
			stable[r][c] = true; 

			//Check neighboring cells, and add them to the queue to check.
			//NOTE: We must check cardinal directions first. 
			if(cellNorth && !visited[r][c - 1])
			{
				queue.add(new byte[] {r, (byte) (c - 1)});
				visited[r][c - 1] = true; 
			}

			if(cellSouth && !visited[r][c + 1])
			{
				queue.add(new byte[] {r, (byte) (c + 1)});
				visited[r][c + 1] = true; 
			}

			if(cellWest && !visited[r - 1][c])
			{
				queue.add(new byte[] {(byte) (r - 1), (byte) (c)});
				visited[r - 1][c] = true; 
			}

			if(cellEast && !visited[r + 1][c])
			{
				queue.add(new byte[] {(byte) (r + 1), (byte) (c)});
				visited[r + 1][c] = true; 
			}


			if(cellNorthWest && !visited[r - 1][c - 1])
			{
				queue.add(new byte[] {(byte) (r - 1), (byte) (c - 1)});
				visited[r - 1][c - 1] = true; 
			}

			if(cellNorthEast && !visited[r + 1][c - 1])
			{
				queue.add(new byte[] {(byte) (r + 1), (byte) (c - 1)});
				visited[r + 1][c - 1] = true; 
			}

			if(cellSouthEast && !visited[r + 1][c + 1])
			{
				queue.add(new byte[] {(byte) (r + 1), (byte) (c + 1)});
				visited[r + 1][c + 1] = true; 
			}

			if(cellSouthWest && !visited[r - 1][c + 1])
			{
				queue.add(new byte[] {(byte) (r - 1), (byte) (c + 1)});
				visited[r - 1][c + 1] = true; 
			}

		}

		return ans; 
	}

	/**
	 * Helper function for cloning a byte array. Used to duplicate the board. 
	 * @param b The byte array to clone
	 * @return A byte array that is the same as b, but without parody. 
	 */
	private static final byte[][] clone(byte[][] b)
	{
		final byte[][] ans = new byte[b.length][b[0].length];

		for(int r = 0; r < b.length; r++)
			for(int c = 0; c < b[r].length; c++)
				ans[r][c] = b[r][c];

		return ans; 
	}

	/**
	 * Return the byte representation of the current player
	 * @return Return the byte representation of the current player
	 */
	public byte getPlayer() { return player; }

	/**
	 * Returns the number of tiles that the player has
	 * @return Returns the number of tiles that the player has
	 */
	public byte getPlayerTiles() { return PlayerTiles; }

	/**
	 * Returns the number of tiles that the opponent has
	 * @return Returns the number of tiles that the opponent has
	 */
	public byte getOpponentTiles() { return OpponentTiles; }	
	
	public byte[][] getBoard() {
		return board;
	}
	
	
	
}
