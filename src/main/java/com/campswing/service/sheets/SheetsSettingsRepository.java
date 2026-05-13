package com.campswing.service.sheets;

import com.campswing.domain.settings.EventInfo;
import com.campswing.domain.settings.PartyPassBenefit;
import com.campswing.domain.settings.PickupBusTrip;
import com.campswing.domain.settings.ScheduleItem;
import com.campswing.domain.settings.Weekday;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SheetsSettingsRepository {

    private static final String SHEET_EVENT = "Event";
    private static final String SHEET_SCHEDULE = "Schedule";
    private static final String SHEET_BENEFIT = "PartyPassBenefit";
    private static final String SHEET_PICKUP = "PickupBus";

    private final GoogleSheetsClient client;

    public SheetsSettingsRepository(GoogleSheetsClient client) {
        this.client = client;
    }

    public EventInfo readEvent() {
        List<List<Object>> rows = client.readRange(client.settingsSpreadsheetId(), SHEET_EVENT, "A2:B");
        Map<String, String> kv = new HashMap<>();
        for (List<Object> row : rows) {
            if (row.size() >= 2) {
                String key = String.valueOf(row.get(0)).trim();
                String value = String.valueOf(row.get(1));
                kv.put(key, value);
            }
        }
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
                kv.getOrDefault("heroSubtitle", "")
        );
    }

    public List<ScheduleItem> readSchedule() {
        List<List<Object>> rows = client.readRange(client.settingsSpreadsheetId(), SHEET_SCHEDULE, "A2:F");
        List<ScheduleItem> result = new ArrayList<>();
        for (List<Object> r : rows) {
            if (r.size() < 5) continue;
            try {
                result.add(new ScheduleItem(
                        parseInt(asString(r, 0)),
                        Weekday.valueOf(asString(r, 1).trim().toUpperCase()),
                        LocalTime.parse(asString(r, 2).trim()),
                        LocalTime.parse(asString(r, 3).trim()),
                        asString(r, 4),
                        r.size() >= 6 ? asString(r, 5) : ""
                ));
            } catch (Exception ignore) {
                // 헤더 행이나 형식 오류 행은 건너뜀
            }
        }
        result.sort(Comparator.comparingInt(ScheduleItem::displayOrder));
        return result;
    }

    public List<PartyPassBenefit> readBenefits() {
        List<List<Object>> rows = client.readRange(client.settingsSpreadsheetId(), SHEET_BENEFIT, "A2:B");
        List<PartyPassBenefit> result = new ArrayList<>();
        for (List<Object> r : rows) {
            if (r.size() < 2) continue;
            try {
                int order = Integer.parseInt(asString(r, 0).trim());
                result.add(new PartyPassBenefit(order, asString(r, 1)));
            } catch (NumberFormatException ignore) {
                // 1열이 숫자가 아니면 헤더 행으로 간주, 건너뜀
            }
        }
        result.sort(Comparator.comparingInt(PartyPassBenefit::displayOrder));
        return result;
    }

    public List<PickupBusTrip> readPickupBus() {
        List<List<Object>> rows = client.readRange(client.settingsSpreadsheetId(), SHEET_PICKUP, "A2:E");
        List<PickupBusTrip> result = new ArrayList<>();
        for (List<Object> r : rows) {
            if (r.size() < 4) continue;
            try {
                result.add(new PickupBusTrip(
                        parseInt(asString(r, 0)),
                        LocalTime.parse(asString(r, 1).trim()),
                        asString(r, 2),
                        parseInt(asString(r, 3)),
                        r.size() >= 5 ? asString(r, 4) : ""
                ));
            } catch (Exception ignore) {
                // 헤더 행이나 형식 오류 행은 건너뜀
            }
        }
        result.sort(Comparator.comparingInt(PickupBusTrip::displayOrder));
        return result;
    }

    private static String asString(List<Object> row, int idx) {
        Object v = row.get(idx);
        return v == null ? "" : String.valueOf(v);
    }

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDate.parse(s.trim());
    }

    private static int parseInt(String s) {
        if (s == null || s.isBlank()) return 0;
        try {
            return Integer.parseInt(s.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
