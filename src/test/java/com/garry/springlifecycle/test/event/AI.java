package com.garry.springlifecycle.test.event;


import com.garry.springlifecycle.annotation.model.Send;

public interface AI {

//    @Send("maTest")
    TestEvent ma();

    String sayHello(String name);
}
