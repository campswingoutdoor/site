package com.campswing.service.sheets;

import com.campswing.domain.event.EventCard;
import com.campswing.domain.market.FleaMarketVendor;
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
    private static final String SHEET_LEGACY_DANCERS = "LegacyDancers";
    private static final String SHEET_SPECIAL_GUEST_DANCERS = "SpecialGuestDancers";
    private static final String SHEET_FLEA_MARKET = "FleaMarket";
    private static final String SHEET_EVENTS = "Events";

    private final GoogleSheetsClient client;

    public SheetsStaffRepository(GoogleSheetsClient client) {
        this.client = client;
    }

    public List<Dj> findAllDjs() {
        if (!client.isEnabled()) {
            return Collections.emptyList();
        }
        try {
            // 라인업 정보는 정적 콘텐츠 — Settings 스프레드시트에서 읽음
            List<List<Object>> rows = client.readRange(client.settingsSpreadsheetId(), SHEET_DJ, "A2:G");
            if (rows.isEmpty()) {
                return Collections.emptyList();
            }
            return rows.stream()
                    .filter(SheetsStaffRepository::isDataRow)
                    .map(SheetsStaffRepository::mapDj)
                    .sorted(Comparator.comparingInt(Dj::displayOrder))
                    .toList();
        } catch (Exception e) {
            log.warn("Falling back to empty DJ list (sheet read failed): {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<FleaMarketVendor> findAllFleaMarketVendors() {
        if (!client.isEnabled()) {
            return Collections.emptyList();
        }
        try {
            // 플리마켓 셀러도 정적 콘텐츠 — Settings 스프레드시트에서 읽음
            List<List<Object>> rows = client.readRange(client.settingsSpreadsheetId(), SHEET_FLEA_MARKET, "A2:G");
            if (rows.isEmpty()) {
                return Collections.emptyList();
            }
            return rows.stream()
                    .filter(SheetsStaffRepository::isDataRow)
                    .map(SheetsStaffRepository::mapVendor)
                    .sorted(Comparator.comparingInt(FleaMarketVendor::displayOrder))
                    .toList();
        } catch (Exception e) {
            log.warn("Falling back to empty flea market list (sheet read failed): {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<EventCard> findAllEventCards() {
        if (!client.isEnabled()) {
            return Collections.emptyList();
        }
        try {
            // 이벤트 카드도 정적 콘텐츠 — Settings 스프레드시트 Events 탭에서 읽음
            List<List<Object>> rows = client.readRange(client.settingsSpreadsheetId(), SHEET_EVENTS, "A2:G");
            if (rows.isEmpty()) {
                return Collections.emptyList();
            }
            return rows.stream()
                    .filter(SheetsStaffRepository::isDataRow)
                    .map(SheetsStaffRepository::mapEventCard)
                    .sorted(Comparator.comparingInt(EventCard::displayOrder))
                    .toList();
        } catch (Exception e) {
            log.warn("Falling back to empty event list (sheet read failed): {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Person> findAllInstructors() {
        return findPersons(SHEET_INSTRUCTORS);
    }

    public List<Person> findAllStaff() {
        return findPersons(SHEET_STAFF);
    }

    public List<Person> findAllLegacyDancers() {
        return findPersons(SHEET_LEGACY_DANCERS);
    }

    public List<Person> findAllSpecialGuestDancers() {
        return findPersons(SHEET_SPECIAL_GUEST_DANCERS);
    }

    private List<Person> findPersons(String sheetName) {
        if (!client.isEnabled()) {
            return Collections.emptyList();
        }
        try {
            // 강사/스태프 정보는 정적 콘텐츠 — Settings 스프레드시트에서 읽음
            List<List<Object>> rows = client.readRange(client.settingsSpreadsheetId(), sheetName, "A2:F");
            if (rows.isEmpty()) {
                return Collections.emptyList();
            }
            return rows.stream()
                    .filter(SheetsStaffRepository::isDataRow)
                    .map(SheetsStaffRepository::mapPerson)
                    .sorted(Comparator.comparingInt(Person::displayOrder))
                    .toList();
        } catch (Exception e) {
            log.warn("Falling back to empty list for sheet '{}' (read failed): {}", sheetName, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 빈 행과 헤더 잔재 행을 걸러낸다. 운영자가 1행에 한글 라벨 + 2행에 영문 헤더(id/name/...)를
     * 두는 패턴을 쓰는데, 코드가 A2부터 읽으므로 영문 헤더 행이 데이터로 들어오는 것을 막아야 한다.
     */
    private static boolean isDataRow(List<Object> r) {
        if (r.isEmpty() || r.get(0) == null) return false;
        String first = r.get(0).toString().trim();
        if (first.isBlank()) return false;
        return !"id".equalsIgnoreCase(first);
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

    private static FleaMarketVendor mapVendor(List<Object> row) {
        return new FleaMarketVendor(
                str(row, 0),
                str(row, 1),
                str(row, 2),
                str(row, 3),
                str(row, 4),
                str(row, 5),
                intOr(row, 6, 0)
        );
    }

    private static EventCard mapEventCard(List<Object> row) {
        return new EventCard(
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
