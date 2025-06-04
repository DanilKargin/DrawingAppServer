package com.example.demo;

import com.example.demo.controller.domain.request.user.UserPictureRequest;
import com.example.demo.dto.UserPictureDto;
import com.example.demo.entity.User;
import com.example.demo.entity.UserPicture;
import com.example.demo.entity.UserProfile;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.repository.*;
import com.example.demo.service.UserPictureService;
import com.example.demo.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DemoApplication.class)
public class UserPictureTests {
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserPictureService userPictureService;
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
    public void createPicturePositiveTest(){
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

        UserPictureRequest request = new UserPictureRequest();
        request.setImage(new byte[] {0});

        var result = userPictureService.createPicture(userResult, request);
        assertNotNull(result.getContent());
        assertEquals(1, userPictureRepository.findAll().size());
    }
    @Test
    public void createPictureNegativeTest(){
        cleanDB();

        var user = User.builder()
                .email("user")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();
        var userResult = userRepository.save(user);

        var userProfile = UserProfile.builder()
                .user(userResult)
                .pictureMaxCount(0)
                .currency(1000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        UserPictureRequest request = new UserPictureRequest();
        request.setImage(new byte[] {0});

        var result = userPictureService.createPicture(userResult, request);
        assertNotNull(result.getError());
        assertEquals(0, userPictureRepository.findAll().size());
    }

    @Test
    public void deletePictureTest(){
        cleanDB();

        var user = User.builder()
                .email("user")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();
        var userResult = userRepository.save(user);

        var userProfile = UserProfile.builder()
                .user(userResult)
                .pictureMaxCount(0)
                .currency(1000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var userPicture = UserPicture.builder()
                .image(new byte[]{0})
                .createDate(LocalDateTime.now())
                .userProfile(userProfileResult)
                .build();
        var userPictureResult = userPictureRepository.save(userPicture);

        UserPictureRequest request = new UserPictureRequest();
        request.setId(userPictureResult.getId().toString());

        var result = userPictureService.deletePicture(user, request);
        assertNotNull(result.getContent());
        assertEquals(0, userPictureRepository.findAll().size());
    }

    @Test
    public void setLikeOnPicturePositiveTest(){
        cleanDB();
        var user = User.builder()
                .email("user")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();
        var userResult = userRepository.save(user);

        var userProfile = UserProfile.builder()
                .user(userResult)
                .pictureMaxCount(0)
                .currency(1000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var userPicture = UserPicture.builder()
                .image(new byte[]{0})
                .likes(0)
                .createDate(LocalDateTime.now())
                .userProfile(userProfileResult)
                .build();
        var userPictureResult = userPictureRepository.save(userPicture);

        var testUser = User.builder()
                .email("user2")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();
        var testUserResult = userRepository.save(testUser);

        var testUserProfile = UserProfile.builder()
                .user(testUserResult)
                .pictureMaxCount(0)
                .currency(1000)
                .nickname("Test")
                .build();
        var testUserProfileResult = userProfileRepository.save(testUserProfile);

        var result = userPictureService.likePicture(testUserResult, userPictureResult.getId().toString());
        assertEquals(true, result);
        assertEquals(1, userPictureRepository.findById(userPictureResult.getId()).get().getLikes());
    }
    @Test
    public void setLikeOnPictureNegativeTest(){
        cleanDB();
        var user = User.builder()
                .email("user")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();
        var userResult = userRepository.save(user);

        var userProfile = UserProfile.builder()
                .user(userResult)
                .pictureMaxCount(0)
                .currency(1000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var userPicture = UserPicture.builder()
                .image(new byte[]{0})
                .likes(0)
                .createDate(LocalDateTime.now())
                .userProfile(userProfileResult)
                .build();
        var userPictureResult = userPictureRepository.save(userPicture);

        var result = userPictureService.likePicture(userResult, userPictureResult.getId().toString());
        assertEquals(false, result);
        assertEquals(0, userPictureRepository.findById(userPictureResult.getId()).get().getLikes());
    }

    @Test
    public void getPictureRecordList(){
        cleanDB();
        var user = User.builder()
                .email("user")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();
        var userResult = userRepository.save(user);

        var userProfile = UserProfile.builder()
                .user(userResult)
                .pictureMaxCount(0)
                .currency(1000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        for(int i = 0; i < 5; i++){
            var userPicture = UserPicture.builder()
                    .image(new byte[]{0})
                    .likes(i)
                    .createDate(LocalDateTime.now())
                    .userProfile(userProfileResult)
                    .build();
            userPictureRepository.save(userPicture);
        }
        var userPicture = UserPicture.builder()
                .image(new byte[]{0})
                .likes(0)
                .createDate(LocalDateTime.now().minusDays(20))
                .userProfile(userProfileResult)
                .build();
        var userPictureResult = userPictureRepository.save(userPicture);

        var resultList = userPictureService.getUserPicturesOrderByLike();

        assertEquals(5, resultList.size());
    }
}
