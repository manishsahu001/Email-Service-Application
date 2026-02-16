package com.springboot.usercrud.controller;

import com.springboot.usercrud.model.User;
import com.springboot.usercrud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User createUser(@RequestBody User user){
    return userService.save(user);
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable long id){
        return userService.findById(id);
    }

    @GetMapping
    public List<User> findAll(){
        return userService.findAll();
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable long id){
        System.out.println("DELETE request received for user ID: " + id);
        userService.deleteById(id);
        return "User with ID " + id + " has been permanently deleted";
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable long id, @RequestBody User user){
        return userService.updateUser(id, user);
    }
}
