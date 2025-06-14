package com.tongji.sportmanagement.TestTools.Service;

import com.tongji.sportmanagement.Common.DTO.ResultMsg;
import com.tongji.sportmanagement.SocializeSubsystem.DTO.MessageDTO;
import com.tongji.sportmanagement.SocializeSubsystem.DTO.MessageUserDTO;
import com.tongji.sportmanagement.SocializeSubsystem.Entity.Message;
import com.tongji.sportmanagement.SocializeSubsystem.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UnitTestService {

    @Autowired
    private MessageService messageService;

    public List<Map<String, Object>> runTests(List<Map<String, Object>> testCases) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (Map<String, Object> testCase : testCases) {
            String testName = (String) testCase.get("name");
            Map<String, String> method = (Map<String, String>) testCase.get("method");
            String testMethod = method.get("测试方法");
            
            Instant start = Instant.now();
            boolean pass = false;
            String actual = "-";
            
            try {
                switch (testMethod) {
                    case "sendMessage":
                        pass = runSendMessageTest(testName);
                        break;
                    case "getChatHistory":
                        pass = runGetChatHistoryTest(testName);
                        break;
                    case "deleteMsg":
                        pass = runDeleteMsgTest(testName);
                        break;
                    default:
                        actual = "未知的测试方法: " + testMethod;
                }
            } catch (Exception e) {
                actual = e.getMessage();
            }
            
            Instant end = Instant.now();
            String duration = Duration.between(start, end).toMillis() + "ms";
            
            testCase.put("actual", actual);
            testCase.put("pass", pass);
            testCase.put("duration", duration);
            results.add(testCase);
        }
        
        return results;
    }
    
    private boolean runSendMessageTest(String testName) {
        try {
            switch (testName) {
                case "sendMessage_Success":
                    MessageDTO messageDTO = new MessageDTO();
                    messageDTO.setChatId(1);
                    messageDTO.setContent("Test message");
                    Message result = messageService.sendMessage(messageDTO, 1);
                    return result != null && result.getContent().equals("Test message");
                    
                case "sendMessage_UserNotInChat":
                    MessageDTO invalidDTO = new MessageDTO();
                    invalidDTO.setChatId(1);
                    invalidDTO.setContent("Test message");
                    try {
                        messageService.sendMessage(invalidDTO, 999);
                        return false;
                    } catch (RuntimeException e) {
                        return e.getMessage().equals("该用户并非该群聊的成员");
                    }
                    
                default:
                    return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("测试执行失败: " + e.getMessage());
        }
    }
    
    private boolean runGetChatHistoryTest(String testName) {
        try {
            switch (testName) {
                case "getChatHistory_Success":
                    List<MessageUserDTO> history = messageService.getChatHistory(1, 1);
                    return history != null && !history.isEmpty();
                    
                case "getChatHistory_UserNotInChat":
                    try {
                        messageService.getChatHistory(1, 999);
                        return false;
                    } catch (RuntimeException e) {
                        return e.getMessage().equals("该用户并非该群聊的成员");
                    }
                    
                default:
                    return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("测试执行失败: " + e.getMessage());
        }
    }
    
    private boolean runDeleteMsgTest(String testName) {
        try {
            switch (testName) {
                case "deleteMsg_Success":
                    ResultMsg result = messageService.deleteMsg(1, 1);
                    return result != null && result.getState() == 1;
                    
                case "deleteMsg_Failure":
                    try {
                        messageService.deleteMsg(1, 999);
                        return false;
                    } catch (RuntimeException e) {
                        return e.getMessage().equals("撤回该信息失败");
                    }
                    
                default:
                    return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("测试执行失败: " + e.getMessage());
        }
    }
} 