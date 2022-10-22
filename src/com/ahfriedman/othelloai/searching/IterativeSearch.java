package com.ahfriedman.othelloai.searching;

import com.ahfriedman.othelloai.models.Game;
import com.ahfriedman.othelloai.models.State;
import com.ahfriedman.othelloai.utils.AMaxPQ;
import com.ahfriedman.othelloai.utils.AMinPQ;
import com.ahfriedman.othelloai.utils.APQ;
import com.ahfriedman.othelloai.utils.Consts;

/**
 * This class is responsible for our iterative A-B minmax search.
 */
public class IterativeSearch {
	
	/*
	  Here is the pseudocode from our book 
	 
	 function ALPHA-BETA-SEARCH(game, state) returns an action
	 {
	 	player = game.TO-MOVE(state)
	 	value, move = MAX-VALUE(game, state, -inf, + inf)
	 	return move
	 }
	 
	 function MAX-VALUE(game, state, alpha, beta) return a (utility, move) pair
	 {
	 	if game.IS-TERMINAL(state) then return game.UTILITY(state, player), null
	 	v = -inf; 
	 	
	 	for each game A in game.ACTIONS(state) do 
	 	{
	 		v2, A2 = MIN-VALUE(game, game.RESULT(state, A), alpha, beta)
	 		if(v2 > v) then 
	 		{
	 			v, move = v2, A
	 			alpha = Max(alpha, v);
	 		}
	 		if(v >= beta) then 
	 			return v, move
	 	}
	 	return v, move
	 }
	 
	 function MIN-VALUE(game, state, alpha, beta) returns a (utility, move) pair
	 {
	 	if(game.IS-TERMINAL(state))
	 		return game.UTILITY(state, pair), null;
	 	v = -inf; 
	 	
	 	for each a in game.ACTIONS(state) 
	 	{
	 		v2, a2 = MAX-VALUE(game, game.RESULT(state, a), alpha, beta);
	 		
	 		if(v2 < v)
	 		{
	 			v, move = v2, a
	 			beta = MIN(beta, v); 
	 		}
	 		if( v <= alpha)
	 			return v, move
	 	}
	 	return v, move 
	 }
	 */
	
	/**
	 * This variable is used to track when we should end the iterative deepening search based on the time.
	 * While we could pass this recursively, making it a global variable means that we use less memory, and, hence, 
	 * probably run faster.
	 */
	private static long END_AT = 0; 

	
	public static byte[] IterativeABSearch(State state)
	{
		return IterativeABSearch(state, Consts.timelimit);
	}
	
	/**
	 * Searches for the best move w/ an iterative deepening A-B minmax search. If null, game is over or we must pass. 
	 * @param state The state to start searching from. 
	 * @return A byte[] that represents the best action to make. 
	 */
	public static byte[] IterativeABSearch(State state, long t)
	{
		//Get the current time in milliseconds so that way we know when to end our search 
		final long pre = System.currentTimeMillis(); 
		//Set end at to be the current time, plus a time limit, minus a margin for safety 
		END_AT = pre + (t - 150);//(Consts.timelimit - 150); //TODO: DO BETTER PREEMPT!
		
		//Variables for tracking the best possible move and current depth. If we return null, we know we must pass 
		Pair ans = null;
		int depth = 5; 
		
		//While we are still in our time limit
		while(System.currentTimeMillis() < END_AT)
		{
			//Run the search based on our current state
			final Pair curr = MaxValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
			if(curr != null)
				ans = curr; 
			//Increment depth 
			depth++;  //TODO: WHICH VERSION	
		}
		
//		System.out.println(ans.utility + " (" + (System.currentTimeMillis() - pre) + " ms; depth: " + depth + ") ");
		
		//Return the action. 
		return ans.action;
	}
	
