package com.ahfriedman.othelloai.utils;

public class ArrayUtils
{
	/**
	 * Swaps two elements in an array
	 * @param arr The array to swap the elements in 
	 * @param a The index of the first element
	 * @param b The index of the second element
	 */
	public static final void swap(Object[] arr, int a, int b)
	{
		final Object temp = arr[a];

		arr[a] = arr[b];
		arr[b] = temp;
	}

	/**
	 * Prints out the elements in the given array. 
	 * @param arr The array to display. 
	 */
	public static final void printArray(Object[] arr)
	{
		for(int n = 0; n < arr.length; n++)
			System.out.print(arr[n] + ((n + 1) < arr.length ? ", " : "")); //Used to make it so we only print a comma if we have more elements
		System.out.println();
	}
	
	/**
	 * Takes a number of samples from a given array, and returns them as a new array.
	 * @param array The array to take samples from
	 * @param fill The array to fill with samples
	 * @return An array that contains a random set of elements from the original array
	 */
	public static final void sample(Object[] array, Object[] fill)
	{
		if(array == null || fill == null) 
			return;
		
		for(int i = 0; i < fill.length; i++)
		{
			fill[i] = array[(int)(Math.random() * array.length)];
		}
		
	}
}
