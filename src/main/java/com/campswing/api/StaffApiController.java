package com.campswing.api;

import com.campswing.api.dto.ApiResponse;
import com.campswing.domain.staff.Dj;
import com.campswing.domain.staff.Person;
import com.campswing.service.StaffService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class StaffApiController {

    private final StaffService service;

    public StaffApiController(StaffService service) {
        this.service = service;
    }

    @GetMapping("/dj")
    public ApiResponse<List<Dj>> djs() {
        return ApiResponse.ok(service.getAllDjs());
    }

    @GetMapping("/instructors")
    public ApiResponse<List<Person>> instructors() {
        return ApiResponse.ok(service.getAllInstructors());
    }

    @GetMapping("/staff")
    public ApiResponse<List<Person>> staff() {
        return ApiResponse.ok(service.getAllStaff());
    }
}
