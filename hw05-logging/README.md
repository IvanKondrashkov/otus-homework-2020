#Домашнее задание. Автоматическое логирование.
Цель: Понять как реализуется AOP, какие для этого есть технические средства.
Разработайте такой функционал:

1) Метод класса можно пометить самодельной аннотацией @Log, например, так:

    class TestLogging {
    
    @Log    
    public void calculation(int param) {};
    }

2) При вызове этого метода "автомагически" в консоль должны логироваться значения параметров.
Например так.

    class Demo {
    
    public void action() {
    
    new TestLogging().calculation(6);
    }
    }

3) В консоле дожно быть:
executed method: calculation, param: 6

Обратите внимание: явного вызова логирования быть не должно. 