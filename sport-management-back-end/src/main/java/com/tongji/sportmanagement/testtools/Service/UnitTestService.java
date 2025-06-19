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

        String[] testClassNames = {
            "com.tongji.sportmanagement.testtools.testfiles.MessageServiceTest",
            "com.tongji.sportmanagement.testtools.testfiles.UserServiceTest"
        };

        for (String className : testClassNames) {
            try {
                Class<?> testClass = Class.forName(className);
                Map<String, Object> classResult = new HashMap<>();
                classResult.put("className", testClass.getSimpleName());
                List<Map<String, Object>> testResults = new ArrayList<>();

                for (Method method : testClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(org.junit.jupiter.api.Test.class)) {
                        Map<String, Object> testResult = new HashMap<>();
                        testResult.put("methodName", method.getName());
                        
                        try {
                            Object testInstance = testClass.getDeclaredConstructor().newInstance();
                            org.mockito.MockitoAnnotations.openMocks(testInstance);
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
            } catch (ClassNotFoundException e) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("className", className);
                errorResult.put("error", "Test class not found: " + e.getMessage());
                results.add(errorResult);
            }
        }
        
        return results;
    }
} 