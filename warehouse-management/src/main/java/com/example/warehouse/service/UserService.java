package com.example.warehouse.service;

import com.example.warehouse.entity.User;
import com.example.warehouse.entity.UserRole;

import java.util.List;

public interface UserService {
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long userId);
    User getUserById(Long userId);
    User getUserByUsername(String username);
    List<User> getAllUsers();
    List<User> getUsersByRole(UserRole role);
    boolean existsUserByUsername(String username);
    boolean existsUserByEmail(String email);
    void resetPassword(String username, String newPassword);

}
