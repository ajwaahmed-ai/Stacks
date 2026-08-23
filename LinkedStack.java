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

        int length = 0;
        int counter = 0;
        Object x = top.data;
        for(Node i = top; i.next != null; i = i.next){
            length++;
        }
        for(Node j = top; j.next != null; j = j.next){
            if(counter == length/2){
                x = j.data;
            };
            counter++;
        }
        return x;
    }

    public Object pop(){
        
        if (size == 0)
            throw new IllegalStateException("stack is empty");

        Object obj = top.data;
        top = top.next;
        size--;

        return obj;
    }

    public Node reverse_stack(){
        Node start = null;
        Node p = null;
        Object[] array = new Object[size];

        Node i = top;
        int j = 0;
        while(i != null){
            array[j] = i.data;
            i = i.next;
            j++;
        }

        int l = array.length - 1;
        while(l >= 0){
            Node newNode = new Node(array[l]);

            if(start == null){
                start = newNode;
                p = start;
            } else{
                p.next = newNode;
                p = p.next;
            }
            l--;

        }

        return start;
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

    public void display(){
        for(Node i = top; i != null; i = i.next){
            System.out.println(i.data);
        }
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

        Node p = this.reverse_stack();
        Node q = l2.reverse_stack();
        LinkedStack l3 = new LinkedStack();

        for(Node i = p; i != null; i = i.next){
            l3.push(i.data);
        }
        for(Node j = q; j != null; j = j.next){
            l3.push(j.data);
        }

        return l3;
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

        s3.display();


        

        
    }
}
