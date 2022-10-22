package com.ahfriedman.othelloai.agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ahfriedman.othelloai.models.State;
import com.ahfriedman.othelloai.searching.IterativeSearch;

public class SearchAgent implements Agent
{	
	private long t; 
	
	public SearchAgent(long t)
	{
		this.t = t;
	}
	/**
	 * This function is used for our agent to pick and run a move
	 * @return 
	 */
	public byte[] RunMove(State s) { //FIXME: TRY WRITING FILES BETTER? Way to lock file?
		//Search for a good move
		return IterativeSearch.IterativeABSearch(s, t);
	}

	@Override
	public List<Agent> produce() throws IOException, ClassNotFoundException {
		final List<Agent> ans = new ArrayList<Agent>();
		ans.add(this);
		return ans; 
	}
	@Override
	public Agent mutateOne() throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	public Agent duplicate() {
		return new SearchAgent(t);
	}

}
