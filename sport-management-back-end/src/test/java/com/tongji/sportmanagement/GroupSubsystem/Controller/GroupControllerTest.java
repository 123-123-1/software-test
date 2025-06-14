package com.tongji.sportmanagement.GroupSubsystem.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tongji.sportmanagement.Common.DTO.AuditResultDTO;
import com.tongji.sportmanagement.Common.DTO.ResultMsg;
import com.tongji.sportmanagement.Common.ServiceException;
import com.tongji.sportmanagement.GroupSubsystem.DTO.*;
import com.tongji.sportmanagement.GroupSubsystem.Entity.*;
import com.tongji.sportmanagement.GroupSubsystem.Repository.GroupMemberRepository;
import com.tongji.sportmanagement.GroupSubsystem.Repository.GroupRepository;
import com.tongji.sportmanagement.GroupSubsystem.Service.GroupApplicationService;
import com.tongji.sportmanagement.GroupSubsystem.Service.GroupMemberService;
import com.tongji.sportmanagement.GroupSubsystem.Service.GroupRecordService;
import com.tongji.sportmanagement.GroupSubsystem.Service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GroupControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GroupService groupService;

    @Mock
    private GroupApplicationService groupApplicationService;

    @Mock
    private GroupMemberService groupMemberService;

    @Mock
    private GroupRecordService groupRecordService;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupController groupController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(groupController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    private Group createTestGroup(Integer groupId, String groupName) {
        Group group = new Group();
        group.setGroupId(groupId);
        group.setGroupName(groupName);
        group.setCreationTime(Instant.now());
        group.setChatId(groupId); 
        return group;
    }

    @Test
    void testCreateGroup() throws Exception {
        CompleteGroupDTO dto = new CompleteGroupDTO(123, "This is a test group", "Test Group", null);
        ResultMsg mockResult = new ResultMsg("success",1);
        Mockito.when(groupService.createGroup(Mockito.any(), Mockito.anyInt())).thenReturn(mockResult);

        mockMvc.perform(post("/api/groups")
                .requestAttr("idFromToken", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("success"));
    }

    @Test
    void testGetGroups() throws Exception {
        Group group1 = new Group();
        group1.setGroupId(1);
        group1.setGroupName("Group 1");
        group1.setDescription("Description 1");

        Group group2 = new Group();
        group2.setGroupId(2);
        group2.setGroupName("Group 2");
        group2.setDescription("Description 2");

        List<Group> groups = Arrays.asList(group1, group2);
        Mockito.when(groupService.getGroups()).thenReturn(groups);

        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].groupId").value(1))
                .andExpect(jsonPath("$[0].groupName").value("Group 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[1].groupId").value(2))
                .andExpect(jsonPath("$[1].groupName").value("Group 2"))
                .andExpect(jsonPath("$[1].description").value("Description 2"));
    }

    @Test
    void getGroupByUser_Success() throws Exception {
        Integer userId = 1;
        List<Group> expectedGroups = new ArrayList<>();
        Group group1 = new Group();
        group1.setGroupId(1);
        group1.setGroupName("Test Group 1");
        group1.setDescription("Test Description 1");
        expectedGroups.add(group1);

        Group group2 = new Group();
        group2.setGroupId(2);
        group2.setGroupName("Test Group 2");
        group2.setDescription("Test Description 2");
        expectedGroups.add(group2);

        when(groupService.getByUserId(userId)).thenReturn(expectedGroups);

        mockMvc.perform(get("/api/groups/byUser")
                .requestAttr("idFromToken", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].groupId").value(1))
                .andExpect(jsonPath("$[0].groupName").value("Test Group 1"))
                .andExpect(jsonPath("$[0].description").value("Test Description 1"))
                .andExpect(jsonPath("$[1].groupId").value(2))
                .andExpect(jsonPath("$[1].groupName").value("Test Group 2"))
                .andExpect(jsonPath("$[1].description").value("Test Description 2"));

        verify(groupService).getByUserId(userId);
    }

    @Test
    void getGroupByUser_NoGroups() throws Exception {
        Integer userId = 1;
        List<Group> emptyList = new ArrayList<>();

        when(groupService.getByUserId(userId)).thenReturn(emptyList);

        mockMvc.perform(get("/api/groups/byUser")
                .requestAttr("idFromToken", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(groupService).getByUserId(userId);
    }

    @Test
    void getUserLeaderGroup_Success() throws Exception {
        Integer userId = 1;
        List<Group> expectedGroups = Arrays.asList(
            createTestGroup(1, "Test Group 1"),
            createTestGroup(2, "Test Group 2")
        );

        when(groupService.getByUserIdFiltered(userId)).thenReturn(expectedGroups);

        mockMvc.perform(get("/api/groups/leadergroups")
                .requestAttr("idFromToken", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].groupId").value(1))
                .andExpect(jsonPath("$[0].groupName").value("Test Group 1"))
                .andExpect(jsonPath("$[1].groupId").value(2))
                .andExpect(jsonPath("$[1].groupName").value("Test Group 2"));

        verify(groupService).getByUserIdFiltered(userId);
    }

    @Test
    void getUserLeaderGroup_NoLeaderGroups() throws Exception {
        Integer userId = 1;
        List<Group> emptyList = new ArrayList<>();

        when(groupService.getByUserIdFiltered(userId)).thenReturn(emptyList);

        mockMvc.perform(get("/api/groups/leadergroups")
                .requestAttr("idFromToken", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(groupService).getByUserIdFiltered(userId);
    }

    @Test
    void deleteGroup_Success() throws Exception {
        Integer userId = 1;
        Integer groupId = 1;
        ResultMsg expectedResult = ResultMsg.success("Group deleted successfully");

        when(groupService.deleteGroup(groupId, userId)).thenReturn(expectedResult);

        mockMvc.perform(delete("/api/groups")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(1))
                .andExpect(jsonPath("$.msg").value("Group deleted successfully"));

        verify(groupService).deleteGroup(groupId, userId);
    }

    @Test
    void deleteGroup_NoPermission() throws Exception {
        Integer userId = 1;
        Integer groupId = 1;

        when(groupService.deleteGroup(groupId, userId))
                .thenThrow(new IllegalArgumentException("No permission to delete group"));

        mockMvc.perform(delete("/api/groups")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("No permission to delete group"));

        verify(groupService).deleteGroup(groupId, userId);
    }

    @Test
    void deleteGroup_GroupNotFound() throws Exception {
        Integer userId = 1;
        Integer groupId = 999;

        when(groupService.deleteGroup(groupId, userId))
                .thenThrow(new IllegalArgumentException("Group not found"));

        mockMvc.perform(delete("/api/groups")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Group not found"));

        verify(groupService).deleteGroup(groupId, userId);
    }

    @Test
    void getGroupApplication_Success() throws Exception {
        Integer userId = 1;
        List<GroupApplicationResultDTO> expectedApplications = new ArrayList<>();
        
        GroupApplicationResultDTO application1 = new GroupApplicationResultDTO();
        application1.setGroupApplicationId(1);
        application1.setType(GroupApplicationType.invited);
        application1.setApplyInfo("I want to join this group");
        application1.setState(GroupApplicationState.accepted);
        application1.setOperationTime(Instant.now());
        application1.setExpirationTime(Instant.now().plusSeconds(86400));
        application1.setApplicantId(2);
        application1.setApplicantName("Applicant 1");
        application1.setGroupId(1);
        application1.setGroupName("Test Group 1");
        application1.setReviewerId(null);
        application1.setReviewerName(null);
        expectedApplications.add(application1);

        GroupApplicationResultDTO application2 = new GroupApplicationResultDTO();
        application2.setGroupApplicationId(2);
        application2.setType(GroupApplicationType.apply);
        application2.setApplyInfo("Invitation to join group");
        application2.setState(GroupApplicationState.rejected);
        application2.setOperationTime(Instant.now());
        application2.setExpirationTime(Instant.now().plusSeconds(86400));
        application2.setApplicantId(3);
        application2.setApplicantName("Applicant 2");
        application2.setGroupId(2);
        application2.setGroupName("Test Group 2");
        application2.setReviewerId(null);
        application2.setReviewerName(null);
        expectedApplications.add(application2);

        when(groupApplicationService.getGroupApplications(userId)).thenReturn(expectedApplications);

        mockMvc.perform(get("/api/groups/application")
                .requestAttr("idFromToken", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].groupApplicationId").value(1))
                .andExpect(jsonPath("$[0].type").value("invited"))
                .andExpect(jsonPath("$[0].applyInfo").value("I want to join this group"))
                .andExpect(jsonPath("$[0].state").value("accepted"))
                .andExpect(jsonPath("$[0].operationTime").exists())
                .andExpect(jsonPath("$[0].expirationTime").exists())
                .andExpect(jsonPath("$[0].applicantId").value(2))
                .andExpect(jsonPath("$[0].applicantName").value("Applicant 1"))
                .andExpect(jsonPath("$[0].groupId").value(1))
                .andExpect(jsonPath("$[0].groupName").value("Test Group 1"))
                .andExpect(jsonPath("$[0].reviewerId").isEmpty())
                .andExpect(jsonPath("$[0].reviewerName").isEmpty())
                .andExpect(jsonPath("$[1].groupApplicationId").value(2))
                .andExpect(jsonPath("$[1].type").value("apply"))
                .andExpect(jsonPath("$[1].applyInfo").value("Invitation to join group"))
                .andExpect(jsonPath("$[1].state").value("rejected"))
                .andExpect(jsonPath("$[1].operationTime").exists())
                .andExpect(jsonPath("$[1].expirationTime").exists())
                .andExpect(jsonPath("$[1].applicantId").value(3))
                .andExpect(jsonPath("$[1].applicantName").value("Applicant 2"))
                .andExpect(jsonPath("$[1].groupId").value(2))
                .andExpect(jsonPath("$[1].groupName").value("Test Group 2"))
                .andExpect(jsonPath("$[1].reviewerId").isEmpty())
                .andExpect(jsonPath("$[1].reviewerName").isEmpty());

        verify(groupApplicationService).getGroupApplications(userId);
    }

    @Test
    void getGroupApplication_NoApplications() throws Exception {
        Integer userId = 1;
        List<GroupApplicationResultDTO> emptyList = new ArrayList<>();

        when(groupApplicationService.getGroupApplications(userId)).thenReturn(emptyList);

        mockMvc.perform(get("/api/groups/application")
                .requestAttr("idFromToken", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(groupApplicationService).getGroupApplications(userId);
    }

    @Test
    void sendGroupApplication_Success() throws Exception {
        Integer userId = 1;
        GroupApplicationDTO applicationDTO = new GroupApplicationDTO(1, userId, "I want to join this group");

        ResultMsg expectedResult = ResultMsg.success("Application sent successfully");

        when(groupApplicationService.sendApplicationIng(applicationDTO, userId))
                .thenReturn(expectedResult);

        mockMvc.perform(post("/api/groups/application")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applicationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(1))
                .andExpect(jsonPath("$.msg").value("Application sent successfully"));

        verify(groupApplicationService).sendApplicationIng(applicationDTO, userId);
    }

    @Test
    void sendGroupApplication_InvalidGroup() throws Exception {
        Integer userId = 1;
        GroupApplicationDTO applicationDTO = new GroupApplicationDTO(999, userId, "I want to join this group");

        when(groupApplicationService.sendApplicationIng(applicationDTO, userId))
                .thenThrow(new IllegalArgumentException("Group not found"));

        mockMvc.perform(post("/api/groups/application")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applicationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Group not found"));

        verify(groupApplicationService).sendApplicationIng(applicationDTO, userId);
    }

    @Test
    void sendGroupApplication_DuplicateApplication() throws Exception {
        Integer userId = 1;
        GroupApplicationDTO applicationDTO = new GroupApplicationDTO(1, userId, "I want to join this group");

        when(groupApplicationService.sendApplicationIng(applicationDTO, userId))
                .thenThrow(new IllegalArgumentException("Application already submitted"));

        mockMvc.perform(post("/api/groups/application")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applicationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Application already submitted"));

        verify(groupApplicationService).sendApplicationIng(applicationDTO, userId);
    }

    @Test
    void sendGroupApplication_InvalidApplicant() throws Exception {
        Integer userId = 1;
        GroupApplicationDTO applicationDTO = new GroupApplicationDTO(1, 999, "I want to join this group");

        when(groupApplicationService.sendApplicationIng(applicationDTO, userId))
                .thenThrow(new IllegalArgumentException("Applicant ID does not match current user"));

        mockMvc.perform(post("/api/groups/application")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applicationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Applicant ID does not match current user"));

        verify(groupApplicationService).sendApplicationIng(applicationDTO, userId);
    }

    @Test
    void inviteMember_Success() throws Exception {
        Integer userId = 1;
        InviteGroupDTO inviteDTO = new InviteGroupDTO();
        inviteDTO.setGroupId(1);
        inviteDTO.setInviteeId(2);

        ResultMsg expectedResult = ResultMsg.success("Invitation sent successfully");

        when(groupApplicationService.inviteMember(inviteDTO, userId))
                .thenReturn(expectedResult);

        mockMvc.perform(post("/api/groups/application/by")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inviteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(1))
                .andExpect(jsonPath("$.msg").value("Invitation sent successfully"));

        verify(groupApplicationService).inviteMember(inviteDTO, userId);
    }

    @Test
    void inviteMember_InvalidGroup() throws Exception {
        Integer userId = 1;
        InviteGroupDTO inviteDTO = new InviteGroupDTO();
        inviteDTO.setGroupId(999);
        inviteDTO.setInviteeId(2);

        when(groupApplicationService.inviteMember(inviteDTO, userId))
                .thenThrow(new IllegalArgumentException("Group not found"));

        mockMvc.perform(post("/api/groups/application/by")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inviteDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Group not found"));

        verify(groupApplicationService).inviteMember(inviteDTO, userId);
    }

    @Test
    void inviteMember_NoPermission() throws Exception {
        Integer userId = 1;
        InviteGroupDTO inviteDTO = new InviteGroupDTO();
        inviteDTO.setGroupId(1);
        inviteDTO.setInviteeId(2);

        when(groupApplicationService.inviteMember(inviteDTO, userId))
                .thenThrow(new IllegalArgumentException("No permission to invite member"));

        mockMvc.perform(post("/api/groups/application/by")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inviteDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("No permission to invite member"));

        verify(groupApplicationService).inviteMember(inviteDTO, userId);
    }

    @Test
    void inviteMember_AlreadyMember() throws Exception {
        Integer userId = 1;
        InviteGroupDTO inviteDTO = new InviteGroupDTO();
        inviteDTO.setGroupId(1);
        inviteDTO.setInviteeId(2);

        when(groupApplicationService.inviteMember(inviteDTO, userId))
                .thenThrow(new IllegalArgumentException("Member already exists in the group"));

        mockMvc.perform(post("/api/groups/application/by")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inviteDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Member already exists in the group"));

        verify(groupApplicationService).inviteMember(inviteDTO, userId);
    }

    @Test
    void inviteMember_DuplicateInvitation() throws Exception {
        Integer userId = 1;
        InviteGroupDTO inviteDTO = new InviteGroupDTO();
        inviteDTO.setGroupId(1);
        inviteDTO.setInviteeId(2);

        when(groupApplicationService.inviteMember(inviteDTO, userId))
                .thenThrow(new IllegalArgumentException("Invitation already sent to the member"));

        mockMvc.perform(post("/api/groups/application/by")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inviteDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Invitation already sent to the member"));

        verify(groupApplicationService).inviteMember(inviteDTO, userId);
    }

    @Test
    void updateGroupApplication_Success() throws Exception {
        Integer userId = 1;
        AuditResultDTO auditResultDTO = new AuditResultDTO();
        auditResultDTO.setResult(true);
        auditResultDTO.setReviewerId(userId);
        auditResultDTO.setAuditObjectId(1);

        ResultMsg expectedResult = ResultMsg.success("Audit result updated successfully");

        when(groupApplicationService.updateApplication(auditResultDTO, userId))
                .thenReturn(expectedResult);

        mockMvc.perform(patch("/api/groups/application")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auditResultDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(1))
                .andExpect(jsonPath("$.msg").value("Audit result updated successfully"));

        verify(groupApplicationService).updateApplication(auditResultDTO, userId);
    }

    @Test
    void updateGroupApplication_ApplicationNotFound() throws Exception {
        Integer userId = 1;
        AuditResultDTO auditResultDTO = new AuditResultDTO();
        auditResultDTO.setResult(true);
        auditResultDTO.setReviewerId(userId);
        auditResultDTO.setAuditObjectId(999);

        when(groupApplicationService.updateApplication(auditResultDTO, userId))
                .thenThrow(new IllegalArgumentException("Application not found"));

        mockMvc.perform(patch("/api/groups/application")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auditResultDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Application not found"));

        verify(groupApplicationService).updateApplication(auditResultDTO, userId);
    }

    @Test
    void updateGroupApplication_NoPermission() throws Exception {
        Integer userId = 1;
        AuditResultDTO auditResultDTO = new AuditResultDTO();
        auditResultDTO.setResult(true);
        auditResultDTO.setReviewerId(userId);
        auditResultDTO.setAuditObjectId(1);

        when(groupApplicationService.updateApplication(auditResultDTO, userId))
                .thenThrow(new IllegalArgumentException("No permission to update application"));

        mockMvc.perform(patch("/api/groups/application")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auditResultDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("No permission to update application"));

        verify(groupApplicationService).updateApplication(auditResultDTO, userId);
    }

    @Test
    void updateGroupApplication_AlreadyProcessed() throws Exception {
        Integer userId = 1;
        AuditResultDTO auditResultDTO = new AuditResultDTO();
        auditResultDTO.setResult(true);
        auditResultDTO.setReviewerId(userId);
        auditResultDTO.setAuditObjectId(1);

        when(groupApplicationService.updateApplication(auditResultDTO, userId))
                .thenThrow(new IllegalArgumentException("Application already processed"));

        mockMvc.perform(patch("/api/groups/application")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auditResultDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Application already processed"));

        verify(groupApplicationService).updateApplication(auditResultDTO, userId);
    }

    @Test
    void updateGroupApplication_Expired() throws Exception {
        Integer userId = 1;
        AuditResultDTO auditResultDTO = new AuditResultDTO();
        auditResultDTO.setResult(true);
        auditResultDTO.setReviewerId(userId);
        auditResultDTO.setAuditObjectId(1);

        when(groupApplicationService.updateApplication(auditResultDTO, userId))
                .thenThrow(new IllegalArgumentException("Application expired"));

        mockMvc.perform(patch("/api/groups/application")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auditResultDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Application expired"));

        verify(groupApplicationService).updateApplication(auditResultDTO, userId);
    }

    @Test
    void getGroupByID_Success() throws Exception {
        Integer groupId = 1;
        GroupDetailDTO expectedGroup = new GroupDetailDTO();
        expectedGroup.setGroupId(groupId);
        expectedGroup.setGroupName("Test Group");
        expectedGroup.setDescription("This is a test group");
        expectedGroup.setCreationTime(Instant.now());
        expectedGroup.setChatId(groupId);
        
        List<GroupMemberDetailDTO> members = new ArrayList<>();
        GroupMemberDetailDTO member1 = new GroupMemberDetailDTO();
        member1.setUserId(1);
        member1.setUserName("Leader");
        member1.setRole("leader");
        members.add(member1);
        
        GroupMemberDetailDTO member2 = new GroupMemberDetailDTO();
        member2.setUserId(2);
        member2.setUserName("Member 1");
        member2.setRole("member");
        members.add(member2);
        
        expectedGroup.setMembers(members);

        when(groupService.getGroupDetail(groupId)).thenReturn(expectedGroup);

        mockMvc.perform(get("/api/groups/byId/{groupId}", groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupId").value(groupId))
                .andExpect(jsonPath("$.groupName").value("Test Group"))
                .andExpect(jsonPath("$.description").value("This is a test group"))
                .andExpect(jsonPath("$.creationTime").exists())
                .andExpect(jsonPath("$.chatId").value(groupId))
                .andExpect(jsonPath("$.members").isArray())
                .andExpect(jsonPath("$.members[0].userId").value(1))
                .andExpect(jsonPath("$.members[0].userName").value("Leader"))
                .andExpect(jsonPath("$.members[0].role").value("leader"))
                .andExpect(jsonPath("$.members[1].userId").value(2))
                .andExpect(jsonPath("$.members[1].userName").value("Member 1"))
                .andExpect(jsonPath("$.members[1].role").value("member"));

        verify(groupService).getGroupDetail(groupId);
    }

    @Test
    void getGroupByID_NotFound() throws Exception {
        Integer groupId = 999;

        when(groupService.getGroupDetail(groupId))
                .thenThrow(new IllegalArgumentException("Group not found"));

        mockMvc.perform(get("/api/groups/byId/{groupId}", groupId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Group not found"));

        verify(groupService).getGroupDetail(groupId);
    }

    @Test
    void getGroupByName_Success() throws Exception {
        String groupName = "Test Group";
        List<Group> expectedGroups = new ArrayList<>();
        
        Group group1 = new Group();
        group1.setGroupId(1);
        group1.setGroupName(groupName);
        group1.setDescription("This is the first test group");
        group1.setCreationTime(Instant.now());
        group1.setChatId(1);
        expectedGroups.add(group1);
        
        Group group2 = new Group();
        group2.setGroupId(2);
        group2.setGroupName(groupName);
        group2.setDescription("This is the second test group");
        group2.setCreationTime(Instant.now());
        group2.setChatId(2);
        expectedGroups.add(group2);

        when(groupService.getGroupByName(groupName)).thenReturn(expectedGroups);

        mockMvc.perform(get("/api/groups/byName/{groupName}", groupName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].groupId").value(1))
                .andExpect(jsonPath("$[0].groupName").value(groupName))
                .andExpect(jsonPath("$[0].description").value("This is the first test group"))
                .andExpect(jsonPath("$[0].creationTime").exists())
                .andExpect(jsonPath("$[0].chatId").value(1))
                .andExpect(jsonPath("$[1].groupId").value(2))
                .andExpect(jsonPath("$[1].groupName").value(groupName))
                .andExpect(jsonPath("$[1].description").value("This is the second test group"))
                .andExpect(jsonPath("$[1].creationTime").exists())
                .andExpect(jsonPath("$[1].chatId").value(2));

        verify(groupService).getGroupByName(groupName);
    }

    @Test
    void getGroupByName_NoResults() throws Exception {
        String groupName = "Non-existent Group";
        List<Group> emptyList = new ArrayList<>();

        when(groupService.getGroupByName(groupName)).thenReturn(emptyList);

        mockMvc.perform(get("/api/groups/byName/{groupName}", groupName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(groupService).getGroupByName(groupName);
    }

    @Test
    void getGroupByName_EmptyName() throws Exception {
        List<Group> expectedGroups = new ArrayList<>();
        
        when(groupService.getGroupByName("empty")).thenReturn(expectedGroups);
        
        mockMvc.perform(get("/api/groups/byName/empty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        
        verify(groupService).getGroupByName("empty");
    }

    @Test
    void deleteGroupMember_Success() throws Exception {
        Integer userId = 1;
        Integer groupId = 1;
        ResultMsg expectedResult = ResultMsg.success("Successfully quit group");

        when(groupMemberService.quitGroup(userId, groupId)).thenReturn(expectedResult);

        mockMvc.perform(delete("/api/groups/members")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(1))
                .andExpect(jsonPath("$.msg").value("Successfully quit group"));

        verify(groupMemberService).quitGroup(userId, groupId);
    }

    @Test
    void deleteGroupMember_NotMember() throws Exception {
        Integer userId = 1;
        Integer groupId = 1;

        when(groupMemberService.quitGroup(userId, groupId))
                .thenThrow(new IllegalArgumentException("You are not a member of this group"));

        mockMvc.perform(delete("/api/groups/members")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("You are not a member of this group"));

        verify(groupMemberService).quitGroup(userId, groupId);
    }

    @Test
    void deleteGroupMember_GroupNotFound() throws Exception {
        Integer userId = 1;
        Integer groupId = 999;

        when(groupMemberService.quitGroup(userId, groupId))
                .thenThrow(new IllegalArgumentException("Group not found"));

        mockMvc.perform(delete("/api/groups/members")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Group not found"));

        verify(groupMemberService).quitGroup(userId, groupId);
    }

    @Test
    void deleteGroupMember_LastLeader() throws Exception {
        Integer userId = 1;
        Integer groupId = 1;

        when(groupMemberService.quitGroup(userId, groupId))
                .thenThrow(new IllegalArgumentException("Leader cannot quit group, please transfer leadership or dissolve group"));

        mockMvc.perform(delete("/api/groups/members")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Leader cannot quit group, please transfer leadership or dissolve group"));

        verify(groupMemberService).quitGroup(userId, groupId);
    }

    @Test
    void removeGroupMember_Success() throws Exception {
        Integer userId = 1;
        Integer groupId = 1;
        Integer memberId = 2;
        ResultMsg expectedResult = ResultMsg.success("Member removed successfully");

        when(groupMemberService.dropMember(groupId, memberId, userId)).thenReturn(expectedResult);

        mockMvc.perform(delete("/api/groups/members/by")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString())
                .param("memberId", memberId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(1))
                .andExpect(jsonPath("$.msg").value("Member removed successfully"));

        verify(groupMemberService).dropMember(groupId, memberId, userId);
    }

    @Test
    void removeGroupMember_NoPermission() throws Exception {
        Integer userId = 1;
        Integer groupId = 1;
        Integer memberId = 2;

        when(groupMemberService.dropMember(groupId, memberId, userId))
                .thenThrow(new IllegalArgumentException("No permission to remove member"));

        mockMvc.perform(delete("/api/groups/members/by")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString())
                .param("memberId", memberId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("No permission to remove member"));

        verify(groupMemberService).dropMember(groupId, memberId, userId);
    }

    @Test
    void removeGroupMember_GroupNotFound() throws Exception {
        Integer userId = 1;
        Integer groupId = 999;
        Integer memberId = 2;

        when(groupMemberService.dropMember(groupId, memberId, userId))
                .thenThrow(new IllegalArgumentException("Group not found"));

        mockMvc.perform(delete("/api/groups/members/by")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString())
                .param("memberId", memberId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Group not found"));

        verify(groupMemberService).dropMember(groupId, memberId, userId);
    }

    @Test
    void removeGroupMember_MemberNotFound() throws Exception {
        Integer userId = 1;
        Integer groupId = 1;
        Integer memberId = 999;

        when(groupMemberService.dropMember(groupId, memberId, userId))
                .thenThrow(new IllegalArgumentException("Member not found"));

        mockMvc.perform(delete("/api/groups/members/by")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString())
                .param("memberId", memberId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Member not found"));

        verify(groupMemberService).dropMember(groupId, memberId, userId);
    }

    @Test
    void removeGroupMember_CannotRemoveLeader() throws Exception {
        Integer userId = 1;
        Integer groupId = 1;
        Integer memberId = 2;

        when(groupMemberService.dropMember(groupId, memberId, userId))
                .thenThrow(new IllegalArgumentException("Cannot remove leader"));

        mockMvc.perform(delete("/api/groups/members/by")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString())
                .param("memberId", memberId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Cannot remove leader"));

        verify(groupMemberService).dropMember(groupId, memberId, userId);
    }

    @Test
    void setGroupMemberRole_Success() throws Exception {
        Integer userId = 1;
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setOperatorId(userId);
        roleDTO.setTargetId(2);
        roleDTO.setGroupId(1);
        roleDTO.setRole(GroupMemberRole.leader);
        
        ResultMsg expectedResult = ResultMsg.success("Member role set successfully");

        when(groupMemberService.setRole(roleDTO, userId)).thenReturn(expectedResult);

        mockMvc.perform(patch("/api/groups/members")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(1))
                .andExpect(jsonPath("$.msg").value("Member role set successfully"));

        verify(groupMemberService).setRole(roleDTO, userId);
    }

    @Test
    void setGroupMemberRole_NoPermission() throws Exception {
        Integer userId = 1;
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setOperatorId(userId);
        roleDTO.setTargetId(2);
        roleDTO.setGroupId(1);
        roleDTO.setRole(GroupMemberRole.leader);

        when(groupMemberService.setRole(roleDTO, userId))
                .thenThrow(new IllegalArgumentException("No permission to set member role"));

        mockMvc.perform(patch("/api/groups/members")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("No permission to set member role"));

        verify(groupMemberService).setRole(roleDTO, userId);
    }

    @Test
    void setGroupMemberRole_GroupNotFound() throws Exception {
        Integer userId = 1;
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setOperatorId(userId);
        roleDTO.setTargetId(2);
        roleDTO.setGroupId(999);
        roleDTO.setRole(GroupMemberRole.leader);

        when(groupMemberService.setRole(roleDTO, userId))
                .thenThrow(new IllegalArgumentException("Group not found"));

        mockMvc.perform(patch("/api/groups/members")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Group not found"));

        verify(groupMemberService).setRole(roleDTO, userId);
    }

    @Test
    void setGroupMemberRole_MemberNotFound() throws Exception {
        Integer userId = 1;
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setOperatorId(userId);
        roleDTO.setTargetId(999);
        roleDTO.setGroupId(1);
        roleDTO.setRole(GroupMemberRole.leader);

        when(groupMemberService.setRole(roleDTO, userId))
                .thenThrow(new IllegalArgumentException("Member not found"));

        mockMvc.perform(patch("/api/groups/members")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Member not found"));

        verify(groupMemberService).setRole(roleDTO, userId);
    }

    @Test
    void setGroupMemberRole_InvalidRole() throws Exception {
        Integer userId = 1;
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setOperatorId(userId);
        roleDTO.setTargetId(2);
        roleDTO.setGroupId(1);
        roleDTO.setRole(null);

        when(groupMemberService.setRole(roleDTO, userId))
                .thenThrow(new IllegalArgumentException("Invalid role type"));

        mockMvc.perform(patch("/api/groups/members")
                .requestAttr("idFromToken", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Invalid role type"));

        verify(groupMemberService).setRole(roleDTO, userId);
    }

    @Test
    void getGroupRecords_Success() throws Exception {
        Integer userId = 1;
        Integer groupId = 1;
        Integer targetId = 2;
        List<GroupRecord> expectedRecords = new ArrayList<>();
        
        GroupRecord record1 = new GroupRecord();
        record1.setGroupRecordId(1);
        record1.setOperatorId(userId);
        record1.setTargetId(targetId);
        record1.setGroupId(groupId);
        record1.setTime(Instant.now());
        record1.setOperateType("join_group");
        expectedRecords.add(record1);
        
        GroupRecord record2 = new GroupRecord();
        record2.setGroupRecordId(2);
        record2.setOperatorId(userId);
        record2.setTargetId(targetId);
        record2.setGroupId(groupId);
        record2.setTime(Instant.now());
        record2.setOperateType("quit_group");
        expectedRecords.add(record2);

        when(groupRecordService.getRecord(userId, targetId, groupId)).thenReturn(expectedRecords);

        mockMvc.perform(get("/api/groups/records")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString())
                .param("targetId", targetId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].groupRecordId").value(1))
                .andExpect(jsonPath("$[0].operatorId").value(userId))
                .andExpect(jsonPath("$[0].targetId").value(targetId))
                .andExpect(jsonPath("$[0].groupId").value(groupId))
                .andExpect(jsonPath("$[0].time").exists())
                .andExpect(jsonPath("$[0].operateType").value("join_group"))
                .andExpect(jsonPath("$[1].groupRecordId").value(2))
                .andExpect(jsonPath("$[1].operatorId").value(userId))
                .andExpect(jsonPath("$[1].targetId").value(targetId))
                .andExpect(jsonPath("$[1].groupId").value(groupId))
                .andExpect(jsonPath("$[1].time").exists())
                .andExpect(jsonPath("$[1].operateType").value("quit_group"));

        verify(groupRecordService).getRecord(userId, targetId, groupId);
    }

    @Test
    void getGroupRecords_NoRecords() throws Exception {
        Integer userId = 1;
        Integer groupId = 1;
        Integer targetId = 2;
        List<GroupRecord> emptyList = new ArrayList<>();

        when(groupRecordService.getRecord(userId, targetId, groupId)).thenReturn(emptyList);

        mockMvc.perform(get("/api/groups/records")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString())
                .param("targetId", targetId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(groupRecordService).getRecord(userId, targetId, groupId);
    }

    @Test
    void getGroupRecords_GroupNotFound() throws Exception {
        Integer userId = 1;
        Integer groupId = 999;
        Integer targetId = 2;

        when(groupRecordService.getRecord(userId, targetId, groupId))
                .thenThrow(new IllegalArgumentException("Group not found"));

        mockMvc.perform(get("/api/groups/records")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString())
                .param("targetId", targetId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("Group not found"));

        verify(groupRecordService).getRecord(userId, targetId, groupId);
    }

    @Test
    void getGroupRecords_NoPermission() throws Exception {
        Integer userId = 1;
        Integer groupId = 1;
        Integer targetId = 2;

        when(groupRecordService.getRecord(userId, targetId, groupId))
                .thenThrow(new IllegalArgumentException("No permission to view group records"));

        mockMvc.perform(get("/api/groups/records")
                .requestAttr("idFromToken", userId)
                .param("groupId", groupId.toString())
                .param("targetId", targetId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state").value(400))
                .andExpect(jsonPath("$.msg").value("No permission to view group records"));

        verify(groupRecordService).getRecord(userId, targetId, groupId);
    }

}

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultMsg handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResultMsg(e.getMessage(), 400);
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultMsg handleServiceException(ServiceException e) {
        return new ResultMsg(e.getMessage(), e.getCode());
    }
}
