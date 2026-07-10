package com.campswing.service;

import com.campswing.domain.event.EventCard;
import com.campswing.domain.market.FleaMarketVendor;
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

    public List<FleaMarketVendor> getAllFleaMarketVendors() {
        return repository.findAllFleaMarketVendors();
    }

    public List<EventCard> getAllEventCards() {
        return repository.findAllEventCards();
    }

    /** id로 단일 이벤트 조회 (상세 페이지용). 없으면 null. */
    public EventCard getEventCard(String id) {
        if (id == null || id.isBlank()) return null;
        return repository.findAllEventCards().stream()
                .filter(e -> id.equals(e.id()))
                .findFirst()
                .orElse(null);
    }
}
