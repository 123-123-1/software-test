package com.tongji.sportmanagement.testtools.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class IntegrationTestService {
    public List<Map<String, Object>> runAllTests() {
        List<Map<String, Object>> results = new ArrayList<>();

        String[] testClassNames = {
            "com.tongji.sportmanagement.testtools.testfiles.MessageServiceTest",
            "com.tongji.sportmanagement.testtools.testfiles.UserServiceTest",
            "com.tongji.sportmanagement.testtools.testfiles.ChatServiceTest",
            "com.tongji.sportmanagement.testtools.testfiles.GroupMemberServiceTest"
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
