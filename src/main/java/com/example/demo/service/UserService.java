package com.example.demo.service;

import com.example.demo.controller.domain.request.VerifyRequest;
import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.authentication.JwtService;
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
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
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

    public MessageResponse verifyEmail(VerifyRequest request) {
            Optional<User> userOpt = findByEmail(request.getEmail());
            if (userOpt.isPresent()) {
                var user = userOpt.get();
                if (user.getRole() != UserRole.NOT_CONFIRMED){
                    return new MessageResponse("", "Пользователь уже подтвержден");
                }
                if (user.getTokenExpiryDate() != null && user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
                    return new MessageResponse("", "Токен истёк. Пожалуйста, запросите новый.");
                }else if(user.getTokenExpiryDate() != null){
                    if(user.getVerificationToken().equals(request.getToken())){
                        user.setRole(UserRole.USER);
                        user.setVerificationToken(null);
                        user.setTokenExpiryDate(null);
                        user.setDeviceId(null);
                        userRepository.save(user);

                        return new MessageResponse("Почта успешно подтверждена", "");
                    }else{
                        return new MessageResponse("","Токен недействительный.");
                    }
                }else{
                    return new MessageResponse("", "Неизвестная ошибка!");
                }
            }else{
                return new MessageResponse("", "Пользователя с такой почтой не существует.");
            }
    }

}
