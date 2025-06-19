package com.tongji.sportmanagement.testtools.Controller;

import com.tongji.sportmanagement.testtools.Service.UnitTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/testtools/unit")
public class UnitTestController {

    @Autowired
    private UnitTestService unitTestService;

    @PostMapping("/run")
    public List<Map<String, Object>> runAllTests() {
        return unitTestService.runAllTests();
    }
} 