	/**
	 * Processes a max node in our search. 
	 * @param state The current state. 
	 * @param alpha The alpha value in minmax search. 
	 * @param beta The beta value in minmax search. 
	 * @param depth The max search depth
	 * @return A pair that stores the best action along with its utility 
	 */
	private static final Pair MaxValue(State state, float alpha, float beta, int depth)
	{
		//If we have exceeded our runtime, then return null. 
		if(System.currentTimeMillis() >= END_AT) return null;
		
		//If the state is terminal, then return the utility of the state and null for the action. 
		if(Game.isTerminal(state) || depth == 0) return new Pair(Game.utility(state), null);
		
		//Variables to store the min move utility and the corresponding move. 
		float v = Integer.MIN_VALUE; 
		byte[] move = null; 
		
		final APQ<EvaluatedAction> actions = new AMaxPQ<EvaluatedAction>(10);
		state.getMoves(actions, state.getPlayer());
		
		
		
		//Get a list of our possible actions from the state
//		final List<byte[]> actions = Game.actions(state);
		
		//If our list is empty, we must pass. 
		if(actions.isEmpty())
		{
			final Pair result = MinValue(Game.pass(state), alpha, beta, depth - 1);
			
			//If our result is null, we hit a time limit, so continue to return null.
			//This ensures we explore a tree of an equal depth. 
			if(result == null) return null;
			
			return new Pair(result.utility, null); //Essentially passing
		}
		
		//Otherwise, check our list of actions to determine which is the best. 
//		for(byte[] a : actions)
//		for(EvaluatedAction a : actions)
		while(!actions.isEmpty())
		{
			final EvaluatedAction a = actions.pop();
			//Run the MinVal part of the search
			final Pair result = MinValue(a.state, alpha, beta, depth - 1);
			
			//If our result is null, we hit a time limit, so continue to return null.
			//This ensures we explore a tree of an equal depth. 
			if(result == null) return null;
			
			//If our results utility is better then the current,  update the current.
			if(move == null || result.utility > v)
			{
				v = result.utility; 
				move = a.action; 
				
				alpha = Math.max(alpha, v);
			}
			
			if(v >= beta)
				return new Pair(v, move);
		}
		
		return new Pair(v, move);
	}
	
	/**
	 * Processes a min node in our search. 
	 * @param state The current state. 
	 * @param alpha The alpha value in minmax search. 
	 * @param beta The beta value in minmax search. 
	 * @param depth The max search depth
	 * @return A pair that stores the best action along with its utility 
	 */
	private static final Pair MinValue(State state, float alpha, float beta, int depth)
	{
		//If we exceed our time limit, return null
		if(System.currentTimeMillis() >= END_AT) return null;
		
		//If the state is terminal, then return the utility of the state and null for the action. 
		if(Game.isTerminal(state) || depth == 0) return new Pair(Game.utility(state), null);
		
		//Get a list of our possible actions from the state
		//final List<byte[]> actions = Game.actions(state);
		
		final APQ<EvaluatedAction> actions = new AMinPQ<EvaluatedAction>(10);
		state.getMoves(actions, state.getPlayer());
		
		//If our list is empty, we must pass. 
		if(actions.isEmpty())
		{
			final Pair result = MaxValue(Game.pass(state), alpha, beta, depth - 1);
			
			//If our result is null, we hit a time limit, so continue to return null.
			//This ensures we explore a tree of an equal depth. 
			if(result == null) return null;
			
			return new Pair(result.utility, null); //Essentially passing
		}
		
		//Variables to store the max move utility and the corresponding move. 
		float v = Integer.MAX_VALUE;
		byte[] move = null; 
		
		
//		Otherwise, check our list of actions to determine which is the best. 
//		for(byte[] a : actions)
		while(!actions.isEmpty())
		{
			final EvaluatedAction a = actions.pop();
			//Run the MinVal part of the search
			final Pair result = MaxValue(a.state, alpha, beta, depth - 1);
			
			//If our result is null, we hit a time limit, so continue to return null.
			//This ensures we explore a tree of an equal depth. 
			if(result == null) return null;
			
			//If our results utility is better then the current,  update the current.
			if(move == null || result.utility < v)
			{
				v = result.utility;
				move = a.action;
				
				beta = Math.min(beta, v);
			}
			
			if(v <= alpha)
				return new Pair(v, move);
		}
		return new Pair(v, move);
	}
	

	public static final class EvaluatedAction implements Comparable<EvaluatedAction>{
		byte[] action;
		State state;
		Float utility;
		
		public EvaluatedAction(byte[] action, State state) {
			this.action = action;
			this.state = state;
			this.utility = Game.utility(state); 
		}

		@Override
		public int compareTo(EvaluatedAction o) {
			return utility.compareTo(o.utility);
		} 
		
		
		
	}
	private static final class Pair {
		float utility; 
		byte[] action; 
		
		public Pair(float u, byte[] a)
		{
			this.utility = u; 
			this.action = a; 
		}
	}
}
