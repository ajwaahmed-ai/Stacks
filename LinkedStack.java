public class LinkedStack implements Stack{
    private int size;
    private Node top;

    public class Node{
        Object data;
        Node next;
        
        Node(Object data){
            this.data = data;
        }
        Node(Object data, Node next){
            this.data = data;
            this.next = next;
        }
    }

    public boolean isEmpty(){
        return (size == 0);
    }

    public int size(){
        return size;
    }

    public void push(Object obj){
        top = new Node(obj, top);
        size++;
    }

    public Object peek(){
        if (size == 0)
            throw new IllegalStateException("stack is empty");
        return top.data;
    }

    public Object peekBottom(){
        if (size == 0)
            throw new IllegalStateException("stack is empty");
        
        Node i = top;
        while(i.next != null){
            i = i.next;
        }
        return i.data;
    }

    public Object peekMiddle(){
        if (size == 0)
            throw new IllegalStateException("stack is empty");

        int mid = size/2;
        Node p = top;
        for(int i = 0; i < mid; i++){
            p = p.next;
        }
        return p.data;
    }

    public Object pop(){
        
        if (size == 0)
            throw new IllegalStateException("stack is empty");

        Object obj = top.data;
        top = top.next;
        size--;

        return obj;
    }

    public void reverse(){
        Node current = this.top;
        Node prev = null;
        while(current != null){
            Node next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        top = prev;
    }

    public boolean equals(LinkedStack s){
        if(this.size != s.size)
            return false;

        Node p = this.top;
        Node q = s.top;

        while(p != null){
            if(!p.data.equals(q.data))
                return false;

                p = p.next;
                q = q.next;
        
        }

        return true;
    }

    public ArrayStack toArrayStack(){
        ArrayStack a1 = new ArrayStack(this.size);

        for(Node i = top; i != null; i = i.next){
            a1.push(i.data);
        }

        ArrayStack a2 = new ArrayStack(a1.size());

        int n = a1.size();
        for(int i = 0; i < n; i++){
            a2.push(a1.pop());
        }
        return a2;

    }

    public LinkedStack merge(LinkedStack l2){
        
        LinkedStack temp1 = new LinkedStack();
        LinkedStack temp2 = new LinkedStack();
        LinkedStack mergeStack = new LinkedStack();

        for(Node i = this.top; i != null; i = i.next){
            temp1.push(i.data);
        }
        for(Node j = l2.top; j != null; j = j.next){
            temp2.push(j.data);
        }

        for(int i = 0; i < this.size; i++){
            mergeStack.push(temp1.pop());
        }
        
        for(int i = 0; i < l2.size; i++){
            mergeStack.push(temp2.pop());
        }

        return mergeStack;
    }

    public String toString() {
        StringBuffer temp = new StringBuffer();

        for(Node i = top; i != null; i = i.next){
            temp.append(i.data + " ");
        }

        return temp.toString();
    }

    public LinkedStack Clone(){
        LinkedStack s1 = new LinkedStack();
        LinkedStack s2 = new LinkedStack();
        Node p = this.top;
        while(p != null){
            s1.push(p.data);
            p = p.next;
        }

        for(int i = 0; i < s1.size; i++){
            s2.push(s1.pop());
        }
        return s2;
    }

    public LinkedStack[] split(){
        LinkedStack s1 = new LinkedStack();
        LinkedStack s2 = new LinkedStack();

        int mid = this.size / 2;
        Node p = this.top;
        
        for(int i = 0; i < mid; i++){
            s1.push(p.data);
            p = p.next;
        }
        for(int i = mid; i < this.size; i++){
            s2.push(p.data);
            p = p.next;
        }

        s1.reverse();
        s2.reverse();

        return new LinkedStack[]{s1,s2};
    }
    
    public static void main(String[] args){
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
