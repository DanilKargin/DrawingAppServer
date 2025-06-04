package com.example.demo;

import com.example.demo.controller.domain.request.gameRoom.GameRoomRequest;
import com.example.demo.entity.*;
import com.example.demo.entity.enums.GameRoomStatus;
import com.example.demo.entity.enums.GameUserStatus;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.repository.*;
import com.example.demo.service.GameRoomService;
import com.example.demo.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DemoApplication.class)
public class GameRoomTests {
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
    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private GameRoomService gameRoomService;
    @Value("${gameConstants.gameRoom.winReward}")
    private int WIN_REWARD;
    @Value("${gameConstants.gameRoom.hintPrice}")
    private int HINT_PRICE;

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
    public void getReadyGameRoomListTest(){
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

        for(int i = 0; i< 2; i++){
            var gameRoom = GameRoom.builder()
                    .status(GameRoomStatus.FREE)
                    .changeDate(LocalDateTime.now())
                    .health(3)
                    .build();

            var roomResult = gameRoomRepository.save(gameRoom);

            var userGameRoom = UserGameRoom.builder()
                    .status(GameUserStatus.DRAWING)
                    .gameRoom(roomResult)
                    .userProfile(userProfileResult)
                    .build();
            userGameRoomRepository.save(userGameRoom);
            var opponentGameRoom = UserGameRoom.builder()
                    .status(GameUserStatus.WAITING)
                    .gameRoom(roomResult)
                    .build();
            userGameRoomRepository.save(opponentGameRoom);
        }
        var gameRoom = GameRoom.builder()
                .status(GameRoomStatus.FREE)
                .changeDate(LocalDateTime.now())
                .health(3)
                .build();

        var roomResult = gameRoomRepository.save(gameRoom);

        var userGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.WAITING)
                .gameRoom(roomResult)
                .userProfile(userProfileResult)
                .build();
        userGameRoomRepository.save(userGameRoom);
        var opponentGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.DRAWING)
                .gameRoom(roomResult)
                .build();
        userGameRoomRepository.save(opponentGameRoom);

        var resultList = gameRoomService.getReadyRoomList(userResult);

        assertEquals(2, resultList.size());
    }
    @Test
    public void getWaitingGameRoomListTest(){
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

        for(int i = 0; i< 2; i++){
            var gameRoom = GameRoom.builder()
                    .status(GameRoomStatus.FREE)
                    .changeDate(LocalDateTime.now())
                    .health(3)
                    .build();

            var roomResult = gameRoomRepository.save(gameRoom);

            var userGameRoom = UserGameRoom.builder()
                    .status(GameUserStatus.WAITING)
                    .gameRoom(roomResult)
                    .userProfile(userProfileResult)
                    .build();
            userGameRoomRepository.save(userGameRoom);
            var opponentGameRoom = UserGameRoom.builder()
                    .status(GameUserStatus.DRAWING)
                    .gameRoom(roomResult)
                    .build();
            userGameRoomRepository.save(opponentGameRoom);
        }
        var gameRoom = GameRoom.builder()
                .status(GameRoomStatus.FREE)
                .changeDate(LocalDateTime.now())
                .health(3)
                .build();

        var roomResult = gameRoomRepository.save(gameRoom);

        var userGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.DRAWING)
                .gameRoom(roomResult)
                .userProfile(userProfileResult)
                .build();
        userGameRoomRepository.save(userGameRoom);
        var opponentGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.WAITING)
                .gameRoom(roomResult)
                .build();
        userGameRoomRepository.save(opponentGameRoom);

        var resultList = gameRoomService.getWaitingRoomList(userResult);

        assertEquals(2, resultList.size());
    }

    @Test
    public void findFreeGameRoomTest(){
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

        var result = gameRoomService.findGameRoom(userResult);
        var userGameRoomList = userGameRoomRepository.findAll();
        assertEquals(1, gameRoomRepository.findAll().size());
        assertEquals(2, userGameRoomList.size());
        assertTrue(userGameRoomList.stream().anyMatch(item -> item.getUserProfile() != null && userProfileResult.getId().equals(item.getUserProfile().getId())));
    }

    @Test
    public void findBusyGameRoomTest(){
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

        var opponent = User.builder()
                .email("opponent")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();
        var opponentResult = userRepository.save(opponent);

        var opponentProfile = UserProfile.builder()
                .user(opponentResult)
                .pictureMaxCount(9)
                .currency(1000)
                .nickname("Test")
                .build();
        var opponentProfileResult = userProfileRepository.save(opponentProfile);

        var gameRoom = GameRoom.builder()
                .status(GameRoomStatus.FREE)
                .changeDate(LocalDateTime.now())
                .word(wordRepository.findByTerm("Test").get())
                .health(3)
                .build();

        var roomResult = gameRoomRepository.save(gameRoom);

        var userGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.WAITING)
                .gameRoom(roomResult)
                .userProfile(opponentProfileResult)
                .build();
        userGameRoomRepository.save(userGameRoom);

        var opponentGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.GUESSING)
                .gameRoom(roomResult)
                .build();
        var opponentGameRoomResult = userGameRoomRepository.save(opponentGameRoom);

        var result = gameRoomService.findGameRoom(userResult);
        assertEquals(result.getId(), roomResult.getId().toString());
        assertEquals(userProfileResult.getId(), userGameRoomRepository.findById(opponentGameRoomResult.getId()).get().getUserProfile().getId());
    }

    @Test
    public void setWordInRoomPositiveTest(){
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

        var gameRoom = GameRoom.builder()
                .status(GameRoomStatus.FREE)
                .changeDate(LocalDateTime.now())
                .health(3)
                .build();

        var roomResult = gameRoomRepository.save(gameRoom);

        var userGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.DRAWING)
                .gameRoom(roomResult)
                .userProfile(userProfileResult)
                .build();
        userGameRoomRepository.save(userGameRoom);

        var opponentGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.WAITING)
                .gameRoom(roomResult)
                .build();
        userGameRoomRepository.save(opponentGameRoom);

        Word word = wordRepository.findByTerm("Test").get();
        GameRoomRequest request = new GameRoomRequest();
        request.setRoomId(roomResult.getId().toString());
        request.setWordId(word.getId().toString());

        var result = gameRoomService.setWordInRoom(userResult, request);
        assertEquals(word.getId(), gameRoomRepository.findById(roomResult.getId()).get().getWord().getId());
    }
    @Test
    public void setWordInRoomNegativeTest(){
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

        var gameRoom = GameRoom.builder()
                .status(GameRoomStatus.FREE)
                .changeDate(LocalDateTime.now())
                .health(3)
                .build();

        var roomResult = gameRoomRepository.save(gameRoom);

        var userGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.WAITING)
                .gameRoom(roomResult)
                .userProfile(userProfileResult)
                .build();
        userGameRoomRepository.save(userGameRoom);

        var opponentGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.DRAWING)
                .gameRoom(roomResult)
                .build();
        userGameRoomRepository.save(opponentGameRoom);

        Word word = wordRepository.findByTerm("Test").get();
        GameRoomRequest request = new GameRoomRequest();
        request.setRoomId(roomResult.getId().toString());
        request.setWordId(word.getId().toString());

        var result = gameRoomService.setWordInRoom(userResult, request);
        assertNull(gameRoomRepository.findById(roomResult.getId()).get().getWord());
    }
    @Test
    public void setImageInRoomPositiveTest(){
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
        Word word = wordRepository.findByTerm("Test").get();
        var gameRoom = GameRoom.builder()
                .status(GameRoomStatus.FREE)
                .changeDate(LocalDateTime.now())
                .word(word)
                .health(3)
                .build();

        var roomResult = gameRoomRepository.save(gameRoom);

        var userGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.DRAWING)
                .gameRoom(roomResult)
                .userProfile(userProfileResult)
                .build();
        var userGameRoomResult = userGameRoomRepository.save(userGameRoom);

        var opponentGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.WAITING)
                .gameRoom(roomResult)
                .build();
        userGameRoomRepository.save(opponentGameRoom);
        GameRoomRequest request = new GameRoomRequest();
        request.setRoomId(roomResult.getId().toString());
        request.setImage(new byte[]{0});

        var result = gameRoomService.setImageInRoom(userResult, request);
        assertArrayEquals(request.getImage(), gameRoomRepository.findById(roomResult.getId()).get().getImage());
        assertEquals(GameUserStatus.WAITING, userGameRoomRepository.findById(userGameRoomResult.getId()).get().getStatus());
    }
    @Test
    public void setImageInRoomNegativeTest(){
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
        Word word = wordRepository.findByTerm("Test").get();
        var gameRoom = GameRoom.builder()
                .status(GameRoomStatus.FREE)
                .changeDate(LocalDateTime.now())
                .word(word)
                .health(3)
                .build();

        var roomResult = gameRoomRepository.save(gameRoom);

        var userGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.WAITING)
                .gameRoom(roomResult)
                .userProfile(userProfileResult)
                .build();
        var userGameRoomResult = userGameRoomRepository.save(userGameRoom);

        var opponentGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.DRAWING)
                .gameRoom(roomResult)
                .build();
        userGameRoomRepository.save(opponentGameRoom);
        GameRoomRequest request = new GameRoomRequest();
        request.setRoomId(roomResult.getId().toString());
        request.setImage(new byte[]{0});

        var result = gameRoomService.setImageInRoom(userResult, request);
        assertNull(gameRoomRepository.findById(roomResult.getId()).get().getImage());
    }
    @Test
    public void checkWordInRoomPositiveTest(){
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
        Word word = wordRepository.findByTerm("Test").get();
        var gameRoom = GameRoom.builder()
                .status(GameRoomStatus.FREE)
                .changeDate(LocalDateTime.now())
                .word(word)
                .health(3)
                .build();

        var roomResult = gameRoomRepository.save(gameRoom);

        var userGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.GUESSING)
                .gameRoom(roomResult)
                .userProfile(userProfileResult)
                .build();
        var userGameRoomResult = userGameRoomRepository.save(userGameRoom);

        var opponentGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.WAITING)
                .gameRoom(roomResult)
                .build();
        userGameRoomRepository.save(opponentGameRoom);
        GameRoomRequest request = new GameRoomRequest();
        request.setRoomId(roomResult.getId().toString());
        request.setWordId(word.getTerm());
        var result = gameRoomService.checkWord(userResult, request);
        assertEquals(GameUserStatus.DRAWING, userGameRoomRepository.findById(userGameRoomResult.getId()).get().getStatus());
        assertEquals(WIN_REWARD + userProfileResult.getCurrency(), userProfileRepository.findById(userProfileResult.getId()).get().getCurrency());
    }
    @Test
    public void checkWordInRoomNegativeTest(){
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
        Word word = wordRepository.findByTerm("Test").get();
        var gameRoom = GameRoom.builder()
                .status(GameRoomStatus.FREE)
                .changeDate(LocalDateTime.now())
                .word(word)
                .health(3)
                .build();

        var roomResult = gameRoomRepository.save(gameRoom);

        var userGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.GUESSING)
                .gameRoom(roomResult)
                .userProfile(userProfileResult)
                .build();
        var userGameRoomResult = userGameRoomRepository.save(userGameRoom);

        var opponentGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.WAITING)
                .gameRoom(roomResult)
                .build();
        userGameRoomRepository.save(opponentGameRoom);
        GameRoomRequest request = new GameRoomRequest();
        request.setRoomId(roomResult.getId().toString());
        request.setWordId("Test2");
        var result = gameRoomService.checkWord(userResult, request);
        assertEquals(2, gameRoomRepository.findById(roomResult.getId()).get().getHealth());
    }
    @Test
    public void buyHintInRoomPositiveTest(){
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
        Word word = wordRepository.findByTerm("Test").get();
        var gameRoom = GameRoom.builder()
                .status(GameRoomStatus.FREE)
                .changeDate(LocalDateTime.now())
                .word(word)
                .health(3)
                .build();

        var roomResult = gameRoomRepository.save(gameRoom);

        var userGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.GUESSING)
                .gameRoom(roomResult)
                .userProfile(userProfileResult)
                .build();
        var userGameRoomResult = userGameRoomRepository.save(userGameRoom);

        var opponentGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.WAITING)
                .gameRoom(roomResult)
                .build();
        userGameRoomRepository.save(opponentGameRoom);

        var result = gameRoomService.getHintInRoom(userResult, roomResult.getId().toString());
        assertEquals(userProfileResult.getCurrency() - HINT_PRICE, userProfileRepository.findById(userProfileResult.getId()).get().getCurrency());
        assertNotEquals("", gameRoomRepository.findById(roomResult.getId()).get().getOpenHints());
    }
    @Test
    public void buyHintInRoomNegativeTest(){
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
                .currency(10)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);
        Word word = wordRepository.findByTerm("Test").get();
        var gameRoom = GameRoom.builder()
                .status(GameRoomStatus.FREE)
                .changeDate(LocalDateTime.now())
                .word(word)
                .health(3)
                .build();

        var roomResult = gameRoomRepository.save(gameRoom);

        var userGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.GUESSING)
                .gameRoom(roomResult)
                .userProfile(userProfileResult)
                .build();
        var userGameRoomResult = userGameRoomRepository.save(userGameRoom);

        var opponentGameRoom = UserGameRoom.builder()
                .status(GameUserStatus.WAITING)
                .gameRoom(roomResult)
                .build();
        userGameRoomRepository.save(opponentGameRoom);

        var result = gameRoomService.getHintInRoom(userResult, roomResult.getId().toString());
        assertNull(gameRoomRepository.findById(roomResult.getId()).get().getOpenHints());
        assertEquals(0, result.getLetter());
    }
}
