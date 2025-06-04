package com.example.demo;

import com.example.demo.controller.domain.request.group.GroupRequest;
import com.example.demo.controller.domain.request.group.MemberMessageRequest;
import com.example.demo.controller.domain.request.group.SearchGroupRequest;
import com.example.demo.entity.Group;
import com.example.demo.entity.GroupMember;
import com.example.demo.entity.User;
import com.example.demo.entity.UserProfile;
import com.example.demo.entity.enums.GroupType;
import com.example.demo.entity.enums.MemberRole;
import com.example.demo.entity.enums.MessageType;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.repository.*;
import com.example.demo.service.GroupService;
import com.example.demo.service.MemberMessageService;
import com.example.demo.service.UserPictureService;
import com.example.demo.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = DemoApplication.class)
public class GroupTests {
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

    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupLogoRepository groupLogoRepository;
    @Autowired
    private MemberMessageService memberMessageService;

    @Value("${gameConstants.group.createPrice}")
    private int CREATE_PRICE;

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
    private Group createGroupAndUserLeader(GroupType type){
        var user = User.builder()
                .email("other_user")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();
        var userResult = userRepository.save(user);

        var userProfile = UserProfile.builder()
                .user(userResult)
                .pictureMaxCount(9)
                .currency(10000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var groupResult = groupRepository.save(Group.builder()
                .groupLogo(groupLogoRepository.findById(UUID.fromString("b045c244-083e-41f4-a4d0-6932c90374bc")).get())
                .name("Test")
                .description("Test")
                .type(type)
                .tag("#test")
                .build());
        var groupMemberResult = groupMemberRepository.save(GroupMember.builder()
                .memberRole(MemberRole.LEADER)
                .group(groupResult)
                .userProfile(userProfileResult)
                .build());
        return groupResult;
    }
    private GroupMember createGroupMemberAndUserRole(Group group, MemberRole role){
        var user = User.builder()
                .email("other_user")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();
        var userResult = userRepository.save(user);

        var userProfile = UserProfile.builder()
                .user(userResult)
                .pictureMaxCount(9)
                .currency(10000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var groupMemberResult = groupMemberRepository.save(GroupMember.builder()
                .memberRole(role)
                .group(group)
                .userProfile(userProfileResult)
                .build());
        return groupMemberResult;
    }
    @Test
    public void createGroupPositiveTest(){
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
                .currency(10000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        GroupRequest request = new GroupRequest();
        request.setName("Test");
        request.setDescription("Test");
        request.setType(GroupType.FREE_ENTRY.toString());
        request.setLogoId("b045c244-083e-41f4-a4d0-6932c90374bc");

        var result = groupService.createGroup(userResult, request);

        var groupResult = groupRepository.findAll().get(0);
        assertEquals(userProfileResult.getCurrency() - CREATE_PRICE, userProfileRepository.findById(userProfileResult.getId()).get().getCurrency());

        assertEquals(request.getName(), groupResult.getName());
        assertEquals(request.getName(), result.getName());

        assertEquals(request.getDescription(), groupResult.getDescription());
        assertEquals(request.getDescription(), result.getDescription());

        assertEquals(request.getType(), groupResult.getType().toString());
        assertEquals(request.getType(), result.getType());

        assertEquals(request.getLogoId(), groupResult.getGroupLogo().getId().toString());
        assertEquals(request.getLogoId(), result.getLogoId());

        var groupMemberResult = groupMemberRepository.findAll().get(0);
        assertEquals(userProfileResult.getId(), groupMemberResult.getUserProfile().getId());
        assertEquals(MemberRole.LEADER, groupMemberResult.getMemberRole());
        assertEquals(groupResult.getId(), groupMemberResult.getGroup().getId());
    }
    @Test
    public void createGroupNegativeTest(){
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

        GroupRequest request = new GroupRequest();
        request.setName("Test");
        request.setDescription("Test");
        request.setType(GroupType.FREE_ENTRY.toString());
        request.setLogoId("b045c244-083e-41f4-a4d0-6932c90374bc");

        var result = groupService.createGroup(userResult, request);

        assertEquals(0, groupRepository.findAll().size());
        assertEquals(userProfileResult.getCurrency(), userProfileRepository.findById(userProfileResult.getId()).get().getCurrency());
    }
    @Test
    public void editGroupInformationTest(){
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
                .currency(10000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var groupResult = groupRepository.save(Group.builder()
                .groupLogo(groupLogoRepository.findById(UUID.fromString("b045c244-083e-41f4-a4d0-6932c90374bc")).get())
                .name("Test")
                .description("Test")
                .type(GroupType.FREE_ENTRY)
                .tag("#test")
                .build());
        var groupMemberResult = groupMemberRepository.save(GroupMember.builder()
                .memberRole(MemberRole.LEADER)
                .group(groupResult)
                .userProfile(userProfileResult)
                .build());

        GroupRequest request = new GroupRequest();
        request.setName("Test2");
        request.setDescription("Test2");
        request.setType(GroupType.CLOSE_ENTRY.toString());

        var result = groupService.editGroup(userResult, request);

        var updateGroup = groupRepository.findById(groupResult.getId()).get();
        assertEquals(request.getName(), updateGroup.getName());
        assertEquals(request.getName(), result.getName());

        assertEquals(request.getDescription(), updateGroup.getDescription());
        assertEquals(request.getDescription(), result.getDescription());

        assertEquals(request.getType(), updateGroup.getType().toString());
        assertEquals(request.getType(), result.getType());
    }

    @Test
    public void sendMessageInGroupTest(){
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
                .currency(10000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var groupResult = groupRepository.save(Group.builder()
                .groupLogo(groupLogoRepository.findById(UUID.fromString("b045c244-083e-41f4-a4d0-6932c90374bc")).get())
                .name("Test")
                .description("Test")
                .type(GroupType.FREE_ENTRY)
                .tag("#test")
                .build());
        var groupMemberResult = groupMemberRepository.save(GroupMember.builder()
                .memberRole(MemberRole.LEADER)
                .group(groupResult)
                .userProfile(userProfileResult)
                .build());

        MemberMessageRequest request = new MemberMessageRequest("Test", userProfileResult.getId().toString(), MessageType.MESSAGE.toString());
        var result = memberMessageService.sendMessage(request);

        var messageResult = memberMessageRepository.findAll().get(0);
        assertEquals(groupMemberResult.getId(), messageResult.getGroupMember().getId());
        assertEquals(request.getText(), messageResult.getText());
        assertEquals(MessageType.MESSAGE, messageResult.getType());
    }

    @Test
    public void joinGroupPositiveTest(){
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
                .currency(10000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var group = createGroupAndUserLeader(GroupType.FREE_ENTRY);

        SearchGroupRequest request = new SearchGroupRequest();
        request.setId(group.getId().toString());
        var result = groupService.joinGroup(userResult, request);

        var groupMembers = groupMemberRepository.findAll();

        assertNotNull(result.getContent());
        assertTrue(groupMembers.stream().anyMatch(item -> item.getUserProfile() != null
                && item.getGroup() != null
                && userProfileResult.getId().equals(item.getUserProfile().getId())
                && group.getId().equals(item.getGroup().getId())
        ));
    }

    @Test
    public void joinGroupNegativeTest(){
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
                .currency(10000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var groupResult = groupRepository.save(Group.builder()
                .groupLogo(groupLogoRepository.findById(UUID.fromString("b045c244-083e-41f4-a4d0-6932c90374bc")).get())
                .name("Test")
                .description("Test")
                .type(GroupType.FREE_ENTRY)
                .tag("#test")
                .build());
        var groupMemberResult = groupMemberRepository.save(GroupMember.builder()
                .memberRole(MemberRole.LEADER)
                .group(groupResult)
                .userProfile(userProfileResult)
                .build());

        var group = createGroupAndUserLeader(GroupType.FREE_ENTRY);

        SearchGroupRequest request = new SearchGroupRequest();
        request.setId(group.getId().toString());
        var result = groupService.joinGroup(userResult, request);

        var groupMembers = groupMemberRepository.findAll();

        assertNotNull(result.getError());
        assertFalse(groupMembers.stream().allMatch(item -> item.getUserProfile() != null
                && item.getGroup() != null
                && userProfileResult.getId().equals(item.getUserProfile().getId())
                && group.getId().equals(item.getGroup().getId())
        ));
    }

    @Test
    public void quitFromGroupTest(){
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
                .currency(10000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var group = createGroupAndUserLeader(GroupType.FREE_ENTRY);

        var groupMemberResult = groupMemberRepository.save(GroupMember.builder()
                .userProfile(userProfileResult)
                .memberRole(MemberRole.MEMBER)
                .group(group)
                .build());

        var result = groupService.quitFromGroup(userResult);
        var groupMembers = groupMemberRepository.findAll();
        assertNotNull(result.getContent());
        assertEquals(MemberRole.EXCLUDED, groupMemberRepository.findById(groupMemberResult.getId()).get().getMemberRole());
    }

    @Test
    public void rangUpInGroupTest(){
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
                .currency(10000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var groupResult = groupRepository.save(Group.builder()
                .groupLogo(groupLogoRepository.findById(UUID.fromString("b045c244-083e-41f4-a4d0-6932c90374bc")).get())
                .name("Test")
                .description("Test")
                .type(GroupType.FREE_ENTRY)
                .tag("#test")
                .build());
        var groupMemberResult = groupMemberRepository.save(GroupMember.builder()
                .memberRole(MemberRole.LEADER)
                .group(groupResult)
                .userProfile(userProfileResult)
                .build());

        var member = createGroupMemberAndUserRole(groupResult, MemberRole.MEMBER);

        var result = groupService.upRoleMember(userResult, member.getId().toString());

        assertNotNull(result.getContent());
        assertEquals(MemberRole.OFFICER, groupMemberRepository.findById(member.getId()).get().getMemberRole());
    }
    @Test
    public void rangLowerInGroupTest(){
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
                .currency(10000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var groupResult = groupRepository.save(Group.builder()
                .groupLogo(groupLogoRepository.findById(UUID.fromString("b045c244-083e-41f4-a4d0-6932c90374bc")).get())
                .name("Test")
                .description("Test")
                .type(GroupType.FREE_ENTRY)
                .tag("#test")
                .build());
        var groupMemberResult = groupMemberRepository.save(GroupMember.builder()
                .memberRole(MemberRole.LEADER)
                .group(groupResult)
                .userProfile(userProfileResult)
                .build());

        var member = createGroupMemberAndUserRole(groupResult, MemberRole.OFFICER);

        var result = groupService.lowerRoleMember(userResult, member.getId().toString());

        assertNotNull(result.getContent());
        assertEquals(MemberRole.MEMBER, groupMemberRepository.findById(member.getId()).get().getMemberRole());
    }
    @Test
    public void excludeInGroupTest(){
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
                .currency(10000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var groupResult = groupRepository.save(Group.builder()
                .groupLogo(groupLogoRepository.findById(UUID.fromString("b045c244-083e-41f4-a4d0-6932c90374bc")).get())
                .name("Test")
                .description("Test")
                .type(GroupType.FREE_ENTRY)
                .tag("#test")
                .build());
        var groupMemberResult = groupMemberRepository.save(GroupMember.builder()
                .memberRole(MemberRole.LEADER)
                .group(groupResult)
                .userProfile(userProfileResult)
                .build());

        var member = createGroupMemberAndUserRole(groupResult, MemberRole.MEMBER);

        var result = groupService.excludeMember(userResult, member.getId().toString());

        assertNotNull(result.getContent());
        assertEquals(MemberRole.EXCLUDED, groupMemberRepository.findById(member.getId()).get().getMemberRole());
    }

    @Test
    public void sendRequestInGroupTest(){
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
                .currency(10000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var group = createGroupAndUserLeader(GroupType.ENTRY_ON_REQUEST);

        SearchGroupRequest request = new SearchGroupRequest();
        request.setId(group.getId().toString());

        var result = groupService.joinGroup(userResult, request);
        var groupMembers = groupMemberRepository.findAll();
        assertNotNull(result.getContent());
        assertTrue(groupMembers.stream().anyMatch(item -> item.getUserProfile() != null
                && item.getGroup() != null
                && item.getMemberRole().equals(MemberRole.NOT_CONFIRMED)
                && userProfileResult.getId().equals(item.getUserProfile().getId())
                && group.getId().equals(item.getGroup().getId())
        ));
    }

    @Test
    public void acceptRequestInGroup(){
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
                .currency(10000)
                .nickname("Test")
                .build();
        var userProfileResult = userProfileRepository.save(userProfile);

        var groupResult = groupRepository.save(Group.builder()
                .groupLogo(groupLogoRepository.findById(UUID.fromString("b045c244-083e-41f4-a4d0-6932c90374bc")).get())
                .name("Test")
                .description("Test")
                .type(GroupType.ENTRY_ON_REQUEST)
                .tag("#test")
                .build());
        var groupMemberResult = groupMemberRepository.save(GroupMember.builder()
                .memberRole(MemberRole.LEADER)
                .group(groupResult)
                .userProfile(userProfileResult)
                .build());

        var groupMember = createGroupMemberAndUserRole(groupResult, MemberRole.NOT_CONFIRMED);

        var result = groupService.acceptRequest(userResult, groupMember.getId().toString());
        assertNotNull(result.getContent());
        assertEquals(MemberRole.MEMBER, groupMemberRepository.findById(groupMember.getId()).get().getMemberRole());
    }
}
