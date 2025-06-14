package com.tongji.sportmanagement.AccountSubsystem.Service;

import com.tongji.sportmanagement.AccountSubsystem.DTO.*;
import com.tongji.sportmanagement.AccountSubsystem.Entity.*;
import com.tongji.sportmanagement.AccountSubsystem.Repository.UserRepository;
import com.tongji.sportmanagement.AccountSubsystem.Repository.NotificationRepository;
import com.tongji.sportmanagement.Common.DTO.ResultMsg;
import com.tongji.sportmanagement.Common.DTO.UserProfileDTO;
import com.tongji.sportmanagement.Common.OssService;
import com.tongji.sportmanagement.Common.Security.JwtTokenProvider;
import com.tongji.sportmanagement.Common.ServiceException;
import com.tongji.sportmanagement.ReservationSubsystem.Service.ViolationService;
import com.tongji.sportmanagement.VenueSubsystem.Service.VenueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private OssService ossService;

    @Mock
    private VenueService venueService;

    @Mock
    private ViolationService violationService;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_Success() throws Exception {
        // Prepare test data
        String userName = "testUser";
        String password = "testPassword";
        Integer userId = 1;
        String userAvatar = "http://example.com/avatar.jpg";
        String token = "test.jwt.token";
        Instant expirationTime = Instant.now().plusSeconds(3600);

        User user = new User();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setPassword(password);
        user.setUserType(UserType.user);

        // Configure mock behavior
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(user));
        when(ossService.getFileLink("avatar_" + userId)).thenReturn(userAvatar);
        when(jwtTokenProvider.generateToken(userId)).thenReturn(token);
        when(jwtTokenProvider.getExpiryDate()).thenReturn(expirationTime);

        // Execute test
        LoginResponseDTO response = userService.login(userName, password);

        // Verify results
        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals(expirationTime, response.getExpiration_time());
        assertEquals(userId, response.getUserId());
        assertEquals(userName, response.getUserName());
        assertEquals(userAvatar, response.getUserAvatar());
        assertEquals(UserType.user, response.getUserType());

        // Verify mock interactions
        verify(userRepository).findByUserName(userName);
        verify(ossService).getFileLink("avatar_" + userId);
        verify(jwtTokenProvider).generateToken(userId);
        verify(jwtTokenProvider).getExpiryDate();
    }

    @Test
    void login_UserNotFound() {
        // Prepare test data
        String userName = "nonExistentUser";
        String password = "testPassword";

        // Configure mock behavior
        when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());

        // Execute test and verify exception
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.login(userName, password);
        });

        assertEquals(400, exception.getCode());
        assertEquals("用户名或密码错误", exception.getMessage());

        // Verify mock interactions
        verify(userRepository).findByUserName(userName);
        verify(ossService, never()).getFileLink(anyString());
        verify(jwtTokenProvider, never()).generateToken(anyInt());
    }

    @Test
    void login_WrongPassword() {
        // Prepare test data
        String userName = "testUser";
        String correctPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        Integer userId = 1;

        User user = new User();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setPassword(correctPassword);

        // Configure mock behavior
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(user));

        // Execute test and verify exception
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.login(userName, wrongPassword);
        });

        assertEquals(400, exception.getCode());
        assertEquals("用户名或密码错误", exception.getMessage());

        // Verify mock interactions
        verify(userRepository).findByUserName(userName);
        verify(ossService, never()).getFileLink(anyString());
        verify(jwtTokenProvider, never()).generateToken(anyInt());
    }

    @Test
    void register_Success_RegularUser() throws Exception {
        // Prepare test data
        RegisterRequestDTO requestDTO = new RegisterRequestDTO();
        requestDTO.setUserName("newUser");
        requestDTO.setPassword("password123");
        requestDTO.setPhone("1234567890");
        requestDTO.setRealName("Test User");
        requestDTO.setPhoto("default.jpg");
        requestDTO.setUserType(UserType.user);

        // Configure mock behavior
        when(userRepository.findByUserName(requestDTO.getUserName())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(1); // Set the ID after save
            return user;
        });
        doNothing().when(violationService).createViolation(anyInt());
        doNothing().when(ossService).copyDefault(eq("default_avatar"), eq("avatar_1"));

        // Execute test
        RegisterResponseDTO response = userService.register(requestDTO);

        // Verify results
        assertNotNull(response);
        assertEquals(1, response.getUserId());
        assertEquals("newUser", response.getUserName());

        // Verify mock interactions
        verify(userRepository).findByUserName(requestDTO.getUserName());
        verify(userRepository).save(any(User.class));
        verify(violationService).createViolation(1);
        verify(venueService, never()).createVenue(anyInt());
        verify(ossService).copyDefault("default_avatar", "avatar_1");
    }

    @Test
    void register_Success_VenueAdmin() throws Exception {
        // Prepare test data
        RegisterRequestDTO requestDTO = new RegisterRequestDTO();
        requestDTO.setUserName("venueAdmin");
        requestDTO.setPassword("password123");
        requestDTO.setPhone("1234567890");
        requestDTO.setRealName("Admin User");
        requestDTO.setPhoto("admin.jpg");
        requestDTO.setUserType(UserType.venueadmin);

        // Configure mock behavior
        when(userRepository.findByUserName(requestDTO.getUserName())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(1); // Set the ID after save
            return user;
        });
        doNothing().when(venueService).createVenue(anyInt());
        doNothing().when(ossService).copyDefault(eq("default_avatar"), eq("avatar_1"));

        // Execute test
        RegisterResponseDTO response = userService.register(requestDTO);

        // Verify results
        assertNotNull(response);
        assertEquals(1, response.getUserId());
        assertEquals("venueAdmin", response.getUserName());

        // Verify mock interactions
        verify(userRepository).findByUserName(requestDTO.getUserName());
        verify(userRepository).save(any(User.class));
        verify(venueService).createVenue(1);
        verify(violationService, never()).createViolation(anyInt());
        verify(ossService).copyDefault("default_avatar", "avatar_1");
    }

    @Test
    void register_UserAlreadyExists() {
        // Prepare test data
        RegisterRequestDTO requestDTO = new RegisterRequestDTO();
        requestDTO.setUserName("existingUser");
        requestDTO.setPassword("password123");
        requestDTO.setPhone("1234567890");
        requestDTO.setRealName("Existing User");
        requestDTO.setPhoto("existing.jpg");
        requestDTO.setUserType(UserType.user);

        User existingUser = new User();
        existingUser.setUserId(1);
        existingUser.setUserName(requestDTO.getUserName());

        // Configure mock behavior
        when(userRepository.findByUserName(requestDTO.getUserName())).thenReturn(Optional.of(existingUser));

        // Execute test and verify exception
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.register(requestDTO);
        });

        assertEquals(422, exception.getCode());
        assertEquals("该用户已存在", exception.getMessage());

        // Verify mock interactions
        verify(userRepository).findByUserName(requestDTO.getUserName());
        verify(userRepository, never()).save(any(User.class));
        verify(violationService, never()).createViolation(anyInt());
        verify(venueService, never()).createVenue(anyInt());
        verify(ossService, never()).copyDefault(anyString(), anyString());
    }

    @Test
    void getUserList_Success() throws Exception {
        User user = new User();
        user.setUserId(1);
        user.setUserName("testUser");
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(ossService.getFileLink("avatar_1")).thenReturn("http://example.com/avatar.jpg");
        
        List<UserInfoDetailDTO> result = userService.getUserList();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUserId());
        assertEquals("testUser", result.get(0).getUserName());
        assertEquals("http://example.com/avatar.jpg", result.get(0).getPhoto());
        
        verify(userRepository).findAll();
        verify(ossService).getFileLink("avatar_1");
    }

    @Test
    void getUserList_EmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.getUserList();
        });
        
        assertEquals(400, exception.getCode());
        assertEquals("用户列表为空", exception.getMessage());
        
        verify(userRepository).findAll();
        verify(ossService, never()).getFileLink(anyString());
    }

    @Test
    void getUsersByName_Success() throws Exception {
        String searchName = "test";
        User user1 = new User();
        user1.setUserId(1);
        user1.setUserName("testUser1");
        user1.setPhone("1234567890");
        user1.setRealName("Test User One");
        user1.setUserType(UserType.user);
        user1.setRegistrationDate(Instant.now().plus(Duration.ofHours(8)));

        User user2 = new User();
        user2.setUserId(2);
        user2.setUserName("testUser2");
        user2.setPhone("0987654321");
        user2.setRealName("Test User Two");
        user2.setUserType(UserType.venueadmin);
        user2.setRegistrationDate(Instant.now().plus(Duration.ofHours(8)));

        when(userRepository.findUsersByName(searchName)).thenReturn(List.of(user1, user2));

        List<UserInfoDetailDTO> result = userService.getUsersByName(searchName);

        assertNotNull(result);
        assertEquals(2, result.size());

        UserInfoDetailDTO firstUser = result.get(0);
        assertEquals(1, firstUser.getUserId());
        assertEquals("testUser1", firstUser.getUserName());
        assertEquals("1234567890", firstUser.getPhone());
        assertEquals("Test User One", firstUser.getRealName());
        assertEquals(UserType.user, firstUser.getUserType());

        UserInfoDetailDTO secondUser = result.get(1);
        assertEquals(2, secondUser.getUserId());
        assertEquals("testUser2", secondUser.getUserName());
        assertEquals("0987654321", secondUser.getPhone());
        assertEquals("Test User Two", secondUser.getRealName());
        assertEquals(UserType.venueadmin, secondUser.getUserType());

        verify(userRepository).findUsersByName(searchName);
    }

    @Test
    void getUsersByName_NoUsersFound() {
        String searchName = "nonexistent";

        when(userRepository.findUsersByName(searchName)).thenReturn(List.of());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.getUsersByName(searchName);
        });

        assertEquals(400, exception.getCode());
        assertEquals("未找到用户", exception.getMessage());

        verify(userRepository).findUsersByName(searchName);
    }

    @Test
    void getUserInfo_Success() throws Exception {
        Integer userId = 1;
        User user = new User();
        user.setUserId(userId);
        user.setUserName("testUser");
        user.setPhone("1234567890");
        user.setRealName("Test User");
        user.setUserType(UserType.user);
        user.setRegistrationDate(Instant.now().plus(Duration.ofHours(8)));

        String avatarUrl = "http://example.com/avatar.jpg";

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(ossService.getFileLink("avatar_" + userId)).thenReturn(avatarUrl);

        UserInfoDetailDTO result = userService.getUserInfo(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("testUser", result.getUserName());
        assertEquals("1234567890", result.getPhone());
        assertEquals("Test User", result.getRealName());
        assertEquals(UserType.user, result.getUserType());
        assertEquals(avatarUrl, result.getPhoto());

        verify(userRepository).findByUserId(userId);
        verify(ossService).getFileLink("avatar_" + userId);
    }

    @Test
    void getUserInfo_UserNotFound() {
        Integer userId = 999;

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.getUserInfo(userId);
        });

        assertEquals(404, exception.getCode());
        assertEquals("未查找到该用户", exception.getMessage());

        verify(userRepository).findByUserId(userId);
        verify(ossService, never()).getFileLink(anyString());
    }

    @Test
    void getUserProfile_Success() {
        Integer userId = 1;
        User user = new User();
        user.setUserId(userId);
        user.setUserName("testUser");
        user.setPhone("1234567890");
        user.setRealName("Test User");
        user.setUserType(UserType.user);
        user.setRegistrationDate(Instant.now().plus(Duration.ofHours(8)));

        String avatarUrl = "http://example.com/avatar.jpg";

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(ossService.getFileLink("avatar_" + userId)).thenReturn(avatarUrl);

        UserProfileDTO result = userService.getUserProfile(userId);

        assertNotNull(result);
        assertEquals("testUser", result.getUserName());
        assertEquals(avatarUrl, result.getPhoto());

        verify(userRepository).findByUserId(userId);
        verify(ossService).getFileLink("avatar_" + userId);
    }

    @Test
    void getUserProfile_UserNotFound() {
        Integer userId = 999;

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        UserProfileDTO result = userService.getUserProfile(userId);

        assertNull(result);

        verify(userRepository).findByUserId(userId);
        verify(ossService, never()).getFileLink(anyString());
    }

    @Test
    void getUserPhoto_Success() {
        Integer userId = 1;
        String avatarUrl = "http://example.com/avatar.jpg";

        when(ossService.getFileLink("avatar_" + userId)).thenReturn(avatarUrl);

        String result = userService.getUserPhoto(userId);

        assertEquals(avatarUrl, result);

        verify(ossService).getFileLink("avatar_" + userId);
    }

    @Test
    void updateUserInfo_Success() throws Exception {
        Integer userId = 1;
        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setUserName("oldUser");
        existingUser.setPhone("1234567890");
        existingUser.setRealName("Old User");
        existingUser.setUserType(UserType.user);
        existingUser.setRegistrationDate(Instant.now().plus(Duration.ofHours(8)));

        UserInfoUpdateDTO updateDTO = new UserInfoUpdateDTO();
        updateDTO.setUserName("newUser");
        updateDTO.setPhone("0987654321");
        updateDTO.setRealName("New User");
        updateDTO.setPhoto("new_avatar.jpg");

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return user;
        });

        UserInfoDetailDTO result = userService.updateUserInfo(userId, updateDTO);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("newUser", result.getUserName());
        assertEquals("0987654321", result.getPhone());
        assertEquals("New User", result.getRealName());
        assertEquals(UserType.user, result.getUserType());
        assertEquals(null, result.getPhoto());

        verify(userRepository).findByUserId(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserInfo_UserNotFound() {
        Integer userId = 999;
        UserInfoUpdateDTO updateDTO = new UserInfoUpdateDTO();
        updateDTO.setUserName("newUser");
        updateDTO.setPhone("0987654321");
        updateDTO.setRealName("New User");
        updateDTO.setPhoto("new_avatar.jpg");

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.updateUserInfo(userId, updateDTO);
        });

        assertEquals(400, exception.getCode());
        assertEquals("未查找到该用户", exception.getMessage());

        verify(userRepository).findByUserId(userId);
        verify(userRepository, never()).save(any(User.class));
        verify(ossService, never()).getFileLink(anyString());
    }

    @Test
    void updateUserPwd_Success() throws Exception {
        Integer userId = 1;
        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setUserName("testUser");
        existingUser.setPassword("oldPassword");

        UpdatePwdDTO updatePwdDTO = new UpdatePwdDTO();
        updatePwdDTO.setOldPwd("oldPassword");
        updatePwdDTO.setNewPwd("newPassword");

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return user;
        });

        IdResponseDTO result = userService.updateUserPwd(userId, updatePwdDTO);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());

        verify(userRepository).findByUserId(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserPwd_UserNotFound() {
        Integer userId = 999;
        UpdatePwdDTO updatePwdDTO = new UpdatePwdDTO();
        updatePwdDTO.setOldPwd("oldPassword");
        updatePwdDTO.setNewPwd("newPassword");

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.updateUserPwd(userId, updatePwdDTO);
        });

        assertEquals(400, exception.getCode());
        assertEquals("未查找到该用户", exception.getMessage());

        verify(userRepository).findByUserId(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserPwd_WrongOldPassword() {
        Integer userId = 1;
        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setUserName("testUser");
        existingUser.setPassword("correctPassword");

        UpdatePwdDTO updatePwdDTO = new UpdatePwdDTO();
        updatePwdDTO.setOldPwd("wrongPassword");
        updatePwdDTO.setNewPwd("newPassword");

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(existingUser));

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.updateUserPwd(userId, updatePwdDTO);
        });

        assertEquals(400, exception.getCode());
        assertEquals("用户密码错误", exception.getMessage());

        verify(userRepository).findByUserId(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserNotification_Success() throws Exception {
        Integer userId = 1;
        List<Notification> notifications = new ArrayList<>();
        
        Notification notification1 = new Notification();
        notification1.setNotificationId(1);
        notification1.setUserId(userId);
        notification1.setType(NotificationType.system);
        notification1.setTitle("系统通知");
        notification1.setContent("测试通知1");
        notification1.setTimestamp(Instant.now().plus(Duration.ofHours(8)));
        notification1.setState(NotificationState.unread);
        
        Notification notification2 = new Notification();
        notification2.setNotificationId(2);
        notification2.setUserId(userId);
        notification2.setType(NotificationType.reservation);
        notification2.setTitle("预约通知");
        notification2.setContent("测试通知2");
        notification2.setTimestamp(Instant.now().plus(Duration.ofHours(8)));
        notification2.setState(NotificationState.read);
        
        notifications.add(notification1);
        notifications.add(notification2);

        when(notificationRepository.findAllByUserId(userId)).thenReturn(notifications);

        List<NotificationDetailDTO> result = userService.getUserNotification(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        
        NotificationDetailDTO firstNotification = result.get(0);
        assertEquals(1, firstNotification.getNotificationId());
        assertEquals(NotificationType.system, firstNotification.getType());
        assertEquals("系统通知", firstNotification.getTitle());
        assertEquals("测试通知1", firstNotification.getContent());
        assertEquals(NotificationState.unread, firstNotification.getState());
        
        NotificationDetailDTO secondNotification = result.get(1);
        assertEquals(2, secondNotification.getNotificationId());
        assertEquals(NotificationType.reservation, secondNotification.getType());
        assertEquals("预约通知", secondNotification.getTitle());
        assertEquals("测试通知2", secondNotification.getContent());
        assertEquals(NotificationState.read, secondNotification.getState());

        verify(notificationRepository).findAllByUserId(userId);
    }

    @Test
    void getUserNotification_EmptyList() throws Exception {
        Integer userId = 1;

        when(notificationRepository.findAllByUserId(userId)).thenReturn(new ArrayList<>());

        List<NotificationDetailDTO> result = userService.getUserNotification(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(notificationRepository).findAllByUserId(userId);
    }

    @Test
    void sendUserNotification_Success() {
        NotificationContentDTO notificationContentDTO = new NotificationContentDTO();
        notificationContentDTO.setUserId(1);
        notificationContentDTO.setType(NotificationType.system);
        notificationContentDTO.setTitle("系统通知");
        notificationContentDTO.setContent("测试通知内容");

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setNotificationId(1);
            return notification;
        });

        ResultMsg result = userService.sendUserNotification(notificationContentDTO);

        assertNotNull(result);
        assertEquals("消息发送成功", result.getMsg());
        assertEquals(1, result.getState());

        verify(notificationRepository).save(argThat(notification -> {
            assertEquals(1, notification.getUserId());
            assertEquals(NotificationType.system, notification.getType());
            assertEquals("系统通知", notification.getTitle());
            assertEquals("测试通知内容", notification.getContent());
            assertEquals(NotificationState.unread, notification.getState());
            return true;
        }));
    }

    @Test
    void editUserNotification_Success() {
        NotificationOperationDTO operationDTO = new NotificationOperationDTO();
        operationDTO.setNotificationId(1);
        operationDTO.setOperation("read");

        Notification notification = new Notification();
        notification.setNotificationId(1);
        notification.setUserId(1);
        notification.setType(NotificationType.system);
        notification.setTitle("系统通知");
        notification.setContent("测试通知内容");
        notification.setState(NotificationState.unread);

        when(notificationRepository.findByNotificationId(operationDTO.getNotificationId()))
            .thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Object> response = userService.editUserNotification(operationDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResultMsg result = (ResultMsg) response.getBody();
        assertNotNull(result);
        assertEquals("通知状态修改成功", result.getMsg());
        assertEquals(1, result.getState());

        verify(notificationRepository).findByNotificationId(operationDTO.getNotificationId());
        verify(notificationRepository).save(argThat(savedNotification -> {
            assertEquals(operationDTO.getNotificationId(), savedNotification.getNotificationId());
            assertEquals(NotificationState.read, savedNotification.getState());
            return true;
        }));
    }

    @Test
    void editUserNotification_NotFound() {
        NotificationOperationDTO operationDTO = new NotificationOperationDTO();
        operationDTO.setNotificationId(999);
        operationDTO.setOperation("read");

        when(notificationRepository.findByNotificationId(operationDTO.getNotificationId()))
            .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            userService.editUserNotification(operationDTO);
        });

        verify(notificationRepository).findByNotificationId(operationDTO.getNotificationId());
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void updateUserAvatar_Success() throws Exception {
        Integer userId = 1;
        String avatarUrl = "http://example.com/avatar.jpg";
        String avatarName = "avatar_" + userId;

        MultipartFile mockFile = mock(MultipartFile.class);
        InputStream mockInputStream = mock(InputStream.class);
        when(mockFile.getInputStream()).thenReturn(mockInputStream);

        doNothing().when(ossService).deleteFile(avatarName);
        doNothing().when(ossService).uploadFile(mockInputStream, avatarName);
        when(ossService.getFileLink(avatarName)).thenReturn(avatarUrl);

        ResultMsg result = userService.updateUserAvatar(userId, mockFile);

        assertNotNull(result);
        assertEquals(avatarUrl, result.getMsg());
        assertEquals(1, result.getState());

        verify(ossService).deleteFile(avatarName);
        verify(ossService).uploadFile(mockInputStream, avatarName);
        verify(ossService).getFileLink(avatarName);
    }

    @Test
    void updateUserAvatar_UploadError() throws Exception {
        Integer userId = 1;
        String avatarName = "avatar_" + userId;

        MultipartFile mockFile = mock(MultipartFile.class);
        InputStream mockInputStream = mock(InputStream.class);
        when(mockFile.getInputStream()).thenReturn(mockInputStream);

        doThrow(new RuntimeException("Upload failed")).when(ossService).uploadFile(mockInputStream, avatarName);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.updateUserAvatar(userId, mockFile);
        });

        assertEquals("Upload failed", exception.getMessage());

        verify(ossService).deleteFile(avatarName);
        verify(ossService).uploadFile(mockInputStream, avatarName);
        verify(ossService, never()).getFileLink(anyString());
    }

    @Test
    void isUserAdmin_True() {
        Integer userId = 1;
        User adminUser = new User();
        adminUser.setUserId(userId);
        adminUser.setUserName("adminUser");
        adminUser.setUserType(UserType.venueadmin);

        when(userRepository.findById(userId)).thenReturn(Optional.of(adminUser));

        Boolean result = userService.isUserAdmin(userId);

        assertTrue(result);

        verify(userRepository).findById(userId);
    }

    @Test
    void isUserAdmin_False() {
        Integer userId = 1;
        User regularUser = new User();
        regularUser.setUserId(userId);
        regularUser.setUserName("regularUser");
        regularUser.setUserType(UserType.user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(regularUser));

        Boolean result = userService.isUserAdmin(userId);

        assertFalse(result);

        verify(userRepository).findById(userId);
    }

    @Test
    void isUserAdmin_UserNotFound() {
        Integer userId = 999;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Boolean result = userService.isUserAdmin(userId);

        assertFalse(result);

        verify(userRepository).findById(userId);
    }
}
