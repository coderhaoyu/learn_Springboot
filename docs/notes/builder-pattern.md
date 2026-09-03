# Builder 链：Java 如何补上具名参数

> 起因：读《Spring 容器与 Bean》「第三方的类 → 写一个 `@Bean` 方法」一节时碰到「builder 链」这个说法，不清楚它是什么、为什么 Java 里满地都是而前端几乎见不到
> 相关笔记：[Spring 容器与 Bean](./spring-container-and-bean.md)
> 本篇示例全部是最小构造，不引用项目代码；关键处附 JS 对照

## 这份笔记回答什么

- builder 链是什么，为什么能一路点下去
- 它到底解决 Java 的什么问题
- 为什么 JS 里几乎见不到这个模式
- 为什么必须有一个 `build()` 收尾
- 什么时候不该用它

---

## 一、是什么

把「创建一个对象」拆成一串连续的方法调用：先拿到一个临时的配置收集器（builder），一次点一个参数，最后用一个收尾方法产出成品。

形状固定：

```java
成品 x = 起点()
        .参数A(值)
        .参数B(值)
        .build();      // 收尾
```

能一路点下去的唯一原因是 **每个参数方法都 `return this`** —— 把自己所在的对象再返回出去，于是下一个点号还有东西可点。没有别的机制。

这个套路在 JS 里早就用过：

```js
$('#box').addClass('on').fadeIn()      // jQuery，每个方法 return this
promise.then(f).then(g).catch(h)       // Promise
```

都是「方法返回一个还能继续点的东西」。

---

## 二、它解决什么问题

Java 构造器的参数一多就没法用。假设一个咖啡对象，一个必填（杯型）、三个可选（糖、奶、冰）：

```java
Coffee c = new Coffee("大杯", 2, true, false);
```

### 2.1 读不出参数含义

`2` 是糖还是冰块数？`true` 是加奶还是要冰？必须跳到构造器定义去对照。

### 2.2 同类型参数写反，编译器不报错

`true, false` 顺序颠倒 → 加了冰没加奶，编译通过，运行时才发现。这是三个毛病里最危险的一条。

### 2.3 想只传一部分，重载会撞墙

不同的参数组合只能靠重载来支持：

```java
public Coffee(String size) { ... }
public Coffee(String size, int sugar) { ... }
public Coffee(String size, int sugar, boolean milk) { ... }
```

现在想「只要大杯 + 加冰，不管糖和奶」，加一个 `Coffee(String, boolean)` 可以。接着想「只要大杯 + 加奶」呢？—— 签名也是 `Coffee(String, boolean)`，**完全一样，加不进去**。

这是重载的硬墙：**同类型的可选参数无法用重载区分**，写多少个重载都解决不了。builder 不可替代的根本原因就在这一条。

---

## 三、JS 里为什么没这个问题

同样的需求，JS 直接传个对象就完了：

```js
new Coffee({ size: '大杯', iced: true })    // 名字自带，不写的项走默认值
```

所以结论是：**Java 的 builder 本质上就是在手工造出 JS 的 options 对象。**

JS 有对象字面量，随手写 `{ size: '大杯' }` 就得到一个「装着若干具名参数的东西」。Java 是静态类型的，没有这个语法 —— 想传一个装着 size 和 iced 的东西，必须先定义一个类。**那个类就是 Builder。**

Java 多写一个类，换到的是编译期检查：

```js
new Coffee({ sugur: 2 })     // JS：key 拼错，静默失效，咖啡里没糖
```

```java
.sugur(2)                    // Java：方法名拼错，编译不过
```

这是个权衡，不是谁更好：JS 省代码，Java 换安全。

---

## 四、最小完整实现

Java：

```java
public class Coffee {

    private final String size;
    private final int sugar;
    private final boolean milk;

    // 私有构造器：外面唯一的入口是 builder()
    private Coffee(Builder b) {
        this.size = b.size;
        this.sugar = b.sugar;
        this.milk = b.milk;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return size + "，糖" + sugar + "勺，加奶=" + milk;
    }

    public static class Builder {

        // 默认值写在这里，不点就用这个
        private String size = "中杯";
        private int sugar = 0;
        private boolean milk = false;

        public Builder size(String v)  { this.size = v;  return this; }
        public Builder sugar(int v)    { this.sugar = v; return this; }
        public Builder milk(boolean v) { this.milk = v;  return this; }

        public Coffee build() {
            return new Coffee(this);
        }
    }
}
```

调用：

```java
Coffee c = Coffee.builder().size("大杯").milk(true).build();
// 大杯，糖0勺，加奶=true
```

JS 里完全等价的东西：

```js
class Coffee {
  constructor({ size = '中杯', sugar = 0, milk = false } = {}) {
    Object.assign(this, { size, sugar, milk })
  }
}

const c = new Coffee({ size: '大杯', milk: true })
```

JS 版本没有 Builder 类，因为 `{ ... }` 已经承担了它的全部职责。

