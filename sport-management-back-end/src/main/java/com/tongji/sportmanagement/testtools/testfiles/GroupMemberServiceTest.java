package com.tongji.sportmanagement.testtools.testfiles;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.Mockito.*;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//
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
//
//import java.util.Optional;
//
//@ExtendWith(MockitoExtension.class)
//
//public class GroupMemberServiceTest {
//    @Mock
//    private GroupMemberRepository groupMemberRepository;
//
//    @Mock
//    private GroupRecordRepository groupRecordRepository;
//
//    @Mock
//    private GroupRepository groupRepository;
//
//    @Mock
//    private GroupApplicationRepository groupApplicationRepository;
//
//    @Mock
//    private ChatService chatService;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private GroupRecordService groupRecordService;
//
//    @InjectMocks
//    private GroupMemberService groupMemberService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    private final Integer validGroupId = 16;
//    private final Integer validMemberId = 20;
//    private final Integer invalidMemberId = 999;
//    private final Integer chatId = 43;
//
//    @Test
//    public void quitGroup_WhenUserIsMember_ShouldRemoveUserFromGroup() {
//        // 准备测试数据
//
//
//        // 模拟依赖行为 - 确保在所有操作前设置
//        when(groupMemberRepository.existsByUserId(validMemberId)).thenReturn(true);
//        when(groupMemberRepository.countByGroupId(validGroupId)).thenReturn(1);
//
//        // 执行测试
//        ResultMsg result = groupMemberService.quitGroup(validGroupId, validMemberId);
//
//        // 验证结果
//        assertEquals("退出团体成功", result.getMsg());
//
//    }
//
//    @Test
//    public void quitGroup_WhenUserIsNotMember_ShouldThrowException() {
//
//        // 执行和验证异常
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            groupMemberService.quitGroup(validGroupId, invalidMemberId);
//        });
//
//        assertEquals("该用户没有加入团体", exception.getMessage());
//
//        // 验证没有其他交互
//        verifyNoMoreInteractions(groupApplicationRepository, groupMemberRepository,
//                               groupRecordRepository, chatService, groupRepository);
//    }
//}


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupMemberServiceTest {

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private GroupApplicationRepository groupApplicationRepository;

    @Mock
    private GroupRecordRepository groupRecordRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ChatService chatService;

    @InjectMocks
    private GroupMemberService groupMemberService;

    @Test
    public void quitGroup_WhenUserIsMember() {
        // 准备测试数据
        Integer groupId = 16;
        Integer memberId = 20;

        // 执行测试
        ResultMsg result = groupMemberService.quitGroup(groupId, memberId);

        // 验证结果
        assertEquals("退出团体成功", result.getMsg());
        assertTrue(result.isSuccess());
    }

    @Test
    public void quitGroup_WhenUserIsNotMember() {
        // 准备测试数据
        Integer groupId = 1;
        Integer memberId = 100;

        // 模拟行为
        when(groupMemberRepository.existsByUserId(memberId)).thenReturn(false);

        // 执行和验证
        assertThrows(IllegalArgumentException.class, () -> {
            groupMemberService.quitGroup(groupId, memberId);
        });

        // 验证没有其他交互
        verifyNoMoreInteractions(groupApplicationRepository, groupMemberRepository,
                groupRecordRepository, groupRepository, chatService);
    }
}