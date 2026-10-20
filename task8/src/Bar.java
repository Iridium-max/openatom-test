import java.util.LinkedList;
import java.util.Queue;

public class Bar {
    private static final int MAX_CAPACITY=5;
    private final Queue<String> milkTeas=new LinkedList<>();

    public synchronized void put(String staffName, String milkTea)
            throws InterruptedException {

        // 如果吧台已满，店员等待
        while (milkTeas.size() >= MAX_CAPACITY) {
            wait();
        }

        // 把奶茶放入吧台
        milkTeas.add(milkTea);

        // 打印制作信息
        System.out.println(
                staffName + "制作了" + milkTea
                        + "，吧台当前数量：" + milkTeas.size()
        );

        // 通知其他正在等待的线程
        notifyAll();
    }

    public synchronized String take(String customerName)
            throws InterruptedException {

        // 如果吧台没有奶茶，顾客就等待
        while (milkTeas.isEmpty()) {
            wait();
        }

        // 取走队列最前面的一杯奶茶
        String milkTea = milkTeas.poll();

        // 打印取奶茶的信息
        System.out.println(
                customerName + "取走了" + milkTea
                        + "，吧台剩余数量：" + milkTeas.size()
        );

        // 通知其他正在等待的线程
        notifyAll();

        // 返回取走的奶茶
        return milkTea;
    }


}
