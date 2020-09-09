package com.example.aop.aopexample.service.impl;

import com.example.aop.aopexample.configuration.aop.common.Auditable;
import com.example.aop.aopexample.entity.User;
import com.example.aop.aopexample.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Auditable
    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        users.add(User.builder().name("Phuc").age("23").build());
        return users;
    }

    @Override
    public String testAop() {
        return "Test AOP";
    }
}
