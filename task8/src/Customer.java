
public class Customer implements Runnable {

    private final String name;
    private final Bar bar;

    public Customer(String name, Bar bar) {
        this.name = name;
        this.bar = bar;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            try {
                // 从吧台取走一杯奶茶
                String milkTea = bar.take(name);

                // 模拟喝奶茶的时间
                Thread.sleep(800);

                System.out.println(
                        name + "喝完了第" + i + "杯奶茶：" + milkTea
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        System.out.println(name + "喝完10杯奶茶，满足了！");
    }
}
