package com.ahfriedman.othelloai.agents;

import java.io.IOException;
import java.util.List;

import com.ahfriedman.othelloai.models.State;

/**
 * This class represents the Game class from the book. 
 */
public interface Agent {

	public byte[] RunMove(State s);

	public List<Agent> produce() throws IOException, ClassNotFoundException;
	
	public Agent mutateOne() throws IOException, ClassNotFoundException;
}
