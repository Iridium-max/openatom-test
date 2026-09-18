public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello world");
        GreetingService service=new GreetingService();
        service.sayHello("Max");
    }
}
