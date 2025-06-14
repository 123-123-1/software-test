package com.tongji.sportmanagement.TestTools.Controller;

import com.tongji.sportmanagement.Common.DTO.ResultMsg;
import com.tongji.sportmanagement.TestTools.Service.UnitTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/testtools/unit")
public class UnitTestController {

    @Autowired
    private UnitTestService unitTestService;

    @PostMapping("/run")
    public ResultMsg runTests(@RequestBody List<Map<String, Object>> testCases) {
        try {
            List<Map<String, Object>> results = unitTestService.runTests(testCases);
            return new ResultMsg(1, "测试完成", results);
        } catch (Exception e) {
            return new ResultMsg(0, "测试失败: " + e.getMessage(), null);
        }
    }
} 