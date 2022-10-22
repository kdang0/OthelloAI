package com.ahfriedman.othelloai.agents;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ahfriedman.othelloai.models.Game;
import com.ahfriedman.othelloai.models.State;

public class GeneticNNAgent implements Agent, Serializable
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6376773774178914730L;

	private Node output;
	private List<List<Node>> nodes; 

	public GeneticNNAgent()
	{

		nodes = new ArrayList<List<Node>>();
		for(int i = 0; i < 5; i++)
		{
			nodes.add(new ArrayList<Node>());
		}

		output = new Node();

		nodes.get(nodes.size() - 1).add(output);

		for(int i = 0; i < 64; i++)
		{
			final Node curr = new Node();


			nodes.get(0).add(curr);

		}




	}

	public GeneticNNAgent mutate(int times)
	{

		for(int i = 0; i < times; i++)
		{
			boolean changed = false;
			while(!changed)
			{
				final double r = Math.random();
				if(r < .60)
					changed = MUTATE_CHANGE_SYNAPSE_WEIGHT();
				else if(r < .70)
					changed = MUTATE_DELETE_SYNAPSE_WEIGHT();
				else if(r < .95)
					changed = MUTATE_ADD_SYNAPSE();
				else
					changed = MUTATE_ADD_NEURON();
			}
		}

		return this; 
	}

	/**
	 * This function is used for our agent to pick and run a move
	 * @return 
	 */
	public byte[] RunMove(State s) { //FIXME: TRY WRITING FILES BETTER? Way to lock file?
		//Search for a good move
		//		return IterativeSearch.IterativeABSearch(OthelloAI.STATE);

		//		final LinkedList<Node> queue = new LinkedList<Node>();

		final List<byte[]> moves = Game.actions(s);

		if(moves.isEmpty())
			return null; //Passing

		byte[] move = null; 
		double value = Integer.MIN_VALUE;

		for(byte[] b : moves)
		{
			double cv = feedforward(Game.result(s, b));

			if(cv > value || move == null)
			{
				move = b;
				value = cv; 
			}
		}


		return move;
	}


	private double feedforward(State s) {
		int ctr = 0; 

		for(int r = 0; r < 8; r++)
		{
			for(int c = 0; c < 8; c++)
			{
				final Node curr = nodes.get(0).get(ctr);

				if(s.getBoard()[r][c] == State.CLR)
					curr.value = 0;
				else if(s.getBoard()[r][c] == s.getPlayer())
					curr.value = 1; 
				else
					curr.value = -1; 
				ctr++;
			}
		}

		for(int r = 1; r < nodes.size(); r ++)
		{
			for(int c = 0; c < nodes.get(r).size(); c++)
			{
				final Node curr = nodes.get(r).get(c);

				curr.value = Math.tanh(curr.parents.stream().map(x -> {
					return x.weight * x.parent.value; 
				}).reduce(0.0, (a, b) -> a + b));
			}
		}


		return nodes.get(nodes.size() - 1).get(0).value;
	}





	private boolean MUTATE_ADD_SYNAPSE()
	{
		for(int t = 0; t < 100; t++)
		{
			final int a = (int) (Math.random() * nodes.size());
			final int b = (int) (Math.random() * nodes.size());

			if(Math.abs(a - b) <= 1)
				continue;


			final int lp = Math.min(a, b);
			final int lc = Math.max(a, b);




			if(nodes.get(lp).isEmpty() || nodes.get(lc).isEmpty())
				continue; 

			final int ip = (int) (Math.random() * nodes.get(lp).size());
			final int ic = (int) (Math.random() * nodes.get(lc).size());

			final Node p = nodes.get(lp).get(ip);
			final Node c = nodes.get(lc).get(ic);

			connect(p, c, Math.random() * 2 - 1);

			//			nodes.get(rand(lp + 1, lc - 1)).add(c);

		}

		return false; 
	}


	private int rand(int Min, int Max)
	{
		return Min + (int)(Math.random() * ((Max - Min) + 1));
	}

	private boolean MUTATE_ADD_NEURON()
	{
		for(int t = 0; t < 100; t++)
		{
			final int a = rand(1, nodes.size() - 2);
			final int b = rand(1, nodes.size() - 2);

			if(Math.abs(a - b) <= 2)
				continue;


			final int lp = Math.min(a, b);
			final int lc = Math.max(a, b);




			if(nodes.get(lp).isEmpty() || nodes.get(lc).isEmpty())
				continue; 

			final int ip = (int) (Math.random() * nodes.get(lp).size());
			final int ic = (int) (Math.random() * nodes.get(lc).size());

			final Node p = nodes.get(lp).get(ip);
			final Node c = nodes.get(lc).get(ic);

			//FIXME: VERIFY
			final Node curr = new Node(); 

			connect(p, curr, Math.random() * 2 - 1);
			connect(curr, c, Math.random() * 2 - 1);

			connect(p, c, Math.random() * 2 - 1);

			nodes.get(rand(lp + 1, lc - 1)).add(curr);

		}

		return false; 
	}

	private boolean MUTATE_CHANGE_SYNAPSE_WEIGHT()
	{
		for(int t = 0; t < 100; t++)
		{
			final int a = (int) (Math.random() * nodes.size());



			if(nodes.get(a).isEmpty())
				continue; 

			final int ip = (int) (Math.random() * nodes.get(a).size());

			if(nodes.get(a).get(ip).parents.isEmpty())
				continue;


			final int cindex = (int) (Math.random() * nodes.get(a).get(ip).parents.size());

			nodes.get(a).get(ip).parents.get(cindex).weight += (Math.random() * 2 - 1) * 0.01; 
			return true;

		}

		return false; 
	}


	private boolean MUTATE_DELETE_SYNAPSE_WEIGHT()
	{
		for(int t = 0; t < 100; t++)
		{
			final int a = (int) (Math.random() * nodes.size());



			if(nodes.get(a).isEmpty())
				continue; 

			final int ip = (int) (Math.random() * nodes.get(a).size());

			if(nodes.get(a).get(ip).parents.isEmpty())
				continue;


			final int cindex = (int) (Math.random() * nodes.get(a).get(ip).parents.size());


			final Connection c = nodes.get(a).get(ip).parents.get(cindex);

			c.child.parents.remove(c); //FIXME: VERIFY
			//			c.parent.children.remove(c);

			//FIXME: REMOVE IF UNCONNECTED!!!
			return true;

		}

		return false; 
	}

	private void connect(Node parent, Node child, double weight)
	{
		final Connection c = new Connection();
		c.parent = parent;
		c.child = child;
		c.weight = weight; 

		//		parent.children.add(c);
		child.parents.add(c);
	}

	private static class Node implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 359657231566321732L;

		double value; 

		List<Connection> parents = new ArrayList<Connection>();
		//		List<Connection> children = new ArrayList<Connection>();

	}

	private static class Connection implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -9132569032967600298L;
		double weight;
		Node parent;
		Node child;
	}


	public List<Agent> produce() throws IOException, ClassNotFoundException
	{
		List<Agent> ans = new ArrayList<Agent>();

		ans.add(this);

		for(int i = 0; i < 10 - 1; i++)
		{
			//https://stackoverflow.com/questions/64036/how-do-you-make-a-deep-copy-of-an-object
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(this);
			oos.flush();
			oos.close();
			bos.close();
			byte[] byteData = bos.toByteArray();

			ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
			GeneticNNAgent clone = (GeneticNNAgent) new ObjectInputStream(bais).readObject();

			clone.mutate(1);
			ans.add(clone);
		}


		return ans;
	}


	//FIXME: MAKE POPULATE USE THIS?
	public Agent mutateOne() throws IOException, ClassNotFoundException
	{
		//https://stackoverflow.com/questions/64036/how-do-you-make-a-deep-copy-of-an-object
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(this);
		oos.flush();
		oos.close();
		bos.close();
		byte[] byteData = bos.toByteArray();

		ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
		GeneticNNAgent clone = (GeneticNNAgent) new ObjectInputStream(bais).readObject();

		clone.mutate(3);

		return clone; 
	}
	public static final GeneticNNAgent loadFromFile(String file) throws ClassNotFoundException, IOException
	{
		final FileInputStream fis = new FileInputStream(file);

		GeneticNNAgent clone = (GeneticNNAgent) new ObjectInputStream(fis).readObject(); 

		fis.close();

		return clone; 
	}

}
