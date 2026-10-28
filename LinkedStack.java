/**
 * A singly-linked list implementation of the {@link Stack} interface.
 */
public class LinkedStack implements Stack {

    /** The number of elements in the stack. */
    private int size;
    /** Reference to the top node of the stack. */
    private Node top;

    /**
     * Inner class representing a node in the singly-linked stack.
     */
    public class Node {
        /** The data payload stored in this node. */
        Object data;
        /** Reference to the next node in the stack. */
        Node next;

        /**
         * Constructs a Node with data payload and null next link.
         *
         * @param data the data to store in the node
         */
        Node(Object data) {
            this.data = data;
        }

        /**
         * Constructs a Node with data payload and next node reference.
         *
         * @param data the data to store in the node
         * @param next the next Node in the list
         */
        Node(Object data, Node next) {
            this.data = data;
            this.next = next;
        }
    }

    /**
     * Checks whether the stack is empty.
     *
     * @return {@code true} if the stack is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * Returns the current size of the stack.
     *
     * @return the number of elements in the stack
     */
    public int size() {
        return size;
    }

    /**
     * Pushes a new element onto the top of the stack.
     *
     * @param obj the element to push
     */
    public void push(Object obj) {
        top = new Node(obj, top);
        size++;
    }

    /**
     * Retrieves the element at the top of the stack without removing it.
     *
     * @return the element at the top of the stack
     * @throws IllegalStateException if the stack is empty
     */
    public Object peek() {
        if (size == 0)
            throw new IllegalStateException("stack is empty");
        return top.data;
    }

    /**
     * Retrieves the element at the bottom of the stack without removing it.
     *
     * @return the element at the bottom of the stack
     * @throws IllegalStateException if the stack is empty
     */
    public Object peekBottom() {
        if (size == 0)
            throw new IllegalStateException("stack is empty");

        Node i = top;
        while (i.next != null) {
            i = i.next;
        }
        return i.data;
    }

    /**
     * Retrieves the element at the middle index of the stack.
     *
     * @return the middle element
     * @throws IllegalStateException if the stack is empty
     */
    public Object peekMiddle() {
        if (size == 0)
            throw new IllegalStateException("stack is empty");

        int mid = size / 2;
        Node p = top;
        for (int i = 0; i < mid; i++) {
            p = p.next;
        }
        return p.data;
    }

    /**
     * Removes and returns the element at the top of the stack.
     *
     * @return the element removed from the top
     * @throws IllegalStateException if the stack is empty
     */
    public Object pop() {

        if (size == 0)
            throw new IllegalStateException("stack is empty");

        Object obj = top.data;
        top = top.next;
        size--;

        return obj;
    }

    /**
     * Reverses the order of nodes in the stack in-place.
     */
    public void reverse() {
        Node current = this.top;
        Node prev = null;
        while (current != null) {
            Node next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        top = prev;
    }

    /**
     * Compares this LinkedStack with another LinkedStack for equality.
     *
     * @param s the other LinkedStack to compare
     * @return {@code true} if both stacks have equal elements in the same sequence; {@code false} otherwise
     */
    public boolean equals(LinkedStack s) {
        if (this.size != s.size)
            return false;

        Node p = this.top;
        Node q = s.top;

        while (p != null) {
            if (!p.data.equals(q.data))
                return false;

            p = p.next;
            q = q.next;
        }

        return true;
    }

    /**
     * Converts this LinkedStack into an {@link ArrayStack}.
     *
     * @return an ArrayStack containing the same elements in identical order
     */
    public ArrayStack toArrayStack() {
        ArrayStack a1 = new ArrayStack(this.size);

        for (Node i = top; i != null; i = i.next) {
            a1.push(i.data);
        }

        ArrayStack a2 = new ArrayStack(a1.size());

        int n = a1.size();
        for (int i = 0; i < n; i++) {
            a2.push(a1.pop());
        }
        return a2;
    }

    /**
     * Merges another LinkedStack with this stack, returning a new merged LinkedStack.
     *
     * @param l2 the second LinkedStack to merge
     * @return a new LinkedStack containing elements from both stacks
     */
    public LinkedStack merge(LinkedStack l2) {

        LinkedStack temp1 = new LinkedStack();
        LinkedStack temp2 = new LinkedStack();
        LinkedStack mergeStack = new LinkedStack();

        for (Node i = this.top; i != null; i = i.next) {
            temp1.push(i.data);
        }
        for (Node j = l2.top; j != null; j = j.next) {
            temp2.push(j.data);
        }

        for (int i = 0; i < this.size; i++) {
            mergeStack.push(temp1.pop());
        }

        for (int i = 0; i < l2.size; i++) {
            mergeStack.push(temp2.pop());
        }

        return mergeStack;
    }

    /**
     * Returns a string representation of the stack contents from top to bottom.
     *
     * @return space-separated string of elements
     */
    public String toString() {
        StringBuffer temp = new StringBuffer();

        for (Node i = top; i != null; i = i.next) {
            temp.append(i.data + " ");
        }

        return temp.toString();
    }

    /**
     * Creates a deep copy of this LinkedStack.
     *
     * @return a new LinkedStack containing identical elements
     */
    public LinkedStack Clone() {
        LinkedStack s1 = new LinkedStack();
        LinkedStack s2 = new LinkedStack();
        Node p = this.top;
        while (p != null) {
            s1.push(p.data);
            p = p.next;
        }

        for (int i = 0; i < s1.size; i++) {
            s2.push(s1.pop());
        }
        return s2;
    }

    /**
     * Splits this stack into two LinkedStack instances at the middle element.
     *
     * @return an array of two LinkedStack objects containing the split halves
     */
    public LinkedStack[] split() {
        LinkedStack s1 = new LinkedStack();
        LinkedStack s2 = new LinkedStack();

        int mid = this.size / 2;
        Node p = this.top;

        for (int i = 0; i < mid; i++) {
            s1.push(p.data);
            p = p.next;
        }
        for (int i = mid; i < this.size; i++) {
            s2.push(p.data);
            p = p.next;
        }

        s1.reverse();
        s2.reverse();

        return new LinkedStack[]{s1, s2};
    }

    /**
     * Main method demonstrating usage and operations of LinkedStack.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        LinkedStack s1 = new LinkedStack();

        s1.push(10);
        s1.push(20);
        s1.push(30);
        s1.push(40);
        s1.push(50);

        LinkedStack s2 = new LinkedStack();

        s2.push(60);
        s2.push(70);
        s2.push(80);
        s2.push(90);
        s2.push(100);

        LinkedStack s3 = s1.merge(s2);
    }
}
