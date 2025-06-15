package com.tongji.sportmanagement.SocializeSubsystem.Service;

import com.tongji.sportmanagement.AccountSubsystem.Entity.User;
import com.tongji.sportmanagement.AccountSubsystem.Entity.UserType;
import com.tongji.sportmanagement.AccountSubsystem.Service.UserService;
import com.tongji.sportmanagement.Common.DTO.ResultMsg;
import com.tongji.sportmanagement.Common.DTO.UserProfileDTO;
import com.tongji.sportmanagement.SocializeSubsystem.DTO.MessageDTO;
import com.tongji.sportmanagement.SocializeSubsystem.DTO.MessageUserDTO;
import com.tongji.sportmanagement.SocializeSubsystem.Entity.Message;
import com.tongji.sportmanagement.SocializeSubsystem.Repository.ChatMemberRepository;
import com.tongji.sportmanagement.SocializeSubsystem.Repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatMemberRepository chatMemberRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendMessage_Success() {
        Integer userId = 1;
        Integer chatId = 1;
        String content = "Test message";

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setChatId(chatId);
        messageDTO.setContent(content);

        Message expectedMessage = new Message();
        expectedMessage.setUserId(userId);
        expectedMessage.setChatId(chatId);
        expectedMessage.setContent(content);
        expectedMessage.setTime(Instant.now());

        when(chatMemberRepository.existsChatMemberByChatIdAndUserId(chatId, userId)).thenReturn(true);
        when(messageRepository.save(any(Message.class))).thenReturn(expectedMessage);

        Message result = messageService.sendMessage(messageDTO, userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(chatId, result.getChatId());
        assertEquals(content, result.getContent());
        assertNotNull(result.getTime());

        verify(chatMemberRepository).existsChatMemberByChatIdAndUserId(chatId, userId);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void sendMessage_UserNotInChat() {
        Integer userId = 1;
        Integer chatId = 1;
        String content = "Test message";

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setChatId(chatId);
        messageDTO.setContent(content);

        when(chatMemberRepository.existsChatMemberByChatIdAndUserId(chatId, userId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            messageService.sendMessage(messageDTO, userId);
        });

        assertEquals("该用户并非该群聊的成员", exception.getMessage());

        verify(chatMemberRepository).existsChatMemberByChatIdAndUserId(chatId, userId);
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void getChatHistory_Success() {
        Integer userId = 1;
        Integer chatId = 1;
        Instant messageTime = Instant.now();

        Message message1 = new Message();
        message1.setMessageId(1);
        message1.setUserId(1);
        message1.setChatId(chatId);
        message1.setContent("Message 1");
        message1.setTime(messageTime);

        Message message2 = new Message();
        message2.setMessageId(2);
        message2.setUserId(2);
        message2.setChatId(chatId);
        message2.setContent("Message 2");
        message2.setTime(messageTime);

        User user1 = new User();
        user1.setUserId(1);
        user1.setUserName("User1");
        user1.setUserType(UserType.user);
        user1.setRegistrationDate(Instant.now());

        User user2 = new User();
        user2.setUserId(2);
        user2.setUserName("User2");
        user2.setUserType(UserType.user);
        user2.setRegistrationDate(Instant.now());

        when(chatMemberRepository.existsChatMemberByChatIdAndUserId(chatId, userId)).thenReturn(true);
        when(messageRepository.getHistoryByChatId(chatId)).thenReturn(Arrays.asList(message1, message2));
        when(userService.getUserProfile(1)).thenReturn(new UserProfileDTO(user1.getUserName(), null));
        when(userService.getUserProfile(2)).thenReturn(new UserProfileDTO(user2.getUserName(), null));

        List<MessageUserDTO> result = messageService.getChatHistory(chatId, userId);

        assertNotNull(result);
        assertEquals(2, result.size());

        MessageUserDTO firstMessage = result.get(0);
        assertEquals(1, firstMessage.getMessageId());
        assertEquals(1, firstMessage.getUserId());
        assertEquals("Message 1", firstMessage.getContent());
        assertEquals(messageTime, firstMessage.getTime());
        assertEquals("User1", firstMessage.getUserName());

        MessageUserDTO secondMessage = result.get(1);
        assertEquals(2, secondMessage.getMessageId());
        assertEquals(2, secondMessage.getUserId());
        assertEquals("Message 2", secondMessage.getContent());
        assertEquals(messageTime, secondMessage.getTime());
        assertEquals("User2", secondMessage.getUserName());

        verify(chatMemberRepository).existsChatMemberByChatIdAndUserId(chatId, userId);
        verify(messageRepository).getHistoryByChatId(chatId);
        verify(userService).getUserProfile(1);
        verify(userService).getUserProfile(2);
    }

    @Test
    void getChatHistory_UserNotInChat() {
        Integer userId = 1;
        Integer chatId = 1;

        when(chatMemberRepository.existsChatMemberByChatIdAndUserId(chatId, userId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            messageService.getChatHistory(chatId, userId);
        });

        assertEquals("该用户并非该群聊的成员", exception.getMessage());

        verify(chatMemberRepository).existsChatMemberByChatIdAndUserId(chatId, userId);
        verify(messageRepository, never()).getHistoryByChatId(anyInt());
        verify(userService, never()).getUserProfile(anyInt());
    }

    @Test
    void deleteMsg_Success() {
        Integer userId = 1;
        Integer messageId = 1;

        when(messageRepository.deleteByMessageIdAndUserIdAndTime(eq(messageId), eq(userId), any(Instant.class))).thenReturn(1);

        ResultMsg result = messageService.deleteMsg(userId, messageId);

        assertNotNull(result);
        assertEquals("消息撤回成功", result.getMsg());
        assertEquals(1, result.getState());

        verify(messageRepository).deleteByMessageIdAndUserIdAndTime(eq(messageId), eq(userId), any(Instant.class));
    }

    @Test
    void deleteMsg_Failure() {
        Integer userId = 1;
        Integer messageId = 1;

        when(messageRepository.deleteByMessageIdAndUserIdAndTime(eq(messageId), eq(userId), any(Instant.class))).thenReturn(0);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            messageService.deleteMsg(userId, messageId);
        });

        assertEquals("撤回该信息失败", exception.getMessage());

        verify(messageRepository).deleteByMessageIdAndUserIdAndTime(eq(messageId), eq(userId), any(Instant.class));
    }
}
