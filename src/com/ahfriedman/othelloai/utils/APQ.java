package com.ahfriedman.othelloai.utils;

/**
 * This is an abstract class so that I can limit duplicate code between
 * this MaxPQ and MinPQ. In addition, it allows me to test them more easily
 * by providing a common way of storing them. 
 * 
 * This is from my assignment 2
 * 
 * @author Alex Friedman
 *
 * @param <K> The type stored in the queue. 
 */
public abstract class APQ<K extends Comparable<K>>
{ 
	/*
	 * Stores the actual data of the queue
	 * Protected so that the classes that extend it can have access.
	 */
	protected K[] queue;

	/*
	 * Stores the actual size of the queue, as it may be different than the number of elements in the array
	 * Protected so that the classes that extend it can have access.
	 */
	protected int size = 0;

	/**
	 * Constructs a new PriorityQueue of a given size.
	 * @param size The size of the PriorityQueue (the number of elements it could store w/o a resize in an ideal world)
	 */
	@SuppressWarnings("unchecked") //Prevents warnings from the generic cast 
	public APQ(int size)
	{
		this.queue =  (K[]) new Comparable[size + 1];
	}

	/**
	 * Inserts a key into the priority queue
	 * @param key The key to be inserted into the queue. 
	 */
	public synchronized void insert(K key) 
	{
		if(++size >= this.queue.length)
			doubleCapacity();

		this.queue[size] = key; //has to be ++size b/c size starts at 0, and we need to insert at 1

		swim(size); 
	}

	/**
	 * Used to pop the top element off of the queue. 
	 * @return The top element in the queue, or null if it is empty. 
	 */
	public K pop() 
	{
		//If the queue is empty, return null
		if(isEmpty())
			return null; 
		
		//Store the value that we are removing 
		final K ans = this.queue[1];

		//Move the bottom element to the top. 
		this.queue[1] = this.queue[size--];
		
		//Delete the value at the end of the queue
		this.queue[size + 1] = null;

		//Sink the top element
		sink(1);
		
		//Return the answer
		return ans; 
	}

	/**
	 * Used to swim an element at a given index. 
	 * @param i The index of the element to swim.
	 */
	protected abstract void swim(int i);

	/**
	 * Used to sink an element at a given index. 
	 * @param i The index of the element to sink. 
	 */
	protected abstract void sink(int i);


	@SuppressWarnings("unchecked")
	/**
	 * This is used to double the capacity of this queue. 
	 */
	private void doubleCapacity()
	{
		final K[] nxt = (K[]) new Comparable[2 * this.queue.length];

		for (int i = 0; i < this.queue.length; i++)
			nxt[i] = this.queue[i];

		this.queue = nxt;
	}

	/**
	 * Used to determine if the queue is empty
	 * @return true if there are no elements in the queue. False otherwise. 
	 */
	public boolean isEmpty() { return (size == 0); }

	/**
	 * Used to get the number of elements this is currently storing. 
	 * @return The number of elements currently stored in the queue. 
	 */
	public int size() { return size; }
	
	/**
	 * Returns the top value in the heap. 
	 * @return
	 */
	public K peek() { return this.queue[1]; }

}
