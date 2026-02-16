package com.springboot.usercrud.service;

import com.springboot.usercrud.model.User;
import com.springboot.usercrud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;


    public User save(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setActive(true); // New users are active by default

        User savedUser = userRepository.save(user);
        try {
            emailService.sendUserCreatedNotification(savedUser);
        } catch (Exception e) {
            System.err.println("Failed to send user creation email: " + e.getMessage());
        }
        return savedUser;
    }

    public User findById(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public void deleteById(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User userToDelete = optionalUser.get();
            userRepository.deleteById(id);
            System.out.println("User permanently deleted: " + userToDelete.getFirstName() + " " + userToDelete.getLastName() + " (ID: " + id + ")");

            try {
                emailService.sendUserDeletedNotification(userToDelete, "Employee permanently removed from system");
            } catch (Exception e) {
                System.err.println("Failed to send user deletion email: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }

    public User updateUser(long id, User user) {

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();

            // Track changes for email notification
            StringBuilder changedFields = new StringBuilder();

            if (!currentUser.getFirstName().equals(user.getFirstName())) {
                changedFields.append("First Name changed from '").append(currentUser.getFirstName())
                        .append("' to '").append(user.getFirstName()).append("'<br>");
            }
            if (!currentUser.getLastName().equals(user.getLastName())) {
                changedFields.append("Last Name changed from '").append(currentUser.getLastName())
                        .append("' to '").append(user.getLastName()).append("'<br>");
            }
            if (!currentUser.getEmail().equals(user.getEmail())) {
                changedFields.append("Email changed from '").append(currentUser.getEmail())
                        .append("' to '").append(user.getEmail()).append("'<br>");
            }
            if (currentUser.getPhoneNumber() != null && user.getPhoneNumber() != null &&
                    !currentUser.getPhoneNumber().equals(user.getPhoneNumber())) {
                changedFields.append("Phone Number changed from '").append(currentUser.getPhoneNumber())
                        .append("' to '").append(user.getPhoneNumber()).append("'<br>");
            }
            if (!currentUser.getDepartment().equals(user.getDepartment())) {
                changedFields.append("Department changed from '").append(currentUser.getDepartment())
                        .append("' to '").append(user.getDepartment()).append("'<br>");
            }
            if (!currentUser.getRole().equals(user.getRole())) {
                changedFields.append("Role changed from '").append(currentUser.getRole())
                        .append("' to '").append(user.getRole()).append("'<br>");
            }
            if (currentUser.isActive() != user.isActive()) {
                changedFields.append("Status changed from '").append(currentUser.isActive() ? "Active" : "Inactive")
                        .append("' to '").append(user.isActive() ? "Active" : "Inactive").append("'<br>");
            }

            currentUser.setFirstName(user.getFirstName());
            currentUser.setLastName(user.getLastName());
            currentUser.setEmail(user.getEmail());
            currentUser.setPhoneNumber(user.getPhoneNumber());
            currentUser.setDepartment(user.getDepartment());
            currentUser.setRole(user.getRole());
            currentUser.setActive(user.isActive());
            currentUser.setUpdatedAt(LocalDateTime.now());

            User updatedUser = userRepository.save(currentUser);

            // Send email notification if there were changes
            if (!changedFields.isEmpty()) {
                try {
                    emailService.sendUserUpdatedNotification(updatedUser, changedFields.toString());
                } catch (Exception e) {
                    System.err.println("Failed to send user update email: " + e.getMessage());
                }
            }

            return updatedUser;
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }
}
