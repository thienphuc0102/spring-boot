package com.example.aop.aopexample.service;

import com.example.aop.aopexample.entity.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();
    String testAop();
}
