package com.ahfriedman.othelloai.utils;


import com.ahfriedman.othelloai.agents.Agent;
import com.ahfriedman.othelloai.models.Game;
import com.ahfriedman.othelloai.models.State;

/**
 * Class used to manage Interprocess communication w/ the referee
 */
public class InternalGame {
	
	public static boolean showprint = false;
	
	public static class AgentHeuristic implements Comparable<AgentHeuristic>
	{
		public Agent agent;
		public double heuristic;
		public double wins; 
		
		@Override
		public int compareTo(AgentHeuristic o) {
			int n1 = Double.valueOf(wins).compareTo(o.wins);
			if(n1 == 0)
				return Double.valueOf(heuristic).compareTo(o.heuristic);
			return n1;
		}
	}
	
	
	public static AgentHeuristic[] compare(Agent a1, Agent a2)
	{
		final State game1 = new State();
		
		byte ans1 = Play(game1, a1, a2);
		
		final double a1g1 = ((double) game1.getPlayerTiles()) / (double) (game1.getPlayerTiles() + game1.getOpponentTiles());
		final double a2g1 = ((double) game1.getOpponentTiles()) / (double) (game1.getPlayerTiles() + game1.getOpponentTiles());
		
		final State game2 = new State();
		
		byte ans2 = Play(game2, a2, a1);
		
		final double a2g2 = ((double) game2.getPlayerTiles()) / (double) (game2.getPlayerTiles() + game2.getOpponentTiles());
		final double a1g2 = ((double) game2.getOpponentTiles()) / (double) (game2.getPlayerTiles() + game2.getOpponentTiles());
	
		final AgentHeuristic h1 = new AgentHeuristic();
		h1.agent = a1;
		h1.heuristic = (a1g1 + a1g2) / 2.0;
		h1.wins = ((ans1 == 0) ? 0.5 : (ans1 == 1) ? 1 : 0) + 
				  ((ans2 == 0) ? 0.5 : (ans2 == 1) ? 0 : 1); 
		
		
		final AgentHeuristic h2 = new AgentHeuristic();
		h2.agent = a2;
		h2.heuristic = (a2g1 + a2g2) / 2.0;
		h2.wins = ((ans1 == 0) ? 0.5 : (ans1 == 1) ? 0 : 1) + 
				  ((ans2 == 0) ? 0.5 : (ans2 == 1) ? 1 : 0); 
		
		
		return new AgentHeuristic[] {h1, h2};
	
	}
	

	/**
	 * This function is used to start our interactions with the referee
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static byte Play(State state, Agent a1, Agent a2)
	{

		//this loop runs until the game has ended
		while(!Game.isTerminal(state))
		{
//			if(showprint)
//				state.displayMoves(Game.toMove(state));
			
			state.display();
			
			final byte[] move = (Game.toMove(state) == State.AG1) ? a1.RunMove(state) : a2.RunMove(state); 
			
			if(move == null)
				state.pass(); 
			else
			{
				State val = state.move(Game.toMove(state), move);
				
				if(val == null)
				{
					return GameOver(state);
				}
			}
			
	
		}
		return GameOver(state);
	}


	/**
	 * This function is called when the game ends
	 * @throws IOException
	 */
	private static byte GameOver(State s) {

		//Get the number of tiles for both players. 
		final byte playerTiles = s.getPlayerTiles();
		final byte opponentTiles = s.getOpponentTiles();

		if(!Game.isTerminal(s) && showprint)
		{
			System.out.println("Warning: Game ended earlier than expected. It is possible that a player exceded the time limit.");
		}


		if(playerTiles == opponentTiles)
		{
			if(showprint)
				System.out.println("Game Over! Draw.");
			return 0; 
		}
		else if(playerTiles > opponentTiles)
		{
			if(showprint)
			System.out.println("Game Over! We win.");
			return 1; 
		}
		else 
		{
			if(showprint)
			System.out.println("Game Over! Opponent wins.");
			return -1; 
		}
	}
}
