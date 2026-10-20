
public class task8 {

    public static void main(String[] args) throws InterruptedException {

        // 1. 创建一个公共吧台
        Bar bar = new Bar();

        // 2. 创建两名店员的线程
        Thread staff1 = new Thread(new Staff("小王", bar));
        Thread staff2 = new Thread(new Staff("小李", bar));

        // 3. 创建两名顾客的线程
        Thread customer1 = new Thread(new Customer("小张", bar));
        Thread customer2 = new Thread(new Customer("小刘", bar));

        // 4. 启动四个线程
        staff1.start();
        staff2.start();
        customer1.start();
        customer2.start();

        // 5. 等待四个线程全部完成
        staff1.join();
        staff2.join();
        customer1.join();
        customer2.join();

        System.out.println("所有店员已下班，所有顾客已满足！");
    }
}
