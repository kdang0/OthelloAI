package com.ahfriedman.othelloai.models;

import java.util.List;

/**
 * This class represents the Game class from the book. 
 */
public class Game {

	
	/**
	 * Given a state, this function returns the player that moves next. 
	 * This function is equivalent to the
	 * <code>
	 * P TO_MOVE(S s)
	 * </code>
	 * function in the book. 
	 * 
	 * @param s A state to get the next player to move
	 * @return The byte representation of the next player to move
	 */
	public static byte toMove(State s) { return s.getPlayer(); }
	
	/**
	 * Given a state, this function lists all possible actions that the current player can take, 
	 * or an empty list if either the game is over or the current player must pass. 
	 * <br />
	 * This function is equivalent to the
	 * <code>
	 * ACTIONS(S s)
	 * </code>
	 * function in the book. 
	 * @param s The state to get the available actions for. 
	 * @return A list of bytes that represent the possible actions. 
	 */
	public static List<byte[]> actions(State s)
	{
		return s.getMoves(toMove(s));
	}
	
	/**
	 * Given a state and an action, this function returns the resulting state. 
	 * <br />
	 * This function is equivalent to the 
	 * <code>
	 * S RESULT(S s, A a)
	 * </code>
	 * function in the book. 
	 * @param s The state to get the result of the action for. 
	 * @param action The action performed. 
	 * @return The resulting state. 
	 */
	public static State result(State s, byte[] action)
	{
		return s.clone().move(toMove(s), action);
	}
	
	/**
	 * Causes a player to pass. 
	 * @param s The current state
	 * @return The state resulting from the pass. 
	 */
	public static State pass(State s)
	{
		return s.clone().pass(); 
	}
	
	/**
	 * This function is used to determine if the given state is terminal or not.
	 * <br />
	 * This function is equivalent to the 
	 * <code>
	 * IS TERMINAL(S s)
	 * </code>
	 * function in the book.
	 * @param s The state
	 * @return true if the state is terminal; false otherwise. 
	 */
	public static boolean isTerminal(State s)
	{
		//First check the number of tiles as that is faster than checking the number of moves
		return (s.getPlayerTiles() + s.getOpponentTiles() == 64) ||
				(s.getPlayerTiles() == 0) ||
				(s.getOpponentTiles() == 0) ||
				s.getMoves(State.AG1).isEmpty() && s.getMoves(State.AG2).isEmpty(); //TODO: Try to find a more efficient terminal check
	}
	
	/**
	 * Given a state, this function will return the utility of the state for our player. 
	 * <br />
	 * This function is equivalent to the
	 * <code>
	 * UTILITY(S s, P p)
	 * </code>
	 * function in the book, although we omit player as it is not needed. 
	 * 
	 * @param s The current state
	 * @return The evaluation/utility of the state depending on the condition of the state
	 */
	public static float utility(State s)
	{
		/*
		 * We used to use a different result for terminal states vs intermediate states. 
		 * Currently, our evaluation function is able to work for both terminal and intermediate states,
		 * so we just use the one. The code does allow for us to use both though. 
		 */
		//	if(isTerminal(s))
		//		return s.getUtility();
		return s.getEvaluation();
	}
}
