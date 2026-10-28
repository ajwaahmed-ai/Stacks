/**
 * Interface representing a generic Stack data structure (Last-In-First-Out / LIFO).
 */
public interface Stack {

    /**
     * Returns the current number of elements in the stack.
     *
     * @return the number of elements in the stack
     */
    public int size();

    /**
     * Retrieves, but does not remove, the element at the top of the stack.
     *
     * @return the top element of the stack
     * @throws IllegalStateException if the stack is empty
     */
    public Object peek();

    /**
     * Removes and returns the element at the top of the stack.
     *
     * @return the top element that was removed
     * @throws IllegalStateException if the stack is empty
     */
    public Object pop();

    /**
     * Pushes an element onto the top of the stack.
     *
     * @param obj the element to push onto the stack
     */
    public void push(Object obj);
}
