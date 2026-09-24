import java.util.Scanner;

public class Toolbox {
    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);
        System.out.println("【欢迎使用多功能工具箱】");
        while(true){
            System.out.println();
            System.out.println("1. 成绩评定");
            System.out.println("2. 空心金字塔");
            System.out.println("3. 递归阶乘");
            System.out.println("4. 退出系统");
            System.out.println("请选择功能： ");
            int choice=scanner.nextInt();
            if(choice==4){
                System.out.println("再见");
                break;
            }
            if(choice==1)
            {
                System.out.println("请输入分数：  ");
                int score=scanner.nextInt();
                if (score < 0 || score > 100) {
                    System.out.println("分数输入不合法，请输入0-100之间的分数！");
                    continue;
                }
                if (score >= 90) {
                    System.out.println("成绩评定（if-else）：A");
                } else if (score >= 80) {
                    System.out.println("成绩评定（if-else）：B");
                } else if (score >= 70) {
                    System.out.println("成绩评定（if-else）：C");
                } else if (score >= 60) {
                    System.out.println("成绩评定（if-else）：D");
                } else {
                    System.out.println("成绩评定（if-else）：F");
                }
                switch (score / 10) {
                    case 10:
                    case 9:
                        System.out.println("成绩评定（switch）：A");
                        break;

                    case 8:
                        System.out.println("成绩评定（switch）：B");
                        break;

                    case 7:
                        System.out.println("成绩评定（switch）：C");
                        break;

                    case 6:
                        System.out.println("成绩评定（switch）：D");
                        break;

                    default:
                        System.out.println("成绩评定（switch）：F");
                }
            }
            if(choice==2)
            {
                System.out.print("请输入金字塔层数：");
                int n=scanner.nextInt();
                for(int i=1;i<=n;i++)
                {
                    for(int j=1;j<=n-i;j++)
                    {
                        System.out.print(" ");//打印前面的空格
                    }
                    for(int k=1;k<=(2*i-1);k++)
                    {
                        if(i==1||i==n||k==1||k==2*i-1)
                        {
                            System.out.print("*");
                        }
                        else {
                            System.out.print(" ");
                        }
                    }
                    System.out.println();
                }

            }
            if(choice==3)
            {
                System.out.print("计算阶乘：");
                int n = scanner.nextInt();

                int result = calculateFactorial(n);

                System.out.println("计算结果：" + result);
            }
        }
        scanner.close();
    }
    //此处未使用不加static的方法
    public static int calculateFactorial(int n){
        if(n==1||n==0){
            return 1;
        }
        return n*calculateFactorial(n-1);
    }
}
