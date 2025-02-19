package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserProfileService userProfileService;

    public User save(User user) {
        return userRepository.save(user);
    }


    public User create(User user) {
        if (userRepository.existsByEmail(user.getUsername())) {
            throw new RuntimeException("Почта уже привязана к другому аккаунту.");
        }
        var saveUser = save(user);
        userProfileService.create(saveUser);
        return saveUser;
    }
    @Transactional
    public User update(UserDto userDto){
        User currentUser = findById(UUID.fromString(userDto.getId()));
        if (!currentUser.getPassword().equals(userDto.getPassword()))
            currentUser.setPassword(userDto.getPassword());

        return userRepository.save(currentUser);
    }

    public UserProfileDto getUserProfile(){
        var user = getCurrentUser();
        return userProfileService.findByUser(user);
    }
    public Optional<User> findByDeviceId(String deviceId){
        return userRepository.findByDeviceId(deviceId);
    }

    public User getByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }
    public User findById(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
    public void deleteUser(User user){
        userRepository.delete(user);
    }
}
