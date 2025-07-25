package com.example.MoodMeal.service;

import com.example.MoodMeal.exception.InvalidInputException;
import com.example.MoodMeal.exception.ResourceNotFoundException;
import com.example.MoodMeal.model.Role;
import com.example.MoodMeal.model.User;
import com.example.MoodMeal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user){
        if(userRepository.existsByUsername(user.getUsername())){
            throw new InvalidInputException("Error: Username already taken");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new InvalidInputException("Error: email already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(Role.ROLE_USER));
        return userRepository.save(user);
    }

    public User getUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId: "+userId));
    }
    public User getUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }
    public User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: "+ email));
    }
    public User updateUser(Long userId, User updatedUser){
        User existingUser = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("user not found with userId: "+userId));

        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAllergies(updatedUser.getAllergies());
        existingUser.setDietaryPreferences(updatedUser.getDietaryPreferences());
        return userRepository.save(existingUser);
    }
    public void changePassword(Long userId,String newPassword){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId: "+ userId));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

}
