package kimspring.learningtest.archunit.application

class MyService {
    var myService2: MyService2? = null

    fun run() {
        myService2 = MyService2()
        println("myService2 = $myService2")
    }
}
