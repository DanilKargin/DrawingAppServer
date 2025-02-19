package com.example.demo.service.authentication;

import com.example.demo.controller.domain.request.*;
import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.service.UserService;
import com.example.demo.service.email.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final ResourceLoader resourceLoader;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private static final int TOKEN_VALIDITY_MINUTES = 15;

    @Transactional
    public MessageResponse signUp(SignUpRequest request) {

        try {
            var checkUser = userService.getByUsername(request.getEmail());
            if(checkUser.getRole() == UserRole.NOT_CONFIRMED){
                userService.deleteUser(checkUser);
            }
            var user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(UserRole.NOT_CONFIRMED)
                    .build();
            generateVerificationToken(user);
            userService.create(user);

            Thread emailSendThread = new Thread(new Runnable()
            {
                public void run()
                {
                    try {
                        sendVerificationEmail(user, "Подтверждение регистрации");
                    } catch (MessagingException e) {

                    }
                }
            });
            emailSendThread.start();
            return new MessageResponse("Письмо с подтверждением регистрации отправлена на почту", "");
        }catch (Exception e){
            return new MessageResponse("", e.getMessage());
        }
    }

    public MessageResponse guestSignIn(GuestSignInRequest request){
        var user = userService.findByDeviceId(request.getDeviceId());
        if(user.isPresent()){
            if(user.get().getRole() == UserRole.GUEST){
                var jwt = jwtService.generateToken(user.get());
                return new MessageResponse(jwt, "");
            }else{
                return new MessageResponse("", "В доступе отказано!");
            }
        }else{
            var guest = User.builder()
                    .role(UserRole.GUEST)
                    .email("Guest-" + UUID.randomUUID())
                    .deviceId(request.getDeviceId())
                    .build();

            var createUser = userService.create(guest);
            if(createUser != null) {
                var jwt = jwtService.generateToken(createUser);
                return new MessageResponse(jwt, "");
            }else {
                return new MessageResponse("", "Неизвестная ошибка!");
            }
        }
    }

    public MessageResponse signIn(SignInRequest request) {
        try {
            var checkUser = userService.getByUsername(request.getEmail());
            if(checkUser.getRole() != UserRole.USER){
                throw new UsernameNotFoundException("Пользователь с такой почтой не найден");
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            ));

            var user = userService.userDetailsService()
                    .loadUserByUsername(request.getEmail());

            var jwt = jwtService.generateToken(user);
            return new MessageResponse(jwt, "");
        } catch (Exception e) {
            return new MessageResponse("", e.getMessage());
        }
    }
    public MessageResponse verifyEmail(VerifyRequest request) {
        try {
            var user = userService.getByUsername(request.getEmail());
            checkUserSecurityToken(request.getToken(), user);
            user.setRole(UserRole.USER);
            user.setVerificationToken(null);
            user.setTokenExpiryDate(null);
            userService.save(user);

            return new MessageResponse("Почта успешно подтверждена", "");
        }catch(Exception e){
            return new MessageResponse("", e.getMessage());
        }
    }
    public MessageResponse sendSecurityToken(VerifyRequest request){
        try {
            User user = userService.getByUsername(request.getEmail());
            generateVerificationToken(user);
            userService.save(user);
            Thread emailSendThread = new Thread(new Runnable()
            {
                public void run()
                {
                    try {
                        sendVerificationEmail(user, "Код безопасности");
                    } catch (MessagingException e) {

                    }
                }
            });
            emailSendThread.start();
            return new MessageResponse("Письмо с кодом безопасности отправлено на почту","");
        }catch (Exception e){
            return new MessageResponse("", e.getMessage());
        }
    }
    public MessageResponse checkToken(String token, String email){
        try{
            User user = userService.getByUsername(email);
            return checkUserSecurityToken(token, user);
        }catch (Exception e){
            return new MessageResponse("", e.getMessage());
        }
    }
    public MessageResponse changePassword(ChangePasswordRequest request){
        try {
            User user = userService.getByUsername(request.getEmail());
            checkUserSecurityToken(request.getToken(), user);
            if(!request.getPassword().isEmpty()){
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setTokenExpiryDate(null);
                user.setVerificationToken(null);
                userService.save(user);
                return new MessageResponse("Пароль успешно изменён", "");
            }else{
                return new MessageResponse("", "Пароль не может быть установлен");
            }
        }catch (Exception e){
            return new MessageResponse("", e.getMessage());
        }
    }
    private MessageResponse checkUserSecurityToken(String token, User user){
        if (user.getTokenExpiryDate() != null && user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Токен истёк. Пожалуйста, запросите новый.");
        } else if (user.getTokenExpiryDate() != null) {
            if (user.getVerificationToken().equals(token)) {
                return new MessageResponse(token, "");
            } else {
                throw new RuntimeException("Токен недействительный.");
            }
        } else {
            throw new RuntimeException("Неизвестная ошибка!");
        }
    }
    private void generateVerificationToken(User user) {
        Random r = new Random( System.currentTimeMillis());
        Integer token = 10000 + r.nextInt(20000);
        user.setVerificationToken(token.toString());
        user.setTokenExpiryDate(LocalDateTime.now().plusMinutes(TOKEN_VALIDITY_MINUTES));
    }
    private void sendVerificationEmail(User user, String title) throws MessagingException {
        String htmlContent = buildEmail(user.getVerificationToken());
        emailService.sendHtmlEmail(user.getEmail(), title, htmlContent);
    }
    private String buildEmail(String verificationToken) {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/email-token-page.html");
            String htmlContent = Files.readString(Path.of(resource.getURI()), StandardCharsets.UTF_8);
            return htmlContent.replace("{{TOKEN}}", verificationToken);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке email-шаблона", e);
        }
    }
}
