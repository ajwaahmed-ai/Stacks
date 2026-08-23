public class ArrayStack implements Stack {

    private Object a[];
    private Object a2[];
    private Object a3[];
    private int size;

    public ArrayStack(int capacity) {
        a = new Object[capacity];
        a2 = new Object[capacity];
        a3 = new Object[capacity];
    }

    public void resize() {
        Object array1[] = a;
        a = new Object[2 * a.length];
        System.arraycopy(array1, 0, a, 0, array1.length);

        Object array2[] = a2;
        a2 = new Object[2 * a2.length];
        System.arraycopy(array2, 0, a2, 0, array2.length);

        Object array3[] = a3;
        a3 = new Object[2 * a3.length];
        System.arraycopy(array3, 0, a3, 0, array3.length);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void push(Object obj) {

        if (size == a.length)
            resize();

        if (size == 0) {
            a2[size] = obj;
        }else if ((Integer) a2[size - 1] > (Integer) obj) {
            a2[size] = obj;
        }else {
            a2[size] = a2[size - 1];
        }

        if (size == 0) {
            a3[size] = obj;
        }else if ((Integer) a3[size - 1] < (Integer) obj) {
            a3[size] = obj;
        }else {
            a3[size] = a3[size - 1];
        }

        a[size] = obj;
        size++;
    }


    public Object peek() {

        if (size == 0)
            throw new IllegalStateException("Stack is empty");
        return a[size - 1];
    }

    public Object peekBottomElement() {
        if (size == 0)
            throw new IllegalStateException("Stack is empty");
        return a[0];
    }

    public Object peekMiddleElement() {
        if (size == 0)
            throw new IllegalStateException("Stack is empty");
        return a[size / 2];
    }

    public Object pop() {

        if (size == 0)
            throw new IllegalStateException("Stack is empty");
        Object x = a[size - 1];

        a[size - 1] = null;
        a2[size - 1] = null;
        a3[size - 1] = null;

        size--;

        return x;
    }

    public Object min() {

        if (size == 0)
            throw new IllegalStateException("Stack is empty");
        return a2[size - 1];
    }

    public Object max() {

        if (size == 0)
            throw new IllegalStateException("Stack is empty");
        return a3[size - 1];
    }

    public Object[] reverse_Stack() {
        Object[] reverse = new Object[size];
        int j = 0;
        for (int i = size - 1; i >= 0; i--) {
            reverse[j] = a[i];
            j++;
        }
        return reverse;
    }

    public boolean equals(ArrayStack arr){
        if(this.size != arr.size)
            return false;

        for(int i = 0; i < this.size; i++){
            if(!this.a[i].equals(arr.a[i]))
                return false;
        }
        return true;
    }

    public LinkedStack toLinkedStack(){

        LinkedStack s1 = new LinkedStack();
        for(int i = 0; i < this.size; i++){
            s1.push(this.a[i]);
        }
        return s1;
    }

    public void displayArray(){
        for(int i = size - 1; i >= 0; i--){
            System.out.println(a[i]);
        }
    }

    public ArrayStack merge(ArrayStack A2){
        int A1size = this.size();
        int A2size = A2.size();

        ArrayStack A3 = new ArrayStack(A1size + A2size);

        for(int i = 0; i < A1size; i++){
                A3.push(this.a[i]);
        }
        for(int i = 0; i < A2size; i++){
            A3.push(A2.a[i]);
        }

        return A3;     
    }

    public String toString() {
        StringBuffer temp = new StringBuffer();

        for(Node i = head.next; i != head; i = i.next){
            temp.append(i.data + " ");
        }

        return temp.toString();
    }

    public static void main(String[] args) {

        ArrayStack a1 = new ArrayStack(5);

        a1.push(10);
        a1.push(20);
        a1.push(30);
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
