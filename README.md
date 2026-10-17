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

# 4.5 知识点总结

- **单例模式 Singleton**  
  保证一个类在程序中只有一个实例，通常通过私有构造方法和 `getInstance()` 实现。

- **简单工厂模式 Simple Factory**  
  把对象的创建交给工厂类完成，让使用者不用直接 `new` 对象。

- **建造者模式 Builder**  
  适合创建参数较多、部分参数可选的复杂对象，可以一步步完成对象组装。

- **`private` 构造方法**  
  构造方法私有化后，类的外部不能直接使用 `new` 创建对象。

- **`static` 静态成员**  
  `static` 修饰的成员属于类本身，而不是某一个具体对象，可以通过类名直接访问。

- **`getInstance()` 方法**  
  单例模式中用于向外部返回类内部唯一实例的方法。

- **`==` 比较对象**  
  对引用类型使用 `==`，可以判断两个引用是否指向同一个对象。

- **父类引用指向子类对象**  
  例如 `TransportVehicle vehicle = new Drone();`，这是多态的一种表现。

- **多态**  
  使用父类或接口类型接收不同的子类对象，可以让代码更加灵活。

- **工厂类**  
  专门负责对象的创建，根据传入的参数决定创建哪一种具体对象。

- **静态内部类 `static class`**  
  定义在另一个类内部的静态类，不依赖外部类对象，可以直接通过外部类名使用。

- **`this`**  
  表示当前对象，例如 `this.sender = sender` 中左边表示当前对象的属性。

- **`return this`**  
  返回当前对象本身，常用于实现链式调用。

- **链式调用**  
  可以连续调用多个方法，例如 `.sender().receiver().fragile()`，使代码更简洁。

- **`build()` 方法**  
  Builder 中用于根据已经设置好的数据创建并返回最终对象。

- **必填参数与选填参数**  
  必填参数通常放在构造方法中，选填参数通过 Builder 的方法单独设置。

- **`toString()` 重写**  
  用于自定义对象打印时显示的内容，避免输出类似 `LogisticsOrder@xxxx` 的默认信息。

### 异常
Throwable
|
|---Error
|
|
|---Exception
       |
       |---RuntimeException
       |
       |---其他Exception

super（message)调用的是父类的构造方法Exception（String message)
- throws InsufficientFundsException：声明方法可能抛出这个受检异常。
- throw new InsufficientFundsException(...)：条件成立时，真正创建并抛出异常对象。
throw 是动作，throws 是声明