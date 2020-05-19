package com.garry.springlifecycle.async.disruptor.pool;

import org.springframework.stereotype.Service;

/**
 * demo 测试注解是否成功
 */
@Service
public class Student {

    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String sayHello(String name){
        System.out.println(name);
        return name;
    }
}
