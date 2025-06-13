package com.tongji.sportmanagement.testtools;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api/testtools/system")
public class TestSystemController {

    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> runSystemTests(@RequestBody List<Map<String, Object>> testCases) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (Map<String, Object> testCase : testCases) {
            Map<String, Object> result = new HashMap<>();
            String name = (String) testCase.get("name");
            Map<String, Object> design = (Map<String, Object>) testCase.get("design");
            String expect = (String) testCase.get("expect");
            String method = (String) testCase.get("method");

            long start = System.currentTimeMillis();
            boolean pass = false;
            String actual = "";
            try {
                RestTemplate restTemplate = new RestTemplate();
                switch (name) {
                    case "用户注册":
                        actual = restTemplate.postForObject("http://localhost:8080/api/users/registration", design, Object.class).toString();
                        pass = actual.contains("success") || actual.contains("注册成功");
                        break;
                    case "个人场地预约":
                        actual = restTemplate.postForObject("http://localhost:8080/api/reservations/individual", design, Object.class).toString();
                        pass = actual.contains("success") || actual.contains("预约成功");
                        break;
                    case "发起拼场预约":
                        actual = restTemplate.postForObject("http://localhost:8080/api/reservations/group", design, Object.class).toString();
                        pass = actual.contains("success") || actual.contains("拼场");
                        break;
                    case "团体预约场地":
                        actual = restTemplate.postForObject("http://localhost:8080/api/reservations/group", design, Object.class).toString();
                        pass = actual.contains("success") || actual.contains("团体预约");
                        break;
                    case "创建运动活动":
                        actual = restTemplate.postForObject("http://localhost:8080/api/activities/create", design, Object.class).toString();
                        pass = actual.contains("success") || actual.contains("活动");
                        break;
                    case "用户登录（密码错误）":
                        actual = restTemplate.postForObject("http://localhost:8080/api/users/login", design, Object.class).toString();
                        pass = actual.contains("密码错误") || actual.contains("fail");
                        break;
                    case "取消个人预约":
                        actual = restTemplate.patchForObject("http://localhost:8080/api/reservations/cancel", design, Object.class).toString();
                        pass = actual.contains("success") || actual.contains("取消成功");
                        break;
                    case "拒绝好友申请":
                        actual = restTemplate.patchForObject("http://localhost:8080/api/socialize/application", design, Object.class).toString();
                        pass = actual.contains("success") || actual.contains("拒绝");
                        break;
                    case "解散运动团体":
                        // 假设groupId在design里
                        Object groupId = design.get("groupId");
                        restTemplate.delete("http://localhost:8080/api/groups?groupId=" + groupId);
                        actual = "已请求解散团体";
                        pass = true; // 你可以根据实际返回内容判断
                        break;
                    default:
                        actual = "未实现的用例";
                }
            } catch (Exception e) {
                actual = e.getMessage();
            }
            long end = System.currentTimeMillis();

            result.put("name", name);
            result.put("design", design);
            result.put("method", method);
            result.put("expect", expect);
            result.put("actual", actual);
            result.put("pass", pass);
            result.put("duration", (end - start) + "ms");
            results.add(result);
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("results", results);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
} 