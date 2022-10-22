package com.ahfriedman.othelloai.NN;

import java.util.concurrent.Callable;

import com.ahfriedman.othelloai.agents.Agent;
import com.ahfriedman.othelloai.agents.SearchAgent;
import com.ahfriedman.othelloai.utils.InternalGame;
import com.ahfriedman.othelloai.utils.InternalGame.AgentHeuristic;

public class TrainThread implements Callable<AgentHeuristic> {

	
	private Agent agent;
	
	public TrainThread(Agent agent)
	{
		this.agent = agent;
	}
	

	@Override
	public AgentHeuristic call() throws Exception {
		// TODO Auto-generated method stub
		return InternalGame.compare(new SearchAgent(1_000), agent)[1];
	}

}
