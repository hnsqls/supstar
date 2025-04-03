package com.ls.supstar.controller;

import com.ls.supstar.MainApplication;

import com.ls.supstar.common.BaseResponse;
import com.ls.supstar.controller.AttendanceRowController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = MainApplication.class) // Add classes parameter
public class AttendanceRowControllerTest {

    @Autowired
    private AttendanceRowController controller;

    @Test
    public void testExportExcel() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        BaseResponse<String> response = controller.exportExcel(ids);
        
        // Add assertions
        assertNotNull(response);
        assertTrue(response.getData().endsWith(".xlsx"));
    }
}