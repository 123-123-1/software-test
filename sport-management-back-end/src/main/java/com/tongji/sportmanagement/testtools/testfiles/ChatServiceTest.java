package com.tongji.sportmanagement.testtools.testfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.tongji.sportmanagement.AccountSubsystem.Service.UserService;
import com.tongji.sportmanagement.SocializeSubsystem.DTO.FriendDTO;
import com.tongji.sportmanagement.SocializeSubsystem.Entity.Chat;
import com.tongji.sportmanagement.SocializeSubsystem.Repository.ChatMemberRepository;
import com.tongji.sportmanagement.SocializeSubsystem.Repository.ChatRepository;
import com.tongji.sportmanagement.SocializeSubsystem.Repository.FriendApplicationRepository;
import com.tongji.sportmanagement.SocializeSubsystem.Repository.MessageRepository;
import com.tongji.sportmanagement.SocializeSubsystem.Service.ChatService;

public class ChatServiceTest {
    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ChatMemberRepository chatMemberRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private FriendApplicationRepository friendApplicationRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChatService chatService;

    @Test
    void getFriendsBy_WhenUserHasFriends_ShouldReturnFriendList() {
        // 准备测试数据
        Integer userId = 1;
        
        Chat chat1 = new Chat();
        chat1.setChatId(1);
        chat1.setUserId(2); // 好友ID
        
        Chat chat2 = new Chat();
        chat2.setChatId(2);
        chat2.setUserId(3); // 另一个好友ID
        
        FriendDTO friend1Profile = new FriendDTO();
        friend1Profile.setId(2);
        friend1Profile.setUserName("friend1");
        friend1Profile.setPhoto("friend1.jpg");
        
        FriendDTO friend2Profile = new FriendDTO();
        friend2Profile.setId(3);
        friend2Profile.setUserName("friend2");
        friend2Profile.setPhoto("friend2.jpg");

        // 执行测试
        List<FriendDTO> result = chatService.getFriendsBy(userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // 验证第一个好友
        assertEquals(chat1.getChatId(), result.get(0).getChatId());
        assertEquals(friend1Profile.getUserName(), result.get(0).getUserName());
        assertEquals(friend1Profile.getPhoto(), result.get(0).getPhoto());
        
        // 验证第二个好友
        assertEquals(chat2.getChatId(), result.get(1).getChatId());
        assertEquals(friend2Profile.getUserName(), result.get(1).getUserName());
        assertEquals(friend2Profile.getPhoto(), result.get(1).getPhoto());
    }

    @Test
    void getFriendsBy_WhenUserHasNoFriends_ShouldReturnEmptyList() {
        // 准备
        Integer userId = 1;
        when(chatRepository.findFriendsByUserId(userId)).thenReturn(Collections.emptyList());

        // 执行
        List<FriendDTO> result = chatService.getFriendsBy(userId);

        // 验证
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getFriendsBy_WhenUserIdNotExist_ShouldReturnEmptyList() {
        // 准备
        Integer nonExistentUserId = 999;
        when(chatRepository.findFriendsByUserId(nonExistentUserId)).thenReturn(Collections.emptyList());

        // 执行
        List<FriendDTO> result = chatService.getFriendsBy(nonExistentUserId);

        // 验证
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void quitGroupChat_WhenUserIsMember_ShouldRemoveUserFromChat() {
        // 准备
        Integer chatId = 1;
        Integer userId = 1;
        
        // 模拟用户是群聊成员
        doAnswer(invocation -> {
            // 验证调用了delete方法
            assertEquals(chatId, invocation.getArgument(0));
            assertEquals(userId, invocation.getArgument(1));
            return null;
        }).when(chatMemberRepository).deleteByChatIdAndUserId(chatId, userId);
        
        // 模拟群聊还有其他成员
        when(chatMemberRepository.countByChatId(chatId)).thenReturn(1);

        // 执行
        chatService.quitGroupChat(chatId, userId);

        // 验证
        verify(chatMemberRepository).deleteByChatIdAndUserId(chatId, userId);
        verify(messageRepository, never()).deleteByChatId(anyInt());
        verify(chatRepository, never()).deleteById(anyInt());
    }

    @Test
    void quitGroupChat_WhenUserIsNotMember_ShouldDoNothing() {
        // 准备
        Integer chatId = 1;
        Integer userId = 1;
        
        // 模拟用户不是群聊成员
        doNothing().when(chatMemberRepository).deleteByChatIdAndUserId(chatId, userId);
        when(chatMemberRepository.countByChatId(chatId)).thenReturn(0);

        // 执行 - 不应抛出异常
        assertDoesNotThrow(() -> chatService.quitGroupChat(chatId, userId));

        // 验证
        verify(chatMemberRepository).deleteByChatIdAndUserId(chatId, userId);
    }

    @Test
    void quitGroupChat_WhenNoMembersLeft_ShouldDeleteChatAndMessages() {
        // 准备
        Integer chatId = 1;
        Integer userId = 1;
        
        // 模拟用户是最后一个成员
        doNothing().when(chatMemberRepository).deleteByChatIdAndUserId(chatId, userId);
        when(chatMemberRepository.countByChatId(chatId)).thenReturn(0);

        // 执行
        chatService.quitGroupChat(chatId, userId);

        // 验证
        verify(chatMemberRepository).deleteByChatIdAndUserId(chatId, userId);
        verify(messageRepository).deleteByChatId(chatId);
        verify(chatRepository).deleteById(chatId);
    }

    @Test
    void quitGroupChat_WhenChatHasNoMembersAfterQuit_ShouldDeleteChat() {
        // 准备
        Integer chatId = 1;
        Integer userId = 1;
        
        // 模拟用户是最后一个成员
        doNothing().when(chatMemberRepository).deleteByChatIdAndUserId(chatId, userId);
        when(chatMemberRepository.countByChatId(chatId)).thenReturn(0);

        // 执行
        chatService.quitGroupChat(chatId, userId);

        // 验证
        verify(messageRepository).deleteByChatId(chatId);
        verify(chatRepository).deleteById(chatId);
    }
}
