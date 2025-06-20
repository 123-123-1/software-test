package com.tongji.sportmanagement.testtools.testfiles;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tongji.sportmanagement.AccountSubsystem.Service.UserService;
import com.tongji.sportmanagement.Common.DTO.ResultMsg;
import com.tongji.sportmanagement.GroupSubsystem.Entity.Group;
import com.tongji.sportmanagement.GroupSubsystem.Repository.GroupApplicationRepository;
import com.tongji.sportmanagement.GroupSubsystem.Repository.GroupMemberRepository;
import com.tongji.sportmanagement.GroupSubsystem.Repository.GroupRecordRepository;
import com.tongji.sportmanagement.GroupSubsystem.Repository.GroupRepository;
import com.tongji.sportmanagement.GroupSubsystem.Service.GroupMemberService;
import com.tongji.sportmanagement.GroupSubsystem.Service.GroupRecordService;
import com.tongji.sportmanagement.SocializeSubsystem.Service.ChatService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)

public class GroupMemberServiceTest {
    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private GroupRecordRepository groupRecordRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupApplicationRepository groupApplicationRepository;

    @Mock
    private ChatService chatService;

    @Mock
    private UserService userService;

    @Mock
    private GroupRecordService groupRecordService;

    @InjectMocks
    private GroupMemberService groupMemberService;

    private final Integer validGroupId = 1;
    private final Integer validMemberId = 1;
    private final Integer invalidMemberId = 999;
    private final Integer chatId = 10;

    @Test
    void quitGroup_WhenUserIsMember_ShouldRemoveUserFromGroup() {
        // 准备测试数据
        Group group = new Group();
        group.setGroupId(validGroupId);
        group.setChatId(chatId);

        // 模拟依赖行为
        when(groupMemberRepository.existsByUserId(validMemberId)).thenReturn(true);
        when(groupRepository.findById(validGroupId)).thenReturn(Optional.of(group));
        when(groupMemberRepository.countByGroupId(validGroupId)).thenReturn(1); // 还有其他成员

        // 执行测试
        ResultMsg result = groupMemberService.quitGroup(validGroupId, validMemberId);

        // 验证结果
        assertEquals("退出团体成功", result.getMsg());
        
        // 验证交互
        verify(groupApplicationRepository).deleteByUserId(validMemberId);
        verify(groupMemberRepository).deleteByGroupIdAndUserId(validGroupId, validMemberId);
        verify(groupRecordRepository).deleteByGroupIdAndOperatorId(validGroupId, validMemberId);
        verify(chatService).quitGroupChat(chatId, validMemberId);
        verify(groupRepository, never()).deleteById(anyInt());
    }

    @Test
    void quitGroup_WhenUserIsNotMember_ShouldThrowException() {
        // 模拟用户不是团体成员
        when(groupMemberRepository.existsByUserId(invalidMemberId)).thenReturn(false);

        // 执行和验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            groupMemberService.quitGroup(validGroupId, invalidMemberId);
        });
        
        assertEquals("该用户没有加入团体", exception.getMessage());
        
        // 验证没有其他交互
        verifyNoMoreInteractions(groupApplicationRepository, groupMemberRepository, 
                               groupRecordRepository, chatService, groupRepository);
    }

    @Test
    void quitGroup_WhenNoMembersLeft_ShouldDeleteGroup() {
        // 准备测试数据
        Group group = new Group();
        group.setGroupId(validGroupId);
        group.setChatId(chatId);

        // 模拟用户是最后一个成员
        when(groupMemberRepository.existsByUserId(validMemberId)).thenReturn(true);
        when(groupRepository.findById(validGroupId)).thenReturn(Optional.of(group));
        when(groupMemberRepository.countByGroupId(validGroupId)).thenReturn(0);

        // 执行测试
        ResultMsg result = groupMemberService.quitGroup(validGroupId, validMemberId);

        // 验证结果
        assertEquals("退出团体成功", result.getMsg());
        
        // 验证交互
        verify(groupApplicationRepository).deleteByUserId(validMemberId);
        verify(groupMemberRepository).deleteByGroupIdAndUserId(validGroupId, validMemberId);
        verify(groupRecordRepository).deleteByGroupIdAndOperatorId(validGroupId, validMemberId);
        verify(chatService).quitGroupChat(chatId, validMemberId);
        verify(groupRepository).deleteById(validGroupId);
    }

    @Test
    void quitGroup_WhenGroupNotFound_ShouldThrowException() {
        // 模拟团体不存在
        when(groupMemberRepository.existsByUserId(validMemberId)).thenReturn(true);
        when(groupRepository.findById(validGroupId)).thenReturn(Optional.empty());

        // 执行和验证异常
        assertThrows(RuntimeException.class, () -> {
            groupMemberService.quitGroup(validGroupId, validMemberId);
        });
        
        // 验证部分交互
        verify(groupApplicationRepository).deleteByUserId(validMemberId);
        verify(groupMemberRepository).deleteByGroupIdAndUserId(validGroupId, validMemberId);
        verify(groupRecordRepository).deleteByGroupIdAndOperatorId(validGroupId, validMemberId);
    }
}
