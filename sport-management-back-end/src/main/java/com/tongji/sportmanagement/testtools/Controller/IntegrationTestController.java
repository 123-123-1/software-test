package com.tongji.sportmanagement.testtools.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tongji.sportmanagement.testtools.Service.IntegrationTestService;

@RestController
@RequestMapping("/api/testtools/integration")
public class IntegrationTestController {
    
    @Autowired
    private IntegrationTestService integrationTestService;

    @PostMapping("/run")
    public List<Map<String, Object>> runAllTests() {
        return integrationTestService.runAllTests();
    }
}
