package com.tongji.sportmanagement.testtools.Service;

import com.tongji.sportmanagement.Common.DTO.ResultMsg;
import com.tongji.sportmanagement.SocializeSubsystem.DTO.MessageDTO;
import com.tongji.sportmanagement.SocializeSubsystem.DTO.MessageUserDTO;
import com.tongji.sportmanagement.SocializeSubsystem.Entity.Message;
import com.tongji.sportmanagement.SocializeSubsystem.Service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UnitTestService {

    @Autowired
    private MessageService messageService;

    public List<Map<String, Object>> runAllTests() {
        List<Map<String, Object>> results = new ArrayList<>();
        
        // 获取所有测试类
        Class<?>[] testClasses = {
            com.tongji.sportmanagement.GroupSubsystem.Controller.GroupControllerTest.class,
            com.tongji.sportmanagement.AccountSubsystem.Service.UserServiceTest.class
        };

        for (Class<?> testClass : testClasses) {
            Map<String, Object> classResult = new HashMap<>();
            classResult.put("className", testClass.getSimpleName());
            List<Map<String, Object>> testResults = new ArrayList<>();

            // 获取所有测试方法
            for (Method method : testClass.getMethods()) {
                if (method.isAnnotationPresent(Test.class)) {
                    Map<String, Object> testResult = new HashMap<>();
                    testResult.put("methodName", method.getName());
                    
                    try {
                        // 创建测试实例
                        Object testInstance = testClass.getDeclaredConstructor().newInstance();
                        
                        // 运行测试方法
                        long startTime = System.currentTimeMillis();
                        method.invoke(testInstance);
                        long endTime = System.currentTimeMillis();
                        
                        testResult.put("status", "PASSED");
                        testResult.put("executionTime", endTime - startTime);
                    } catch (Exception e) {
                        testResult.put("status", "FAILED");
                        testResult.put("error", e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
                    }
                    
                    testResults.add(testResult);
                }
            }
            
            classResult.put("testResults", testResults);
            results.add(classResult);
        }
        
        return results;
    }
} 