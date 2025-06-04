package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.entity.UserProfile;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.repository.*;
import com.example.demo.service.MemberMessageService;
import com.example.demo.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = DemoApplication.class)
public class UserProfileTests {
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserPictureRepository userPictureRepository;
    @Autowired
    private GameRoomRepository gameRoomRepository;
    @Autowired
    private UserGameRoomRepository userGameRoomRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    @Autowired
    private MemberMessageRepository memberMessageRepository;

    @Value("${gameConstants.userPicture.deltaStorageSize}")
    private int DELTA_STORAGE_SIZE;
    @Value("${gameConstants.userPicture.deltaStoragePrice}")
    private int DELTA_STORAGE_PRICE;

    private void cleanDB(){
        userGameRoomRepository.deleteAll();
        gameRoomRepository.deleteAll();
        memberMessageRepository.deleteAll();
        groupMemberRepository.deleteAll();
        groupRepository.deleteAll();
        userPictureRepository.deleteAll();
        userProfileRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    public void changeNicknameTest(){
        cleanDB();

        var user = User.builder()
                .email("user")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();
        var userResult = userRepository.save(user);

        var userProfile = UserProfile.builder()
                .user(userResult)
                .pictureMaxCount(9)
                .currency(1000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var resultNickname = "Result_Nickname";
        var result = userProfileService.changeNickname(resultNickname, userResult);
        assertNotNull(result.getContent());
        assertEquals(resultNickname, userProfileRepository.findById(userProfile.getId()).get().getNickname());
    }

    @Test
    public void positiveBuyPictureSizeTest(){
        cleanDB();

        var user = User.builder()
                .email("user")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();
        var userResult = userRepository.save(user);

        var userProfile = UserProfile.builder()
                .user(userResult)
                .pictureMaxCount(9)
                .currency(1000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var result = userProfileService.buyPictureStorageSize(userResult, 1);
        var newUserProfileResult = userProfileRepository.findById(userProfileResult.getId());
        assertNotNull(result.getContent());
        assertEquals(userProfileResult.getCurrency() - DELTA_STORAGE_PRICE, newUserProfileResult.get().getCurrency());
        assertEquals(userProfileResult.getPictureMaxCount() + DELTA_STORAGE_SIZE, newUserProfileResult.get().getPictureMaxCount());
    }
    @Test
    public void negativeBuyPictureSizeTest(){
        cleanDB();

        var user = User.builder()
                .email("user")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();
        var userResult = userRepository.save(user);

        var userProfile = UserProfile.builder()
                .user(userResult)
                .pictureMaxCount(9)
                .currency(100)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var result = userProfileService.buyPictureStorageSize(userResult, 1);
        var newUserProfileResult = userProfileRepository.findById(userProfileResult.getId());
        assertNotNull(result.getError());
        assertEquals(userProfileResult.getCurrency(), newUserProfileResult.get().getCurrency());
        assertEquals(userProfileResult.getPictureMaxCount(), newUserProfileResult.get().getPictureMaxCount());
    }
}
