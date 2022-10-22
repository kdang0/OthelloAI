package com.ahfriedman.othelloai.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.ahfriedman.othelloai.OthelloAI;
import com.ahfriedman.othelloai.agents.Agent;
import com.ahfriedman.othelloai.agents.GeneticNNAgent;
import com.ahfriedman.othelloai.agents.SearchAgent;
import com.ahfriedman.othelloai.models.Game;
import com.ahfriedman.othelloai.models.State;

/**
 * Class used to manage Interprocess communication w/ the referee
 */
public class IPC {

	/**
	 * Used to track if we have initialized our agent or not. 
	 */
	private static boolean INITIALIZED; 
	
	private static Agent agent = null; 
	{
		try {
			agent = (new File("1633876307589-0.5546875-2.0.bin-local2")).exists() ? GeneticNNAgent.loadFromFile("1633876307589-0.5546875-2.0.bin-local2") : new SearchAgent(Consts.timelimit);
		} 
		catch (Exception e)
		{
			agent = new SearchAgent(Consts.timelimit);
		}
	}

	/**
	 * This function is used to start our interactions with the referee
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void ListenForReferee() throws IOException, InterruptedException
	{
		INITIALIZED = false;

		//According to the documentation, this (java's file watcher) might not work perfectly on all systems. 
		//However, according to Professor Ruiz, its fine as long as it works on ours/some. 

		//Get the current file directory
		final Path path = (new File("")).toPath();

		//Get a file reference for the end game file
		final File GameOverFile = new File("end_game");

		//The name of the file that will be written when it is our turn to go
		final String GO_FILE = Consts.PROGRAM_NAME + ".go";

		//Set up a watch service for files being modified and created so that way we can easily know when to update.
		final WatchService watcher = FileSystems.getDefault().newWatchService();
		path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);


		//this loop runs until the game has ended
		while(true)
		{
			//Get the most recent events
			final WatchKey key = watcher.take();

			//Iterate through the events
			for(WatchEvent<?> e : key.pollEvents())
			{
				@SuppressWarnings("unchecked") //B/c we are only using ENTRY_MODIFY and ENTRY_CREATE, we will always get WachEvent<Path>s so this is safe
				final WatchEvent<Path> event = (WatchEvent<Path>) e;
				final Path filename = event.context();

				//If the file causing the event has the name of the GO_FILE, it is our turn
				if (filename.toString().equals(GO_FILE)) {

					if(event.kind() != StandardWatchEventKinds.ENTRY_CREATE) //Only run when file shows up. 
						break;

					//Check if the game is over
					if(GameOverFile.exists())
					{
						GameOver(); 
						return; 
					}

					//Otherwise, we must take our turn
					TakeTurn(); 

				}
				else if(filename.toString().equals("end_game"))
				{
					//Check if the game is over
					if(GameOverFile.exists())
					{
						GameOver(); 
						return; 
					}
				}
			}

			if(!key.reset()) //Reset the key so that way we can watch for more events
				break;  //We can no longer watch directory for some reason. Probably an error
		}
	}


	/**
	 * This function is called when the game ends
	 * @throws IOException
	 */
	private static void GameOver() throws IOException {
		//Read the move file, and process it. If it turns out that it was our move, then nothing will happen from this function call. 
		final String line = readMoveFile(); 
		
		if(line != null) //If opponent times out on first move, line may be null, but its not an error
			ProcessOpponentMove(line); 

		//Get the number of tiles for both players. 
		final byte playerTiles = OthelloAI.STATE.getPlayerTiles();
		final byte opponentTiles = OthelloAI.STATE.getOpponentTiles();

		if(!Game.isTerminal(OthelloAI.STATE))
		{
			System.out.println("Warning: Game ended earlier than expected. It is possible that a player exceded the time limit.");
		}


		if(playerTiles == opponentTiles)
			System.out.println("Game Over! Draw.");
		else if(playerTiles > opponentTiles)
			System.out.println("Game Over! We win.");
		else 
			System.out.println("Game Over! Opponent wins.");

		System.exit(0);
	}


	/**
	 * Reads the move file and returns the first line in it. 
	 * @return The string that is the first line in the move file. 
	 * @throws IOException
	 */
	private static String readMoveFile()  throws IOException 
	{
		final BufferedReader reader = new BufferedReader(new FileReader("move_file"));
		final String line = reader.readLine(); 
		reader.close();
		return line; 
	}

	/**
	 * Given the line from the move file, this function processes the move.
	 * @param line The line from the move file. 
	 */
	private static void ProcessOpponentMove(String line)
	{
		//Split the line over spaces
		final String[] data = line.split(" "); 

		//If the data length is not 3, then something is wrong with the file format. 
		if(data.length != 3)
			throw new Error("Invalid move file syntax: " + line);

		final String move_player = data[0];
		//If our player was the most recent to move, print a warning, and return so that way we do not continue to process
		if(move_player.equals(Consts.PROGRAM_NAME))
		{
			System.out.println("Warning: We were the last player to move. It is possible that the game is over.");
			return;
		}

		//If our move is not a pass
		if(!data[1].equals("P"))
		{
			//Convert the string representation to the byte representation of the action
			final byte[] opponent_move = State.StringToByteCoordinates(data[1], data[2]);

			if(opponent_move == null)
			{
				System.out.println("Opponent made invalid move (out of bounds): " + line);
				System.out.println("We win.");
				System.exit(0);
			}
			
			//If the move is invalid, the move function will display information about it and end the program's execution
			OthelloAI.STATE.move(OthelloAI.OPPONENT, opponent_move);
		}
		else //Otherwise, pass 
		{
			if(OthelloAI.STATE.getMoves(OthelloAI.STATE.getPlayer()).size() != 0)
			{
				System.out.println("Opponent made invalid pass. We win.");
				System.exit(0);
			}
			OthelloAI.STATE.pass();
		}

	}

	/**
	 * This function is used by our agent when it goes to make a move. 
	 * @throws IOException
	 */
	private static void TakeTurn() throws IOException {
		//Must read the move file 
		final String line = readMoveFile(); 

		//If we have not initialized the program (we haven't run once before)
		if(!INITIALIZED)
		{
			INITIALIZED = true; 
			
			//Check if the move file is empty. If it is, we are the first player, and we can proceed normally.
			if(line == null || line.isEmpty())
			{
				//We are going first. 
				RunMove();
				return; 
			}

			//Opponent went first, so swap the players, and then process the move. 
			OthelloAI.AGENT_PLAYER = State.AG2;
			OthelloAI.OPPONENT = State.AG1; 
		}


		//Process the line, and then take our turn. 
		ProcessOpponentMove(line); 	
		RunMove(); 
	}


	/**
	 * This function is used for our agent to pick and run a move
	 * @throws IOException
	 */
	private static final void RunMove() throws IOException { //FIXME: TRY WRITING FILES BETTER? Way to lock file?
		//Search for a good move
		final byte[] move = agent.RunMove(OthelloAI.STATE);
		
		//If the move is null, we must pass. 
		if(move == null)
		{
			final FileWriter writer = new FileWriter("move_file");
			writer.write(Consts.PROGRAM_NAME + " P 1");
			writer.flush();
			writer.close();

			OthelloAI.STATE.pass();

			return;
		}

		//Otherwise, take the move, and write the move to the file. 
		OthelloAI.STATE = OthelloAI.STATE.move(OthelloAI.AGENT_PLAYER, move);

		final FileWriter writer = new FileWriter("move_file");
		writer.write(Consts.PROGRAM_NAME + " " + State.byteCoordinatesToMoveString(move));
		writer.flush();
		writer.close();
	}

}
