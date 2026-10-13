public class LogisticsOrder {
    private String orderId;
    private String sender;
    private String receiver;
    private boolean fragile;
    private double insuredAmount;
    private LogisticsOrder(Builder builder) {
        this.orderId = builder.orderId;
        this.sender = builder.sender;
        this.receiver = builder.receiver;
        this.fragile = builder.fragile;
        this.insuredAmount = builder.insuredAmount;
    }
    public static class Builder{
        private String orderId;
        private String sender;
        private String receiver;
        private boolean fragile;
        private double insuredAmount;
        public Builder(String orderId)
        {
            this.orderId=orderId;
        }
        public Builder sender(String sender) {
            this.sender = sender;
            return this;
        }
        public Builder receiver(String receiver) {
            this.receiver = receiver;
            return this;
        }

        public Builder fragile(boolean fragile) {
            this.fragile = fragile;
            return this;
        }

        public Builder insuredAmount(double insuredAmount) {
            this.insuredAmount = insuredAmount;
            return this;
        }
        public LogisticsOrder build() {
            return new LogisticsOrder(this);
        }
    }
    @Override
    public String toString() {
        return "订单号[" + orderId + "]"
                + ", 寄件人[" + sender + "]"
                + ", 收件人[" + receiver + "]"
                + ", 易碎品[" + fragile + "]"
                + ", 保价[" + insuredAmount + "元]";
    }
}
