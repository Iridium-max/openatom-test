
public class PaymentService {

    public boolean processPayment(double amount, String transactionId) {
        try {
            PaymentProcessor processor = new PaymentProcessor();
            processor.pay(amount, transactionId);

            return true;

        } catch (InsufficientFundsException e) {
            System.out.println("余额不足，流水号为" + e.getTransactionId());
            return false;

        } catch (IllegalArgumentException e) {
            throw new SystemMaintenanceException("支付参数异常", e);

        } catch (Exception e) {
            System.out.println("发生未知异常:" + e.getMessage());
            return false;

        } finally {
            System.out.println("执行资源清理任务...");
        }
    }
}
