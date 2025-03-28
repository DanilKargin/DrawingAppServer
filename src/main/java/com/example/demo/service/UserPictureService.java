package com.example.demo.service;

import com.example.demo.controller.domain.request.user.UserPictureRequest;
import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.dto.UserPictureDto;
import com.example.demo.entity.User;
import com.example.demo.entity.UserPicture;
import com.example.demo.repository.UserPictureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPictureService {
    private final UserPictureRepository userPictureRepository;
    private final UserProfileService userProfileService;

    public List<UserPictureDto> getUserPictures(User user){
        var userProfile = userProfileService.findByUser(user);
        return userPictureRepository.findAllByUserProfile(userProfile).stream().map(UserPictureDto::new).collect(Collectors.toList());
    }
    public List<UserPictureDto> getUserPicturesByUserProfileId(String id){
        try {
            var userProfile = userProfileService.findById(id);
            return userPictureRepository.findAllByUserProfile(userProfile).stream().map(UserPictureDto::new).collect(Collectors.toList());
        }catch (Exception e){
            return null;
        }
    }

    public List<UserPictureDto> getUserPicturesOrderByLike(){

        return userPictureRepository.findAllByCreateDateAfterOrderByLikesAsc(
                LocalDateTime.now()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                        .withHour(0)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0))
                .stream().map(UserPictureDto::new).collect(Collectors.toList());
    }

    public MessageResponse createPicture(User user, UserPictureRequest request){
        var userProfile = userProfileService.findByUser(user);
        List<UserPictureDto> userPictureList = getUserPictures(user);
        if(userPictureList.size() + 1 <= userProfile.getPictureMaxCount()) {
            var userPicture = UserPicture.builder()
                    .userProfile(userProfile)
                    .image(request.getImage())
                    .likes(0)
                    .createDate(LocalDateTime.now())
                    .build();
            userPictureRepository.save(userPicture);
            return new MessageResponse("Изображение сохранено в профиль.", "");
        }else{
            return new MessageResponse("", "Недостаточно места.");
        }
    }
    public MessageResponse deletePicture(User user, UserPictureRequest request){
        var userProfile = userProfileService.findByUser(user);
        var userPictureOpt = userPictureRepository.findByUserProfileAndId(userProfile, UUID.fromString(request.getId()));
        if(userPictureOpt.isPresent()){
            userPictureRepository.delete(userPictureOpt.get());
            return new MessageResponse("Изображение удалено", "");
        }else{
            return new MessageResponse("", "Изображение не найдено.");
        }
    }
    private UserPicture getUserPictureById(UUID id){
        return userPictureRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Изображение не найдено."));
    }
}
