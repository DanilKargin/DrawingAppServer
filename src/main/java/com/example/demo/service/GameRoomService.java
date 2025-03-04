package com.example.demo.service;

import com.example.demo.controller.domain.request.gameRoom.GameRoomRequest;
import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.dto.GameRoomDto;
import com.example.demo.dto.UserGameRoomDto;
import com.example.demo.entity.GameRoom;
import com.example.demo.entity.User;
import com.example.demo.entity.UserGameRoom;
import com.example.demo.entity.UserProfile;
import com.example.demo.entity.enums.GameRoomStatus;
import com.example.demo.entity.enums.GameUserStatus;
import com.example.demo.repository.GameRoomRepository;
import com.example.demo.repository.UserGameRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameRoomService {
    private final GameRoomRepository gameRoomRepository;
    private final UserGameRoomRepository userGameRoomRepository;
    private final UserProfileService userProfileService;
    private final WordService wordService;

    public List<UserGameRoomDto> getReadyRoomList(User user){
        var userProfile = userProfileService.findByUser(user);
        return userGameRoomRepository.findAllByUserProfileIsNotAndStatusIsNot(userProfile.getId(), GameUserStatus.WAITING).stream().map(UserGameRoomDto::new).collect(Collectors.toList());
    }
    public List<UserGameRoomDto> getWaitingRoomList(User user){
        var userProfile = userProfileService.findByUser(user);
        return userGameRoomRepository.findAllByUserProfileIsNotAndStatus(userProfile.getId(), GameUserStatus.WAITING).stream().map(UserGameRoomDto::new).collect(Collectors.toList());
    }

    @Transactional
    public GameRoomDto findGameRoom(User user) {
        var userProfile = userProfileService.findByUser(user);
        var opponentOpt = gameRoomRepository.findFirstOldRoomByStatus(GameRoomStatus.FREE, GameUserStatus.WAITING, userProfile);
        GameRoom gameRoom = null;
        UserGameRoom userGameRoom;
        if(userProfileService.subtractEnergy(userProfile)) {
            if (opponentOpt.isPresent()) {
                var room = opponentOpt.get();
                room.setStatus(GameRoomStatus.BUSY);
                gameRoom = gameRoomRepository.save(room);
                userGameRoom = getUserGameRoomByUserAndRoom(null, gameRoom);
                userGameRoom.setUserProfile(userProfile);
            } else {
                var room = GameRoom.builder()
                        .status(GameRoomStatus.FREE)
                        .health(3)
                        .changeDate(LocalDateTime.now())
                        .build();
                gameRoom = gameRoomRepository.save(room);
                userGameRoom = UserGameRoom.builder()
                        .userProfile(userProfile)
                        .gameRoom(gameRoom)
                        .status(GameUserStatus.DRAWING)
                        .build();
                var opponentUserGameRoom = UserGameRoom.builder()
                        .userProfile(null)
                        .gameRoom(gameRoom)
                        .status(GameUserStatus.WAITING)
                        .build();
                userGameRoomRepository.save(opponentUserGameRoom);
            }
            userGameRoomRepository.save(userGameRoom);
            var result = new GameRoomDto(gameRoom);
            if(userGameRoom.getStatus() == GameUserStatus.GUESSING){
                result.setLetters(createAlphabet(result.getTerm()));
                result.setTerm(null);
                result.setDescription(null);
            }
            return result;
        }else{
            return null;
        }
    }

    public GameRoomDto getGameRoomById(User user, String roomId){
        try{
            var userProfile = userProfileService.findByUser(user);
            var gameRoom = getGameRoomById(UUID.fromString(roomId));
            var userGameRoom = getUserGameRoomByUserAndRoom(userProfile, gameRoom);
            GameRoomDto result = new GameRoomDto(gameRoom);
            if(userGameRoom.getStatus().equals(GameUserStatus.GUESSING)){
                result.setLetters(createAlphabet(result.getTerm()));
                result.setTerm(null);
                result.setDescription(null);
            }
            return result;
        }catch(Exception e){
            return null;
        }
    }

    @Transactional
    public MessageResponse setImageInRoom(User user, GameRoomRequest request){
        try {
            var userProfile = userProfileService.findByUser(user);
            var gameRoom = getGameRoomById(UUID.fromString(request.getRoomId()));
            var userGameRoom = getUserGameRoomByUserAndRoom(userProfile, gameRoom);
            var opponentGameRoom = userGameRoomRepository.findByUserProfileIsNotAndGameRoom(userProfile, gameRoom);
            if(userGameRoom.getStatus().equals(GameUserStatus.DRAWING)){
                gameRoom.setImage(request.getImage());
                gameRoom.setHealth(3);
                gameRoom.setChangeDate(LocalDateTime.now());
                userGameRoom.setStatus(GameUserStatus.WAITING);
                userGameRoomRepository.save(userGameRoom);
                gameRoomRepository.save(gameRoom);
                if(opponentGameRoom.isPresent()){
                    var opponent = opponentGameRoom.get();
                    opponent.setStatus(GameUserStatus.GUESSING);
                    userGameRoomRepository.save(opponent);
                }
            }
            return new MessageResponse("Успешно!", "");
        }catch(Exception e){
            return new MessageResponse("", e.getMessage());
        }
    }
    public GameRoomDto setWordInRoom(User user, GameRoomRequest request){
        try {
            var userProfile = userProfileService.findByUser(user);
            var gameRoom = getGameRoomById(UUID.fromString(request.getRoomId()));
            var word = wordService.findById(UUID.fromString(request.getWordId()));
            var userGameRoom = getUserGameRoomByUserAndRoom(userProfile, gameRoom);
            if(userGameRoom.getStatus().equals(GameUserStatus.DRAWING)){
                if(gameRoom.getWord() == null) {
                    gameRoom.setWord(word);
                    return new GameRoomDto(gameRoomRepository.save(gameRoom));
                }
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }
    @Transactional
    public GameRoomDto checkWord(User user, GameRoomRequest request){
        try {
            var userProfile = userProfileService.findByUser(user);
            var gameRoom = getGameRoomById(UUID.fromString(request.getRoomId()));
            var userGameRoom = getUserGameRoomByUserAndRoom(userProfile, gameRoom);
            var word = wordService.findByTerm(request.getWordId());
            if(userGameRoom.getStatus().equals(GameUserStatus.GUESSING)){
                var health = gameRoom.getHealth();
                GameRoomDto gameRoomDto;
                if(health > 0){
                    if(word.isPresent() && gameRoom.getWord().getTerm().equalsIgnoreCase(word.get().getTerm())){
                        userGameRoom.setStatus(GameUserStatus.DRAWING);
                        userGameRoomRepository.save(userGameRoom);
                        gameRoom.setWord(null);
                        gameRoom.setImage(null);
                    }else{
                        health-=1;
                        if(health <= 0){
                            userGameRoom.setStatus(GameUserStatus.DRAWING);
                            userGameRoomRepository.save(userGameRoom);
                            gameRoom.setWord(null);
                            gameRoom.setImage(null);
                        }
                        gameRoom.setHealth(health);
                    }
                    gameRoomDto = new GameRoomDto(gameRoomRepository.save(gameRoom));
                    if(userGameRoom.getGameRoom().equals(GameUserStatus.GUESSING)){
                        gameRoomDto.setTerm(null);
                        gameRoomDto.setDescription(null);
                    }
                    return gameRoomDto;
                }
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }
    private GameRoom getGameRoomById(UUID id){
        return gameRoomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Комната не найдена!"));
    }
    private UserGameRoom getUserGameRoomByUserAndRoom(UserProfile user, GameRoom room){
        return userGameRoomRepository.findByUserProfileAndGameRoom(user, room)
                .orElseThrow(() -> new NotFoundException("Комната не найдена!"));
    }
    private String createAlphabet(String word){
        char[] alphabet = new char[18];
        Random rand = new Random();
        for(int i = 0; i < alphabet.length; i++){
            alphabet[i] = (char) (rand.nextInt(32) + 1040);
        }

        List<Integer> history = new ArrayList<>();
        for(var letter : word.toUpperCase().toCharArray()){
            while(true){
                var index = rand.nextInt(alphabet.length);
                if(!history.contains(index)) {
                    alphabet[index] = letter;
                    history.add(index);
                    break;
                }
            }
        }
        return new String(alphabet);
    }
}
