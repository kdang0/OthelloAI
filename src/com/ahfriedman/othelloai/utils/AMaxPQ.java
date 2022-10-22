package com.ahfriedman.othelloai.utils;


/**
 * Max Priority queue--inspired by the book's example
 * 
 * This is from my assignment 2
 * @author Alex Friedman
 * @since 11/9/2020
 */
public class AMaxPQ<K extends Comparable<K>> extends APQ<K>
{
	
	/**
	 * Constructs a new MaxPriorityQueue of a given size.
	 * @param size The size of the MaxPriorityQueue (the number of elements it could store w/o a resize in an ideal world)
	 */
	public AMaxPQ(int size)
	{
		super(size);
	}
	
	
	/**
	 * This removes and returns the maximum element in the queue (or null if empty).
	 * 
	 * Because removing the top element from a heap is the same mechanism 
	 * regardless of type (min or max) (the only change is how sink & swim work),
	 * this can use the generic pop method in the parent class, instead of
	 * duplicating code. 
	 * @return The max element in the queue. 
	 */
	public K delMax() { return pop(); }

	
	@Override
	protected void swim(int i)
	{
		//If i < 1, then we are done. 
		if(i < 1)
			return;

		//Get the index of our parent. 
		final int parent = i/2;

		//If our parent is less than 1, we are also done. 
		if(parent < 1)
			return; 

		/*
		 * Determine if we need to swap the elements, and continue to swim. 
		 */
		if(this.queue[i].compareTo(this.queue[parent]) > 0)
		{
			ArrayUtils.swap(this.queue, i, parent);
			swim(parent);
		}
	}

	@Override
	protected void sink(int i)
	{
		if(i < 1)
			return; //Just to prevent infinite recursion if we're given zero somehow

		final int left = 2 * i;

		if(left >= this.queue.length) //If left is larger, then so is right
			return;

		final int right = left + 1; 

		final K leftVal = this.queue[left];

		//Gets the right value, or sets it to be null if its out of bounds
		final K rightVal = (this.queue.length > right) ? this.queue[right] : null;
		
		//If our right side is null
		if(rightVal == null)
		{
			if(leftVal == null) //Make sure or left node is not null.
				return; 

			if(this.queue[i].compareTo(leftVal) < 0)
			{
				ArrayUtils.swap(this.queue, i, left);
				sink(left);
			}
		}
		else //We have a right side
		{
			if(leftVal == null) //This should never occur, but in the off chance it does, this is here.
			{
				if(this.queue[i].compareTo(rightVal) < 0)
				{
					ArrayUtils.swap(this.queue, i, right);
					sink(right);
				}
			}
			else //Compare right and left, and determine which direction to go in.
			{
				final int dir = (leftVal.compareTo(rightVal) > 0) ? left : right;
				
				if(queue[i].compareTo(queue[dir]) < 0)
				{
					ArrayUtils.swap(queue, i, dir);
					sink(dir);
				}
			}
		}
	}
	
	
	public K max() { return this.peek(); }
}
