public class main3 {
    public static void main(String[] args) {
        MyArrayList<String> list=new MyArrayList<>();
        list.add("单号A");
        list.add("单号B");
        list.add("单号C");
        list.add("单号D");
        list.add("单号E");

        System.out.println("获取索引[2]的元素：" + list.get(2));

        list.remove(1);

        System.out.println("验证移位，此时索引[1]的元素变为了：" + list.get(1));
    }
}
