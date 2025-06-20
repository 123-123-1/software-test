package com.tongji.sportmanagement.testtools;
import org.springframework.web.bind.annotation.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import com.tongji.sportmanagement.SocializeSubsystem.Service.MessageService;
import com.tongji.sportmanagement.SocializeSubsystem.DTO.MessageDTO;
import com.tongji.sportmanagement.SocializeSubsystem.Service.ChatService;
import com.tongji.sportmanagement.AccountSubsystem.DTO.LoginResponseDTO;
import com.tongji.sportmanagement.AccountSubsystem.DTO.UserInfoDetailDTO;
import com.tongji.sportmanagement.AccountSubsystem.Service.UserService;
import com.tongji.sportmanagement.VenueSubsystem.Service.VenueService;
import com.tongji.sportmanagement.ReservationSubsystem.DTO.ReservationUserDTO;
import com.tongji.sportmanagement.ReservationSubsystem.Entity.Reservation;
import com.tongji.sportmanagement.ReservationSubsystem.Service.ReservationService;
import com.tongji.sportmanagement.Common.ServiceException;

@RestController
@RequestMapping("/api/integration-test")
public class IntegrationTestController {
    
    private final MessageService messageService;
    private final UserService userService;
    private final ChatService chatService;
    
    // 测试数据
    private Integer existingUserId;
    private Integer nonExistingUserId = 999999;
    private Integer existingChatId;
    private Integer nonExistingChatId = 999999;
    private Integer existingMessageId;
    private Integer nonExistingMessageId = 999999;
    
    public IntegrationTestController(MessageService messageService, 
                                   UserService userService,
                                   ChatService chatService,
                                   VenueService venueService,
                                   ReservationService reservationService) {
        this.messageService = messageService;
        this.userService = userService;
        this.chatService = chatService;
        initializeTestData();
    }
    
    private void initializeTestData() {
        // 初始化测试数据
        existingUserId = 5;
        existingChatId = 4;
        MessageDTO messageDto = new MessageDTO();
        messageDto.setChatId(existingChatId);
        messageDto.setContent("Test message");
        existingMessageId = 3;
    }
    
    @GetMapping("/run-all")
    public Map<String, Object> runAllIntegrationTests() {
        Map<String, Object> results = new LinkedHashMap<>();
        
        results.put("MessageService", testMessageService());
        results.put("UserService", testUserService());
        results.put("ChatService", testChatService());
        
        return results;
    }

    @GetMapping("/message-service")
    public Map<String, Object> testMessageService() {
        Map<String, Object> results = new LinkedHashMap<>();
               
        // TC_SI_001
        MessageDTO validMessage = new MessageDTO();
        validMessage.setChatId(existingChatId);
        validMessage.setContent("New message");
        results.put("sendMessage_WithValidData", 
            messageService.sendMessage(validMessage, existingUserId) != null);
        
        // TC_SS_001
        results.put("getChatHistory_WithValidIds", 
            !messageService.getChatHistory(existingChatId, existingUserId).isEmpty());
        results.put("getChatHistory_WithNonExistingChatId", 
            messageService.getChatHistory(nonExistingChatId, existingUserId).isEmpty());
        results.put("getChatHistory_WithNonExistingUserId", 
            messageService.getChatHistory(existingChatId, nonExistingUserId).isEmpty());
        
        // TC_SS_002
        results.put("deleteMsg_WithValidIds", 
            messageService.deleteMsg(existingUserId, existingMessageId).isSuccess());
        results.put("deleteMsg_WithNonExistingMessageId", 
            !messageService.deleteMsg(existingUserId, nonExistingMessageId).isSuccess());
        
        return results;
    }
    
    @GetMapping("/user-service")
    public Map<String, Object> testUserService() {
        Map<String, Object> results = new LinkedHashMap<>();
        
         // TC_AS_001: login测试
        try {
            // 测试1: 正常登录
            LoginResponseDTO response = userService.login("testuser", "password");
            results.put("TC_AS_001_login_WithValidCredentials", response.getToken() != null);
        } catch (Exception e) {
            results.put("TC_AS_001_login_WithValidCredentials", false);
            results.put("TC_AS_001_login_WithValidCredentials_error", e.getMessage());
        }
        
        try {
            // 测试2: 错误密码
            userService.login("testuser", "wrongpassword");
            results.put("TC_AS_001_login_WithInvalidPassword", false); // 不应该执行到这里
        } catch (ServiceException e) {
            results.put("TC_AS_001_login_WithInvalidPassword", e.getCode() == 400);
        } catch (Exception e) {
            results.put("TC_AS_001_login_WithInvalidPassword", false);
        }
        
        try {
            // 测试3: 不存在用户
            userService.login("nonexisting", "password");
            results.put("TC_AS_001_login_WithNonExistingUser", false); // 不应该执行到这里
        } catch (ServiceException e) {
            results.put("TC_AS_001_login_WithNonExistingUser", e.getCode() == 400);
        } catch (Exception e) {
            results.put("TC_AS_001_login_WithNonExistingUser", false);
        }
        
        // TC_AS_002: getUserInfo测试
        try {
            // 测试1: 存在的用户
            UserInfoDetailDTO existingUser = userService.getUserInfo(existingUserId);
            results.put("TC_AS_002_getUserInfo_WithExistingUser", 
                existingUser != null && existingUser.getPhoto() != null);
        } catch (ServiceException e) {
            results.put("TC_AS_002_getUserInfo_WithExistingUser", false);
            results.put("TC_AS_002_getUserInfo_WithExistingUser_error", e.getMessage());
        } catch (Exception e) {
            results.put("TC_AS_002_getUserInfo_WithExistingUser", false);
            results.put("TC_AS_002_getUserInfo_WithExistingUser_system_error", e.getMessage());
        }

        try {
            // 测试2: 不存在的用户
            userService.getUserInfo(nonExistingUserId);
            results.put("TC_AS_002_getUserInfo_WithNonExistingUser", false); // 不应执行到这里
        } catch (ServiceException e) {
            results.put("TC_AS_002_getUserInfo_WithNonExistingUser", e.getCode() == 404);
        } catch (Exception e) {
            results.put("TC_AS_002_getUserInfo_WithNonExistingUser", false);
        }
        
        return results;
    }
    
    @GetMapping("/chat-service")
    public Map<String, Object> testChatService() {
        Map<String, Object> results = new LinkedHashMap<>();
 
        // TC_RE_001
        results.put("getFriendsBy_WithExistingUser", 
            chatService.getFriendsBy(existingUserId) != null);
        results.put("getFriendsBy_WithNonExistingUser", 
            chatService.getFriendsBy(nonExistingUserId).isEmpty());
        
        // TC_RE_002
        try {
            chatService.quitGroupChat(existingChatId, existingUserId);
            results.put("quitGroupChat_WithValidIds", true);
        } catch (Exception e) {
            results.put("quitGroupChat_WithValidIds", false);
        }
        
        try {
            chatService.quitGroupChat(nonExistingChatId, existingUserId);
            results.put("quitGroupChat_WithNonExistingChat", false);
        } catch (NotFoundException e) {
            results.put("quitGroupChat_WithNonExistingChat", true);
        }
        
        return results;
    }


    // 异常类定义
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }
}
