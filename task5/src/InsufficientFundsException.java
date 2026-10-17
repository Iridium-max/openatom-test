public class InsufficientFundsException extends Exception {
    private String transactionId;
    public InsufficientFundsException(String message,String transactionId)
    {
        super(message);//相当于调用父类的构造方法，父类负责保存
        this.transactionId=transactionId;

    }
    public String getTransactionId() {
        return transactionId;
    }
}