### 逐项对照

| 要做的事 | Java builder | JS |
|---|---|---|
| 表达参数名 | 方法名 `.size("大杯")` | key `size: '大杯'` |
| 设默认值 | Builder 的字段初始值 | 解构默认值 `= '中杯'` |
| 只传一部分 | 只点想要的方法 | 只写想要的 key |
| 收尾 | 必须 `.build()` | 不需要 |
| 名字写错 | 编译报错 | 静默失效 |
| 额外代码量 | 一个 Builder 类 | 零 |

---

## 五、链式调用没有魔法

把点号拆开，完全等价：

```java
Coffee.Builder b = Coffee.builder();
b.size("大杯");
b.milk(true);
Coffee c = b.build();
```

功能一模一样，只是每行都要重复写一遍 `b`。**`return this` 省掉的就是这个重复的变量名。** 链式写法是语法上的便利，不是新能力。

反过来，JS 也完全可以写 builder，机制一字不差：

```js
class CoffeeBuilder {
  size(v) { this._size = v; return this }   // 同样是 return this
  milk(v) { this._milk = v; return this }
  build() { return new Coffee(this) }
}
```

只是没人这么写 —— 有 options 对象就够了。这解释了为什么 builder 在 Java 里满地都是、在前端却很少见：**它补的是 Java 缺少具名参数和默认参数这个坑，而 JS 没这个坑。**

---

## 六、为什么必须有 build()

JS 不需要收尾，因为对象字面量本身就是个完整的值。Java 需要，因为这里存在两个不同的类型：

- `Coffee.Builder` —— 还在攒参数的**草稿**，字段可变、可能不完整
- `Coffee` —— 参数攒齐的**成品**

`build()` 是从前者到后者的那一步，做两件事：**校验**（必填项有没有漏、字段之间有没有冲突）和**拷贝**（把值搬进成品）。

由此带来一个实际好处：成品的字段可以全部声明成 `final`。所有可变性都关在 Builder 里，`build()` 交出去的对象谁都改不了。这正好对上《Spring 容器与 Bean》第五章那条「单例 Bean 不能有可变实例字段」—— 如果不走 builder，而是直接在成品上挂一串 setter，字段就不能是 `final`，那条规则也就守不住了。

另一个后果：**builder 用完就别留着。** 它内部字段是可变的，存下来改一改再 build，很容易踩到共享状态的坑。当场建、当场 `build()`。

---

## 七、前端概念对照

| Java | 前端近似物 |
|---|---|
| Builder 类 | options 对象字面量 `{ ... }` |
| 参数方法 `.size(v)` | 对象的 key |
| Builder 字段初始值 | 解构默认值 |
| `return this` | jQuery / Promise 的链式返回 |
| `build()` | 无对应物，JS 的字面量本来就是成品 |
| 成品字段 `final` | `Object.freeze()` |

类比只用于入门，不能画等号。

---

## 八、什么时候用，什么时候不用

用：

- 可选参数三个以上
- 或者同类型的可选参数不止一个（2.3 那道硬墙）

不用：

- 参数少且全部必填 → 直接写构造器，builder 是三倍代码量换不来任何东西
- 只是装几个数据、没有可选项 → Java 21 直接用 record：`record Point(int x, int y) {}`

判据就一句：**参数是不是「多且大部分可选」。**

---

## 九、需要记住的五条

1. builder 链的全部机制就是每个方法 `return this`，没有魔法
2. 它补的是 Java 缺少具名参数和默认参数这个坑，JS 用 options 对象解决同一个问题
3. 重载无法区分同类型的可选参数，这是必须用 builder 的硬理由
4. `build()` 是草稿变成品的分界点，负责校验和拷贝，让成品字段能是 `final`
5. builder 是一次性的，当场建当场 build，不要存下来复用

---

## 十、暂时不用看的

- **必填项的两种流派**：放进 `builder(必填项)` 的参数里（漏传直接编译不过），还是全走链式方法、在 `build()` 里查 null（运行时才报错）
- **集合字段的防御性拷贝**：成品持有 List / Set 时，`build()` 里要写 `List.copyOf(...)`，否则成品和 builder 共享同一个集合，build 完再改 builder 会影响到已建好的对象
- **Lombok 的 `@Builder`**：能自动生成这一整套，但不做必填校验，默认值要写 `@Builder.Default` 才生效
- **自限定泛型**（`class X extends Base<X>`）：builder 遇上继承时 `return this` 会坏掉，框架里那些看着绕回去的泛型签名就是为了修这个
- **lambda DSL**（`.csrf(c -> c.disable())`）：配置本身有层级时，用函数参数代替平铺方法，Spring Security 大量使用

前两条实际动手写 builder 时会用到，后三条读框架源码撞上再查。

同一条判断标准：机制和为什么值得提前理解，API 和方法名边用边查 —— 机制没搞懂会写出「能跑但是错的」代码，API 记错了编译器立刻报错。
