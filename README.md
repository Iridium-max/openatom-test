# OpenAtom Test
这是我的开放原子招新git学习测试项目
## 学习记录
-学习git
-学习github
-学习Markdown

## 面向对象

接口描述你会什么，抽象类描述你是什么；一个类只能继承一个父类，但是可以实现多个接口

@Override重写父类/接口中的方法

多态：TransportVehicle truck = new Truck("T-998");
TransportVehicle drone = new Drone("D-007");

static方法：给类用

private:只能自己用；protected:自己和子类都可以使用

super必须放在构造方法的第一行

public Truck(String vehicleId) {
super(vehicleId);
System.out.println("卡车创建成功");
}

#### super的使用（引用一下ai的解释）
关键就在这里：super 本身并不是“调用某个父类方法”，要看你后面怎么写。
如果你写的是：
super();
或者：
super("T-998");
这调用的是父类构造方法。
如果你写的是：
super.eat();
这才是调用父类里的 eat() 方法。
如果你写：
super.getLocation();
那就是调用父类里的 getLocation()。
所以，方法名其实已经写出来了，Java当然知道你要调哪个方法。
比如父类有很多方法：
class Animal {

    public void eat() {
        System.out.println("吃东西");
    }

    public void sleep() {
        System.out.println("睡觉");
    }

    public void run() {
        System.out.println("跑步");
    }
}
子类：
class Dog extends Animal {

    public void test() {
        super.eat();
        super.sleep();
        super.run();
    }
}
这里分别就是：
super.eat();    // 调父类 eat()
super.sleep();  // 调父类 sleep()
super.run();    // 调父类 run()
真正容易混淆的是这个：
super(...)
它后面没有点号和方法名，因为它是专门用来调用父类构造方法的。
比如父类有多个构造方法：
class Animal {

    public Animal() {
        System.out.println("无参构造");
    }

    public Animal(String name) {
        System.out.println("一个String参数");
    }

    public Animal(String name, int age) {
        System.out.println("String + int参数");
    }
}
子类里写：
super();
Java就找：
Animal()
如果写：
super("小狗");
Java就找：
Animal(String name)
如果写：
super("小狗", 3);
Java就找：
Animal(String name, int age)
也就是说，Java是根据：
参数的个数 + 参数类型

来判断调用哪个构造方法的。
这叫重载匹配。
你可以这样记：
super(...)       → 调父类构造方法
super.方法名()   → 调父类普通方法
super.变量名     → 访问父类变量
所以你这道题里的：
super(vehicleId);
不是“随便调用父类某个方法”，而是明确表示：
调用父类中那个能接收 String 参数的构造方法。


#### 泛型
泛型：让类型先不确定，使用时再指定。
<T>：声明一个类型占位符 T。
泛型类：class StorageBox<T>，整个类都可以使用 T。
泛型方法：public <T> void test(T x)，只有这个方法使用 T。
指定类型：StorageBox<Product> 表示 T = Product。
作用：提高代码复用性和类型安全，减少强制类型转换。
类型检查：错误类型在编译阶段就能发现。
<> 菱形语法：new StorageBox<>()，Java 会根据前面自动推断类型。
