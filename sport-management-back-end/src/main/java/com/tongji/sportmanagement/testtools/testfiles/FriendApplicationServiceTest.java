package com.tongji.sportmanagement.testtools.testfiles;

import com.tongji.sportmanagement.AccountSubsystem.Service.UserService;
import com.tongji.sportmanagement.Common.DTO.UserProfileDTO;
import com.tongji.sportmanagement.SocializeSubsystem.DTO.ApplicationResponseDTO;
import com.tongji.sportmanagement.SocializeSubsystem.Entity.FriendApplication;
import com.tongji.sportmanagement.SocializeSubsystem.Entity.FriendApplicationState;
import com.tongji.sportmanagement.SocializeSubsystem.Repository.FriendApplicationRepository;
import com.tongji.sportmanagement.SocializeSubsystem.Service.FriendApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FriendApplicationServiceTest {
    @Mock
    private FriendApplicationRepository friendApplicationRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FriendApplicationService friendApplicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllFriendApplication_WithMultipleApplications_ShouldReturnCorrectList() {
        // 准备测试数据
        Integer userId = 1;
        Instant now = Instant.now();

        FriendApplication app1 = new FriendApplication();
        app1.setFriendApplicationId(1);
        app1.setApplicantId(2);
        app1.setReviewerId(userId);
        app1.setState(FriendApplicationState.waiting);
        app1.setOperationTime(now);
        app1.setExpirationTime(now.plusSeconds(3600));
        app1.setApplyInfo("Hello, let's be friends!");

        FriendApplication app2 = new FriendApplication();
        app2.setFriendApplicationId(2);
        app2.setApplicantId(3);
        app2.setReviewerId(userId);
        app2.setState(FriendApplicationState.waiting);
        app2.setOperationTime(now.minusSeconds(3600));
        app2.setExpirationTime(now.plusSeconds(3600));
        app2.setApplyInfo("I want to add you as friend");

        when(friendApplicationRepository.findFriendApplicationsByReviewerId(userId))
                .thenReturn(Arrays.asList(app1, app2));

        UserProfileDTO applicant1 = new UserProfileDTO();
        applicant1.setUserName("User2");
        when(userService.getUserProfile(2)).thenReturn(applicant1);

        UserProfileDTO applicant2 = new UserProfileDTO();
        applicant2.setUserName("User3");
        when(userService.getUserProfile(3)).thenReturn(applicant2);

        UserProfileDTO reviewer = new UserProfileDTO();
        reviewer.setUserName("TestUser");
        when(userService.getUserProfile(userId)).thenReturn(reviewer);

        // 执行测试
        List<ApplicationResponseDTO> result = friendApplicationService.getAllFriendApplication(userId);

        // 验证结果
        assertEquals(2, result.size());

        // 验证第一个申请
        ApplicationResponseDTO dto1 = result.get(0);
        assertEquals(1, dto1.getFriendApplicationId());
        assertEquals(2, dto1.getApplicantId());
        assertEquals(userId, dto1.getReviewerId());
        assertEquals(FriendApplicationState.waiting, dto1.getState());
        assertEquals(now, dto1.getOperationTime());
        assertEquals("Hello, let's be friends!", dto1.getApplyInfo());
        assertEquals("User2", dto1.getApplicantName());
        assertEquals("TestUser", dto1.getReviewerName());

        // 验证第二个申请
        ApplicationResponseDTO dto2 = result.get(1);
        assertEquals(2, dto2.getFriendApplicationId());
        assertEquals(3, dto2.getApplicantId());
    }

    @Test
    public void getAllFriendApplication_WithNoApplications_ShouldReturnEmptyList() {
        // 准备测试数据
        Integer userId = 1;
        when(friendApplicationRepository.findFriendApplicationsByReviewerId(userId))
                .thenReturn(Collections.emptyList());

        // 执行测试
        List<ApplicationResponseDTO> result = friendApplicationService.getAllFriendApplication(userId);

        // 验证结果
        assertTrue(result.isEmpty());
    }

    @Test
    public void getAllFriendApplication_ShouldCorrectlyMapAllFields() {
        // 准备测试数据
        Integer userId = 1;
        Instant now = Instant.now();

        FriendApplication app = new FriendApplication();
        app.setFriendApplicationId(1);
        app.setApplicantId(2);
        app.setReviewerId(userId);
        app.setState(FriendApplicationState.accepted);
        app.setOperationTime(now);
        app.setExpirationTime(now.plusSeconds(3600));
        app.setApplyInfo("Test application");

        when(friendApplicationRepository.findFriendApplicationsByReviewerId(userId))
                .thenReturn(Collections.singletonList(app));

        UserProfileDTO applicant = new UserProfileDTO();
        applicant.setUserName("TestApplicant");
        when(userService.getUserProfile(2)).thenReturn(applicant);

        UserProfileDTO reviewer = new UserProfileDTO();
        reviewer.setUserName("TestReviewer");
        when(userService.getUserProfile(userId)).thenReturn(reviewer);

        // 执行测试
        List<ApplicationResponseDTO> result = friendApplicationService.getAllFriendApplication(userId);

        // 验证结果
        assertEquals(1, result.size());
        ApplicationResponseDTO dto = result.get(0);

        assertEquals(app.getFriendApplicationId(), dto.getFriendApplicationId());
        assertEquals(app.getApplicantId(), dto.getApplicantId());
        assertEquals(app.getReviewerId(), dto.getReviewerId());
        assertEquals(app.getState(), dto.getState());
        assertEquals(app.getOperationTime(), dto.getOperationTime());
        assertEquals(app.getExpirationTime(), dto.getExpirationTime());
        assertEquals(app.getApplyInfo(), dto.getApplyInfo());
        assertEquals("TestApplicant", dto.getApplicantName());
        assertEquals("TestReviewer", dto.getReviewerName());
    }

    @Test
    public void getAllFriendApplication_ShouldCallUserServiceCorrectly() {
        // 准备测试数据
        Integer userId = 1;
        FriendApplication app = new FriendApplication();
        app.setApplicantId(2);
        app.setReviewerId(userId);

        when(friendApplicationRepository.findFriendApplicationsByReviewerId(userId))
                .thenReturn(Collections.singletonList(app));

        UserProfileDTO applicant = new UserProfileDTO();
        applicant.setUserName("TestUser");
        when(userService.getUserProfile(2)).thenReturn(applicant);

        UserProfileDTO reviewer = new UserProfileDTO();
        when(userService.getUserProfile(userId)).thenReturn(reviewer);

        // 执行测试
        friendApplicationService.getAllFriendApplication(userId);

        // 验证UserService调用
        verify(userService, times(1)).getUserProfile(2); // 申请人
        verify(userService, times(1)).getUserProfile(userId); // 审核人
    }
}
