package com.campswing.service.sheets;

import com.campswing.domain.settings.ApplyCard;
import com.campswing.domain.settings.ComingSoonItem;
import com.campswing.domain.settings.ConceptCopy;
import com.campswing.domain.settings.EventInfo;
import com.campswing.domain.settings.IndexHighlight;
import com.campswing.domain.settings.LocationGuide;
import com.campswing.domain.settings.NoticeLine;
import com.campswing.domain.settings.PageMeta;
import com.campswing.domain.settings.PartyPassBenefit;
import com.campswing.domain.settings.PickupBusTrip;
import com.campswing.domain.settings.ScheduleItem;
import com.campswing.domain.settings.SettingsSnapshot;
import com.campswing.domain.settings.VenueDetail;
import com.campswing.domain.settings.Weekday;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SheetsSettingsRepository {

    private static final String SHEET_EVENT = "Event";
    private static final String SHEET_SCHEDULE = "Schedule";
    private static final String SHEET_BENEFIT = "PartyPassBenefit";
    private static final String SHEET_PICKUP = "PickupBus";
    private static final String SHEET_PAGE_META = "PageMeta";
    private static final String SHEET_INDEX_HIGHLIGHT = "IndexHighlight";
    private static final String SHEET_VENUE_DETAIL = "VenueDetail";
    private static final String SHEET_APPLY_CARD = "ApplyCard";
    private static final String SHEET_LOCATION_GUIDE = "LocationGuide";
    private static final String SHEET_CONCEPT_COPY = "ConceptCopy";
    private static final String SHEET_COMING_SOON = "ComingSoon";
    private static final String SHEET_CAMPSITE_NOTICE = "CampsiteNotice";
    private static final String SHEET_DORMITORY_NOTICE = "DormitoryNotice";

    // batchGet에 사용할 range — 순서 중요 (parseSnapshot의 인덱스와 일치해야 함)
    private static final String RANGE_EVENT = SHEET_EVENT + "!A2:B";
    private static final String RANGE_SCHEDULE = SHEET_SCHEDULE + "!A2:F";
    private static final String RANGE_BENEFIT = SHEET_BENEFIT + "!A2:B";
    private static final String RANGE_PICKUP = SHEET_PICKUP + "!A2:E";
    private static final String RANGE_PAGE_META = SHEET_PAGE_META + "!A2:E";
    private static final String RANGE_INDEX_HIGHLIGHT = SHEET_INDEX_HIGHLIGHT + "!A2:E";
    private static final String RANGE_VENUE_DETAIL = SHEET_VENUE_DETAIL + "!A2:G";
    private static final String RANGE_APPLY_CARD = SHEET_APPLY_CARD + "!A2:G";
    private static final String RANGE_LOCATION_GUIDE = SHEET_LOCATION_GUIDE + "!A2:B";
    private static final String RANGE_CONCEPT_COPY = SHEET_CONCEPT_COPY + "!A2:B";
    private static final String RANGE_COMING_SOON = SHEET_COMING_SOON + "!A2:C";

    private static final List<String> ALL_RANGES = List.of(
            RANGE_EVENT, RANGE_SCHEDULE, RANGE_BENEFIT, RANGE_PICKUP,
            RANGE_PAGE_META, RANGE_INDEX_HIGHLIGHT, RANGE_VENUE_DETAIL, RANGE_APPLY_CARD,
            RANGE_LOCATION_GUIDE, RANGE_CONCEPT_COPY, RANGE_COMING_SOON
    );

    private final GoogleSheetsClient client;

    public SheetsSettingsRepository(GoogleSheetsClient client) {
        this.client = client;
    }

    /**
     * 11개 Settings 탭을 한 번의 batchGet 호출로 가져옴.
     * Sheets API quota 절감 (N → 1).
     * 일부 탭이 없으면 전체 batch가 실패하므로 SettingsService에서 try/catch.
     */
    public SettingsSnapshot readAll() {
        Map<String, List<List<Object>>> values = client.batchReadRanges(client.settingsSpreadsheetId(), ALL_RANGES);
        return new SettingsSnapshot(
                parseEvent(values.getOrDefault(RANGE_EVENT, List.of())),
                parseSchedule(values.getOrDefault(RANGE_SCHEDULE, List.of())),
                parseBenefits(values.getOrDefault(RANGE_BENEFIT, List.of())),
                parsePickupBus(values.getOrDefault(RANGE_PICKUP, List.of())),
                parsePageMetas(values.getOrDefault(RANGE_PAGE_META, List.of())),
                parseIndexHighlights(values.getOrDefault(RANGE_INDEX_HIGHLIGHT, List.of())),
                parseVenueDetails(values.getOrDefault(RANGE_VENUE_DETAIL, List.of())),
                parseApplyCards(values.getOrDefault(RANGE_APPLY_CARD, List.of())),
                parseLocationGuide(toKv(values.getOrDefault(RANGE_LOCATION_GUIDE, List.of()))),
                parseConceptCopy(toKv(values.getOrDefault(RANGE_CONCEPT_COPY, List.of()))),
                parseComingSoon(values.getOrDefault(RANGE_COMING_SOON, List.of()))
        );
    }

    // ===== 개별 read 메서드 (호환성 유지, 테스트/디버그 용) =====

    public EventInfo readEvent() {
        return parseEvent(client.readRange(client.settingsSpreadsheetId(), SHEET_EVENT, "A2:B"));
    }

    public List<ScheduleItem> readSchedule() {
        return parseSchedule(client.readRange(client.settingsSpreadsheetId(), SHEET_SCHEDULE, "A2:F"));
    }

    public List<PartyPassBenefit> readBenefits() {
        return parseBenefits(client.readRange(client.settingsSpreadsheetId(), SHEET_BENEFIT, "A2:B"));
    }

    public List<PickupBusTrip> readPickupBus() {
        return parsePickupBus(client.readRange(client.settingsSpreadsheetId(), SHEET_PICKUP, "A2:E"));
    }

    public Map<String, PageMeta> readPageMetas() {
        return parsePageMetas(client.readRange(client.settingsSpreadsheetId(), SHEET_PAGE_META, "A2:E"));
    }

    public List<IndexHighlight> readIndexHighlights() {
        return parseIndexHighlights(client.readRange(client.settingsSpreadsheetId(), SHEET_INDEX_HIGHLIGHT, "A2:E"));
    }

    public List<VenueDetail> readVenueDetails() {
        return parseVenueDetails(client.readRange(client.settingsSpreadsheetId(), SHEET_VENUE_DETAIL, "A2:G"));
    }

    public List<ApplyCard> readApplyCards() {
        return parseApplyCards(client.readRange(client.settingsSpreadsheetId(), SHEET_APPLY_CARD, "A2:G"));
    }

    public LocationGuide readLocationGuide() {
        return parseLocationGuide(toKv(client.readRange(client.settingsSpreadsheetId(), SHEET_LOCATION_GUIDE, "A2:B")));
    }

    public ConceptCopy readConceptCopy() {
        return parseConceptCopy(toKv(client.readRange(client.settingsSpreadsheetId(), SHEET_CONCEPT_COPY, "A2:B")));
    }

    public Map<String, ComingSoonItem> readComingSoon() {
        return parseComingSoon(client.readRange(client.settingsSpreadsheetId(), SHEET_COMING_SOON, "A2:C"));
    }

    /**
     * 캠핑/도미토리 안내 문구는 메인 batchGet 밖에서 개별 read.
     * 신규 탭(CampsiteNotice/DormitoryNotice)이 아직 없어도 메인 Settings batch가 깨지지 않도록 격리.
     * 탭 미존재 시 SheetsApiException → SettingsService.refresh()가 per-read try/catch로 폴백 유지.
     */
    public List<NoticeLine> readCampsiteNotice() {
        return parseNotices(client.readRange(client.settingsSpreadsheetId(), SHEET_CAMPSITE_NOTICE, "A2:B"));
    }

    public List<NoticeLine> readDormitoryNotice() {
        return parseNotices(client.readRange(client.settingsSpreadsheetId(), SHEET_DORMITORY_NOTICE, "A2:B"));
    }

    // ===== Parse helpers =====

    private static EventInfo parseEvent(List<List<Object>> rows) {
        Map<String, String> kv = toKv(rows);
        return new EventInfo(
                kv.get("name"),
                kv.get("slogan"),
                parseDate(kv.get("startDate")),
                parseDate(kv.get("endDate")),
                new EventInfo.Venue(kv.get("mainVenueName"), kv.get("mainVenueAddress")),
                new EventInfo.Venue(kv.get("prePartyVenueName"), kv.get("prePartyVenueAddress")),
                kv.getOrDefault("contactEmail", ""),
                kv.getOrDefault("instagram", ""),
                kv.getOrDefault("bankName", ""),
                kv.getOrDefault("bankAccount", ""),
                kv.getOrDefault("accountHolder", ""),
                kv.getOrDefault("kakaoMapUrl", ""),
                kv.getOrDefault("heroSubtitle", ""),
                kv.getOrDefault("successDepositNotice", ""),
                kv.getOrDefault("successDeadlineNotice", "")
        );
    }

    private static List<ScheduleItem> parseSchedule(List<List<Object>> rows) {
        List<ScheduleItem> result = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<Object> r = rows.get(i);
            if (isHeaderRow(r) || r.size() < 5) continue;
            try {
                result.add(new ScheduleItem(
                        parseDisplayOrder(asString(r, 0), i),
                        Weekday.valueOf(asString(r, 1).toUpperCase()),
                        parseTime(asString(r, 2)),
                        parseTime(asString(r, 3)),
                        asString(r, 4),
                        r.size() >= 6 ? asString(r, 5) : ""
                ));
            } catch (Exception ignore) {
            }
        }
        result.sort(Comparator.comparingInt(ScheduleItem::displayOrder));
        return result;
    }

    private static List<PartyPassBenefit> parseBenefits(List<List<Object>> rows) {
        List<PartyPassBenefit> result = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<Object> r = rows.get(i);
            if (isHeaderRow(r) || r.size() < 2) continue;
            String text = asString(r, 1);
            if (text.isEmpty()) continue;
            result.add(new PartyPassBenefit(parseDisplayOrder(asString(r, 0), i), text));
        }
        result.sort(Comparator.comparingInt(PartyPassBenefit::displayOrder));
        return result;
    }

    private static List<NoticeLine> parseNotices(List<List<Object>> rows) {
        List<NoticeLine> result = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<Object> r = rows.get(i);
            if (isHeaderRow(r) || r.size() < 2) continue;
            String text = asString(r, 1);
            if (text.isEmpty()) continue;
            result.add(new NoticeLine(parseDisplayOrder(asString(r, 0), i), text));
        }
        result.sort(Comparator.comparingInt(NoticeLine::displayOrder));
        return result;
    }

    private static List<PickupBusTrip> parsePickupBus(List<List<Object>> rows) {
        List<PickupBusTrip> result = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<Object> r = rows.get(i);
            if (isHeaderRow(r) || r.size() < 4) continue;
            try {
                LocalTime departure = parseTime(asString(r, 1));
                if (departure == null) continue;
                result.add(new PickupBusTrip(
                        parseDisplayOrder(asString(r, 0), i),
                        departure,
                        asString(r, 2),
                        parseInt(asString(r, 3)),
                        r.size() >= 5 ? asString(r, 4) : ""
                ));
            } catch (Exception ignore) {
            }
        }
        result.sort(Comparator.comparingInt(PickupBusTrip::displayOrder));
        return result;
    }

    private static Map<String, PageMeta> parsePageMetas(List<List<Object>> rows) {
        Map<String, PageMeta> result = new LinkedHashMap<>();
        for (List<Object> r : rows) {
            if (isHeaderRow(r) || r.size() < 2) continue;
            String key = asString(r, 0);
            if (key.isEmpty()) continue;
            result.put(key, new PageMeta(
                    key,
                    r.size() >= 2 ? asString(r, 1) : "",
                    r.size() >= 3 ? asString(r, 2) : "",
                    r.size() >= 4 ? asString(r, 3) : "",
                    r.size() >= 5 ? asString(r, 4) : ""
            ));
        }
        return result;
    }

    private static List<IndexHighlight> parseIndexHighlights(List<List<Object>> rows) {
        List<IndexHighlight> result = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<Object> r = rows.get(i);
            if (isHeaderRow(r) || r.size() < 3) continue;
            String key = asString(r, 1);
            if (key.isEmpty()) continue;
            result.add(new IndexHighlight(
                    parseDisplayOrder(asString(r, 0), i),
                    key,
                    asString(r, 2),
                    r.size() >= 4 ? asString(r, 3) : "",
                    r.size() >= 5 ? asString(r, 4) : ""
            ));
        }
        result.sort(Comparator.comparingInt(IndexHighlight::displayOrder));
        return result;
    }

    private static List<VenueDetail> parseVenueDetails(List<List<Object>> rows) {
        List<VenueDetail> result = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<Object> r = rows.get(i);
            if (isHeaderRow(r) || r.size() < 4) continue;
            String key = asString(r, 1);
            if (key.isEmpty()) continue;
            result.add(new VenueDetail(
                    parseDisplayOrder(asString(r, 0), i),
                    key,
                    asString(r, 2),
                    asString(r, 3),
                    r.size() >= 5 ? asString(r, 4) : "",
                    r.size() >= 6 ? asString(r, 5) : "",
                    r.size() >= 7 ? asString(r, 6) : ""
            ));
        }
        result.sort(Comparator.comparingInt(VenueDetail::displayOrder));
        return result;
    }

    private static List<ApplyCard> parseApplyCards(List<List<Object>> rows) {
        List<ApplyCard> result = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<Object> r = rows.get(i);
            if (isHeaderRow(r) || r.size() < 4) continue;
            String key = asString(r, 1);
            if (key.isEmpty()) continue;
            result.add(new ApplyCard(
                    parseDisplayOrder(asString(r, 0), i),
                    key,
                    asString(r, 2),
                    asString(r, 3),
                    r.size() >= 5 ? asString(r, 4) : "",
                    r.size() >= 6 ? asString(r, 5) : "",
                    r.size() >= 7 ? asString(r, 6) : ""
            ));
        }
        result.sort(Comparator.comparingInt(ApplyCard::displayOrder));
        return result;
    }

    private static LocationGuide parseLocationGuide(Map<String, String> kv) {
        return new LocationGuide(
                kv.getOrDefault("transportTitle", ""),
                kv.getOrDefault("transportRoute", ""),
                kv.getOrDefault("duration", ""),
                kv.getOrDefault("roadAddress", ""),
                kv.getOrDefault("pickupHeadline", ""),
                kv.getOrDefault("pickupDescription", ""),
                kv.getOrDefault("pickupNote1", ""),
                kv.getOrDefault("pickupNote2", "")
        );
    }

    private static ConceptCopy parseConceptCopy(Map<String, String> kv) {
        return new ConceptCopy(
                kv.getOrDefault("concept", ""),
                kv.getOrDefault("tagline", ""),
                kv.getOrDefault("subjectLine", ""),
                kv.getOrDefault("scheduleNote", "")
        );
    }

    private static Map<String, ComingSoonItem> parseComingSoon(List<List<Object>> rows) {
        Map<String, ComingSoonItem> result = new LinkedHashMap<>();
        for (List<Object> r : rows) {
            if (isHeaderRow(r) || r.size() < 2) continue;
            String key = asString(r, 0);
            if (key.isEmpty()) continue;
            result.put(key, new ComingSoonItem(
                    key,
                    r.size() >= 2 ? asString(r, 1) : "",
                    r.size() >= 3 ? asString(r, 2) : ""
            ));
        }
        return result;
    }

    /**
     * 시트의 헤더 행 감지 — A열이 'displayOrder' 또는 'key'면 헤더로 간주.
     * 운영자가 코드 기대 행(2행)부터 데이터를 시작하지 않고 1행에 영문 헤더, 2행부터 데이터를 입력하는
     * 자연스러운 시트 운영 패턴을 자동 수용. 코드는 A2부터 읽으므로 첫 번째 데이터 행이 헤더라면 스킵.
     */
    private static boolean isHeaderRow(List<Object> r) {
        if (r.isEmpty()) return true;
        String first = String.valueOf(r.get(0)).trim();
        return "displayOrder".equalsIgnoreCase(first)
                || "key".equalsIgnoreCase(first);
    }

    private static Map<String, String> toKv(List<List<Object>> rows) {
        Map<String, String> kv = new HashMap<>();
        for (List<Object> row : rows) {
            if (isHeaderRow(row) || row.size() < 2) continue;
            String key = String.valueOf(row.get(0)).trim();
            if (key.isEmpty()) continue;
            // value도 trim — 운영자가 끝에 공백 실수 입력해도 화면에 공백 안 보이도록
            kv.put(key, String.valueOf(row.get(1)).trim());
        }
        return kv;
    }

    /**
     * row의 idx번째 셀을 trim된 문자열로 반환.
     * 시트 입력 시 운영자가 의도치 않게 넣은 trailing/leading space는 화면 일관성을 해치므로 자동 제거.
     */
    private static String asString(List<Object> row, int idx) {
        Object v = row.get(idx);
        return v == null ? "" : String.valueOf(v).trim();
    }

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDate.parse(s.trim());
    }

    /**
     * 시간 파싱 — `07:50`/`7:50`/`7:50:00` 등 다양한 입력 허용.
     * 시트가 자동으로 `07:50`을 `7:50`으로 변환하는 경우(셀 서식 기본값)도 받아냄.
     */
    private static LocalTime parseTime(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        try {
            return LocalTime.parse(t);
        } catch (DateTimeParseException e) {
            // 한자리 시간 (H:mm 또는 H:mm:ss) 폴백
            return LocalTime.parse(t, DateTimeFormatter.ofPattern("H:mm[:ss]"));
        }
    }

    private static int parseInt(String s) {
        if (s == null || s.isBlank()) return 0;
        try {
            return Integer.parseInt(s.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * displayOrder 셀을 정수로 파싱. 비어있거나 잘못된 형식이면 row의 0-based 인덱스를 사용.
     * 결과: 시트에서 displayOrder를 일부만 채우거나 비워도 시트 행 순서가 보존됨.
     */
    private static int parseDisplayOrder(String s, int rowIndex) {
        if (s == null || s.isBlank()) return rowIndex;
        try {
            return Integer.parseInt(s.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return rowIndex;
        }
    }
}
