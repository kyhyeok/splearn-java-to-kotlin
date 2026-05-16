package kimspring.learningtest.archunit.adapter

import kimspring.learningtest.archunit.application.MyService2

class MyAdapter {
    var myService2: MyService2? = null

    fun run() {
        myService2 = MyService2()
        println("myService2 = $myService2")
    }
}
