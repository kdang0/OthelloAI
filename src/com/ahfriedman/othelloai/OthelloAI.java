package com.ahfriedman.othelloai;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


import com.ahfriedman.othelloai.models.State;
import com.ahfriedman.othelloai.utils.Consts;
import com.ahfriedman.othelloai.utils.IPC;

/**
 * Main class
 */
public class OthelloAI {


	//TODO: DO BETTER!

	/**
	 * Byte representation of our agent
	 */
	public static byte AGENT_PLAYER = State.AG1;

	/**
	 * Byte representation of opponent 
	 */
	public static byte OPPONENT = State.AG2; 



	public static State STATE = new State();

	/**
	 * Main function; starts running program
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void main(String...args) throws ClassNotFoundException, IOException, InterruptedException, ExecutionException
	{

		/*
		 * Here, we run with the referee
		 */

		if(args.length == 1)
			Consts.PROGRAM_NAME = args[0];
		
		try 
		{
			IPC.ListenForReferee();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}


	}

}
