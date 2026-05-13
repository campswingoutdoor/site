package com.campswing.service.sheets;

import com.campswing.domain.staff.Dj;
import com.campswing.domain.staff.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository
public class SheetsStaffRepository {

    private static final Logger log = LoggerFactory.getLogger(SheetsStaffRepository.class);

    private static final String SHEET_DJ = "Dj";
    private static final String SHEET_INSTRUCTORS = "Instructors";
    private static final String SHEET_STAFF = "Staff";

    private static final List<Dj> STATIC_DJS = List.of(
            new Dj("chori", "DJ CHORI", "ASIAN PRINCE",
                    "SWING DANCE DJ", "/img/dj/chori.jpeg", null, 1),
            new Dj("royal-jelly", "DJ ROYAL JELLY", "PINK JELLY BEAR",
                    "SWING DANCE DJ", "/img/dj/royal-jelly.jpeg", null, 2)
    );

    private final GoogleSheetsClient client;

    public SheetsStaffRepository(GoogleSheetsClient client) {
        this.client = client;
    }

    public List<Dj> findAllDjs() {
        if (!client.isEnabled()) {
            return STATIC_DJS;
        }
        try {
            List<List<Object>> rows = client.readRange(SHEET_DJ, "A2:G");
            if (rows.isEmpty()) {
                return STATIC_DJS;
            }
            return rows.stream()
                    .filter(r -> !r.isEmpty() && r.get(0) != null && !r.get(0).toString().isBlank())
                    .map(SheetsStaffRepository::mapDj)
                    .sorted(Comparator.comparingInt(Dj::displayOrder))
                    .toList();
        } catch (Exception e) {
            log.warn("Falling back to static DJ list (sheet read failed): {}", e.getMessage());
            return STATIC_DJS;
        }
    }

    public List<Person> findAllInstructors() {
        return findPersons(SHEET_INSTRUCTORS);
    }

    public List<Person> findAllStaff() {
        return findPersons(SHEET_STAFF);
    }

    private List<Person> findPersons(String sheetName) {
        if (!client.isEnabled()) {
            return Collections.emptyList();
        }
        try {
            List<List<Object>> rows = client.readRange(sheetName, "A2:F");
            if (rows.isEmpty()) {
                return Collections.emptyList();
            }
            return rows.stream()
                    .filter(r -> !r.isEmpty() && r.get(0) != null && !r.get(0).toString().isBlank())
                    .map(SheetsStaffRepository::mapPerson)
                    .sorted(Comparator.comparingInt(Person::displayOrder))
                    .toList();
        } catch (Exception e) {
            log.warn("Falling back to empty list for sheet '{}' (read failed): {}", sheetName, e.getMessage());
            return Collections.emptyList();
        }
    }

    private static Dj mapDj(List<Object> row) {
        return new Dj(
                str(row, 0),
                str(row, 1),
                str(row, 2),
                str(row, 3),
                str(row, 4),
                str(row, 5),
                intOr(row, 6, 0)
        );
    }

    private static Person mapPerson(List<Object> row) {
        return new Person(
                str(row, 0),
                str(row, 1),
                str(row, 2),
                str(row, 3),
                str(row, 4),
                intOr(row, 5, 0)
        );
    }

    private static String str(List<Object> row, int idx) {
        if (idx >= row.size()) return null;
        Object v = row.get(idx);
        return v == null ? null : v.toString();
    }

    private static int intOr(List<Object> row, int idx, int fallback) {
        String s = str(row, idx);
        if (s == null || s.isBlank()) return fallback;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
