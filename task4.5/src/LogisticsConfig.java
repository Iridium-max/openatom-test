public class LogisticsConfig {
    private String systemName="顺丰只能仓储系统";
    private LogisticsConfig(){
    }//防止外面new
    private static LogisticsConfig instance = new LogisticsConfig();//防止外面修改或直接访问这个变量
    public static LogisticsConfig getInstance() {
        return instance;
    }
}
