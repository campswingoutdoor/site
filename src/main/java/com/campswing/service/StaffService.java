package com.campswing.service;

import com.campswing.domain.staff.Dj;
import com.campswing.domain.staff.Person;
import com.campswing.service.sheets.SheetsStaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService {

    private final SheetsStaffRepository repository;

    public StaffService(SheetsStaffRepository repository) {
        this.repository = repository;
    }

    public List<Dj> getAllDjs() {
        return repository.findAllDjs();
    }

    public List<Person> getAllInstructors() {
        return repository.findAllInstructors();
    }

    public List<Person> getAllStaff() {
        return repository.findAllStaff();
    }

    public List<Person> getLegacyDancers() {
        return repository.findAllLegacyDancers();
    }

    public List<Person> getSpecialGuestDancers() {
        return repository.findAllSpecialGuestDancers();
    }
}
