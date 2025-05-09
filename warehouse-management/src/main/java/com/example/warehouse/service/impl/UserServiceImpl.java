package com.example.warehouse.service.impl;

import com.example.warehouse.entity.User;
import com.example.warehouse.entity.UserRole;
import com.example.warehouse.repository.UserRepository;
import com.example.warehouse.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User createUser(User user) {
        if(userRepository.existsByUsername(user.getUsername())){
            throw new IllegalArgumentException("Username already exists");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(!existingUser.getUsername().equals(user.getUsername()) &&
        userRepository.existsByUsername(user.getUsername())){
            throw new IllegalArgumentException("Username already exists");
        }

        if(!existingUser.getEmail().equals(user.getEmail()) &&
        userRepository.existsByEmail(user.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }

        if(!user.getPassword().equals(user.getPassword())){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);

    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if(!userRepository.existsById(userId)){
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    @Override
    public boolean existsUserByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void resetPassword(String username, String newPassword) {
        User user = getUserByUsername(username);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
