package com.example.demo.service.authentication;

import com.example.demo.controller.domain.request.GuestSignInRequest;
import com.example.demo.controller.domain.request.SignInRequest;
import com.example.demo.controller.domain.request.SignUpRequest;
import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.service.UserService;
import com.example.demo.service.email.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private static final int TOKEN_VALIDITY_HOURS = 1;

    @Transactional
    public MessageResponse signUp(SignUpRequest request) {

        try {
            var checkUser = userService.findByEmail(request.getEmail());
            if(checkUser.isPresent() && checkUser.get().getRole() == UserRole.NOT_CONFIRMED){
                userService.deleteUser(checkUser.get());
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
                public void run() //Этот метод будет выполняться в побочном потоке
                {
                    try {
                        sendVerificationEmail(user);
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
            var checkUser = userService.findByEmail(request.getEmail());
            if(checkUser.isPresent() && checkUser.get().getRole() != UserRole.USER){
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
    public String regenerateToken(User user){
        try {
            generateVerificationToken(user);
            userService.save(user);
            sendVerificationEmail(user);
            return "Письмо с подтверждением регистрации отправлено на почту";
        }catch (Exception e){
            return e.getMessage();
        }
    }

    private void generateVerificationToken(User user) {
        Random r = new Random( System.currentTimeMillis());
        Integer token = 10000 + r.nextInt(20000);
        user.setVerificationToken(token.toString());
        user.setTokenExpiryDate(LocalDateTime.now().plusHours(TOKEN_VALIDITY_HOURS));
    }
    private void sendVerificationEmail(User user) throws MessagingException {
        String htmlContent = buildEmail(user.getVerificationToken()); // Создаем HTML-контент письма
        emailService.sendHtmlEmail(user.getEmail(), "Подтверждение регистрации", htmlContent);
    }
    private String buildEmail(String verificationToken) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    .button {
                        background-color: #4CAF50;
                        border: none;
                        color: white;
                        padding: 15px 32px;
                        text-align: center;
                        text-decoration: none;
                        display: inline-block;
                        font-size: 16px;
                        margin: 4px 2px;
                        cursor: pointer;
                    }
                    .container {
                        font-family: Arial, sans-serif;
                        padding: 20px;
                        text-align: center;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>Подтверждение регистрации в игровом приложении DrawingBattle</h2>
                    <p>Спасибо за регистрацию. Ваш код подтверждения: <b>%s</b></p>
                    <p>Если вы не регистрировались в DrawingBattle, просто закройте данное сообщение.</p>
                </div>
            </body>
            </html>
            """.formatted(verificationToken);
    }
}
