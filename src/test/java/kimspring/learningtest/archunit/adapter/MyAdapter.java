package kimspring.learningtest.archunit.adapter;

import kimspring.learningtest.archunit.application.MyService2;

public class MyAdapter {
    MyService2 myService2;

    void run() {
        myService2 = new MyService2();
        System.out.println("myService2 = " + myService2);
    }
}
