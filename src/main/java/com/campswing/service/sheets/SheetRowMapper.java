package com.campswing.service.sheets;

import com.campswing.common.util.KstClock;
import com.campswing.domain.application.CampsiteApplication;
import com.campswing.domain.application.DormitoryApplication;
import com.campswing.domain.application.PartyPassApplication;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class SheetRowMapper {

    private static final DateTimeFormatter KST_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final List<String> PARTY_PASS_HEADERS = List.of(
            "id", "submittedAt", "realName", "nickname", "phone", "email",
            "passType", "club", "role", "useVehicle", "vehicleNumber",
            "dietaryNote", "memo", "agreedToTerms"
    );

    public static final List<String> CAMPSITE_HEADERS = List.of(
            "id", "submittedAt", "realName", "nickname", "phone", "email",
            "partySize", "arrivalTime", "usePickupBus",
            "totalPrice", "memo", "agreedToTerms"
    );

    public static final List<String> DORMITORY_HEADERS = List.of(
            "id", "submittedAt", "realName", "nickname", "phone", "email",
            "gender", "nights", "totalPrice", "memo", "agreedToTerms"
    );

    private SheetRowMapper() {
    }

    public static List<Object> toRow(PartyPassApplication a) {
        return row(
                a.id(),
                a.submittedAt().atZone(KstClock.KST).format(KST_FMT),
                a.realName(),
                a.nickname(),
                a.phone(),
                a.email(),
                a.passType().name(),
                nullToEmpty(a.club()),
                a.role().name(),
                a.useVehicle(),
                nullToEmpty(a.vehicleNumber()),
                nullToEmpty(a.dietaryNote()),
                nullToEmpty(a.memo()),
                a.agreedToTerms()
        );
    }

    public static List<Object> toRow(CampsiteApplication a) {
        return row(
                a.id(),
                a.submittedAt().atZone(KstClock.KST).format(KST_FMT),
                a.realName(),
                a.nickname(),
                a.phone(),
                a.email(),
                a.partySize(),
                a.arrivalTime().name(),
                a.usePickupBus(),
                a.totalPrice(),
                nullToEmpty(a.memo()),
                a.agreedToTerms()
        );
    }

    public static List<Object> toRow(DormitoryApplication a) {
        return row(
                a.id(),
                a.submittedAt().atZone(KstClock.KST).format(KST_FMT),
                a.realName(),
                a.nickname(),
                a.phone(),
                a.email(),
                a.gender().name(),
                a.nights().name(),
                a.totalPrice(),
                nullToEmpty(a.memo()),
                a.agreedToTerms()
        );
    }

    private static List<Object> row(Object... values) {
        List<Object> list = new ArrayList<>(values.length);
        for (Object v : values) {
            list.add(v);
        }
        return list;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
