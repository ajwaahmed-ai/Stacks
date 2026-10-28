/**
 * An array-based implementation of the {@link Stack} interface.
 * <p>
 * This class maintains three internal arrays:
 * <ul>
 *   <li><code>a</code>: Stores the actual stack elements.</li>
 *   <li><code>a2</code>: Tracks the minimum element at each stack depth.</li>
 *   <li><code>a3</code>: Tracks the maximum element at each stack depth.</li>
 * </ul>
 * </p>
 */
public class ArrayStack implements Stack {

    /** Primary array holding stack elements. */
    private Object a[];
    /** Auxiliary array tracking minimum values at each depth. */
    private Object a2[];
    /** Auxiliary array tracking maximum values at each depth. */
    private Object a3[];
    /** The current number of elements in the stack. */
    private int size;

    /**
     * Constructs an empty ArrayStack with the specified initial capacity.
     *
     * @param capacity the initial size of the internal storage arrays
     */
    public ArrayStack(int capacity) {
        a = new Object[capacity];
        a2 = new Object[capacity];
        a3 = new Object[capacity];
    }

    /**
     * Doubles the capacity of all internal storage arrays when full.
     */
    public void resize() {
        // Double capacity of main array
        Object array1[] = a;
        a = new Object[2 * a.length];
        System.arraycopy(array1, 0, a, 0, array1.length);

        // Double capacity of min-tracking array
        Object array2[] = a2;
        a2 = new Object[2 * a2.length];
        System.arraycopy(array2, 0, a2, 0, array2.length);

        // Double capacity of max-tracking array
        Object array3[] = a3;
        a3 = new Object[2 * a3.length];
        System.arraycopy(array3, 0, a3, 0, array3.length);
    }

    /**
     * Checks if the stack is empty.
     *
     * @return {@code true} if the stack contains no elements; {@code false} otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements in the stack.
     *
     * @return the current stack size
     */
    public int size() {
        return size;
    }

    /**
     * Pushes an element onto the top of the stack and updates min/max tracking.
     *
     * @param obj the element to push onto the stack
     */
    public void push(Object obj) {

        // Resize arrays if full capacity is reached
        if (size == a.length)
            resize();

        // Update auxiliary min array (a2)
        if (size == 0) {
            a2[size] = obj;
        } else if ((Integer) a2[size - 1] > (Integer) obj) {
            a2[size] = obj;
        } else {
            a2[size] = a2[size - 1];
        }

        // Update auxiliary max array (a3)
        if (size == 0) {
            a3[size] = obj;
        } else if ((Integer) a3[size - 1] < (Integer) obj) {
            a3[size] = obj;
        } else {
            a3[size] = a3[size - 1];
        }

        // Insert element into primary array and increment size
        a[size] = obj;
        size++;
    }

    /**
     * Retrieves the element at the top of the stack without removing it.
     *
     * @return the top element
     * @throws IllegalStateException if the stack is empty
     */
    public Object peek() {

        if (size == 0)
            throw new IllegalStateException("Stack is empty");
        return a[size - 1];
    }

    /**
     * Retrieves the element at the bottom of the stack (first pushed element).
     *
     * @return the bottom element
     * @throws IllegalStateException if the stack is empty
     */
    public Object peekBottomElement() {
        if (size == 0)
            throw new IllegalStateException("Stack is empty");
        return a[0];
    }

    /**
     * Retrieves the element at the middle index of the stack.
     *
     * @return the middle element
     * @throws IllegalStateException if the stack is empty
     */
    public Object peekMiddleElement() {
        if (size == 0)
            throw new IllegalStateException("Stack is empty");
        return a[size / 2];
    }

    /**
     * Removes and returns the element at the top of the stack.
     *
     * @return the popped element
     * @throws IllegalStateException if the stack is empty
     */
    public Object pop() {

        if (size == 0)
            throw new IllegalStateException("Stack is empty");
        Object x = a[size - 1];

        // Clear references to prevent memory leaks
        a[size - 1] = null;
        a2[size - 1] = null;
        a3[size - 1] = null;

        size--;

        return x;
    }

    /**
     * Returns the minimum element currently in the stack in O(1) time.
     *
     * @return the minimum element
     * @throws IllegalStateException if the stack is empty
     */
    public Object min() {

        if (size == 0)
            throw new IllegalStateException("Stack is empty");
        return a2[size - 1];
    }

    /**
     * Returns the maximum element currently in the stack in O(1) time.
     *
     * @return the maximum element
     * @throws IllegalStateException if the stack is empty
     */
    public Object max() {

        if (size == 0)
            throw new IllegalStateException("Stack is empty");
        return a3[size - 1];
    }

    /**
     * Creates an array containing the stack elements in reverse order (bottom to top).
     *
     * @return an array of objects representing the reversed stack
     */
    public Object[] reverse_Stack() {
        Object[] reverse = new Object[size];
        int j = 0;
        for (int i = size - 1; i >= 0; i--) {
            reverse[j] = a[i];
            j++;
        }
        return reverse;
    }

    /**
     * Checks whether another ArrayStack is equal to this stack.
     *
     * @param arr the other ArrayStack to compare with
     * @return {@code true} if both stacks have equal elements in the same order; {@code false} otherwise
     */
    public boolean equals(ArrayStack arr) {
        if (this.size != arr.size)
            return false;

        for (int i = 0; i < this.size; i++) {
            if (!this.a[i].equals(arr.a[i]))
                return false;
        }
        return true;
    }

    /**
     * Converts this ArrayStack into a {@link LinkedStack}.
     *
     * @return a LinkedStack containing the elements of this stack
     */
    public LinkedStack toLinkedStack() {

        LinkedStack s1 = new LinkedStack();
        for (int i = 0; i < this.size; i++) {
            s1.push(this.a[i]);
        }
        return s1;
    }

    /**
     * Displays elements of the array stack from top to bottom on standard output.
     */
    public void displayArray() {
        for (int i = size - 1; i >= 0; i--) {
            System.out.println(a[i]);
        }
    }

    /**
     * Merges another ArrayStack onto this one, returning a new ArrayStack.
     *
     * @param A2 the second ArrayStack to merge
     * @return a new ArrayStack containing elements from both stacks
     */
    public ArrayStack merge(ArrayStack A2) {
        int A1size = this.size();
        int A2size = A2.size();

        ArrayStack A3 = new ArrayStack(A1size + A2size);

        for (int i = 0; i < A1size; i++) {
            A3.push(this.a[i]);
        }
        for (int i = 0; i < A2size; i++) {
            A3.push(A2.a[i]);
        }

        return A3;
    }

    /**
     * Returns a string representation of the stack contents.
     *
     * @return a space-separated string of elements from bottom to top
     */
    public String toString() {
        StringBuffer temp = new StringBuffer();

        for (int i = 0; i < size; i++) {
            temp.append(a[i] + " ");
        }

        return temp.toString();
    }

    /**
     * Main method demonstrating usage and operations of ArrayStack.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {

        ArrayStack a1 = new ArrayStack(5);

        a1.push(10);
        a1.push(20);
        a1.push(100);
        a1.push(40);
        a1.push(50);

        ArrayStack a2 = new ArrayStack(5);

        a2.push(60);
        a2.push(70);
        a2.push(80);
        a2.push(90);
        a2.push(100);

        ArrayStack a3 = a1.merge(a2);
        a3.displayArray();
    }
}
