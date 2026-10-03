public class MyArrayList<T> {
    private Object[] elements;
    private int size;
    public MyArrayList(){
        elements=new Object[3];
        size=0;
    }
    public void add(T element){
        if(size==elements.length){
            int oldLength=elements.length;
            Object[] newElements=new Object[elements.length*2];
            for(int i=0;i<elements.length;i++)
            {
                newElements[i]=elements[i];
            }
            elements=newElements;
            System.out.println("触发底层扩容！当前数组容量由 "
                    + oldLength + " 扩容至 " + elements.length);
        }
        elements[size]=element;
        size++;
        System.out.println("成功添加：" + element);
    }
    public T get(int index){
        if (index < 0 || index >= size) {
            System.out.println("索引越界！");
            return null;
        }
        return (T) elements[index];
    }
    public void remove(int index){
        if (index < 0 || index >= size) {
            System.out.println("索引越界！");
            return;
        }
        Object removedElement = elements[index];
        for(int i=index;i<size-1;i++){
            elements[i]=elements[i+1];
        }
        elements[size-1]=null;
        size--;
        System.out.println("删除了索引[" + index + "]的元素(" + removedElement + ")");
    }
}
