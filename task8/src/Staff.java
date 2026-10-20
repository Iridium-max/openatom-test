
public class Staff implements Runnable {

    private final String name;
    private final Bar bar;

    public Staff(String name, Bar bar) {
        this.name = name;
        this.bar = bar;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            try {
                Thread.sleep(500);

                String milkTea = name + "的第" + i + "杯奶茶";
                bar.put(name, milkTea);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        System.out.println(name + "制作完10杯奶茶，下班了！");
    }
}
