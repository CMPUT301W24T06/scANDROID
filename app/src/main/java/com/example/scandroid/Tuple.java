package com.example.scandroid;

/**
 * Tuple class for holding 2 objects
 * @param <A> The type of the first object
 * @param <B> The type of the second object
 */
//OpenAI, 2024, ChatGPT, How to store 2 objects in a list
public class Tuple<A, B> {
    public final A first;
    public final B second;

    /**
     * Constructor for a tuple object
     * @param first The first object of the pair
     * @param second The second object of the pair
     */
    public Tuple(A first, B second) {
        this.first = first;
        this.second = second;
    }
}
