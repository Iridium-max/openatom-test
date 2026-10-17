
public class task5 {

    public static void main(String[] args) {
        PaymentService service = new PaymentService();

        System.out.println("===== 测试金额小于0 =====");
        testPayment(service, -100, "TX001");

        System.out.println("\n===== 测试金额大于10000 =====");
        testPayment(service, 12000, "TX002");

        System.out.println("\n===== 测试金额等于500 =====");
        testPayment(service, 500, "TX003");

        System.out.println("\n===== 测试正常支付 =====");
        testPayment(service, 100, "TX004");
    }

    public static void testPayment(
            PaymentService service,
            double amount,
            String transactionId) {

        try {
            boolean success = service.processPayment(amount, transactionId);
            System.out.println("支付处理结果：" + (success ? "成功" : "失败"));

        } catch (SystemMaintenanceException e) {
            System.out.println("系统异常：" + e.getMessage());

        } catch (Exception e) {
            System.out.println("其他异常：" + e.getMessage());
        }
    }
}
