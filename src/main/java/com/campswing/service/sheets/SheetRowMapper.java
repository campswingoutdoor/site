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

    // 주의: status(운영자 기록용)는 항상 마지막 열이며 toRow에 포함하지 않는다.
    // 신청 append 시 status 셀을 건드리지 않아 빈 값으로 덮어쓰지 않도록 하기 위함.
    public static final List<String> PARTY_PASS_HEADERS = List.of(
            "id", "submittedAt", "realName", "nickname", "phone", "email",
            "passType", "club", "role", "applyWorkshop", "vehicleUsage", "vehicleNumber",
            "totalPrice", "memo", "agreedToTerms", "priceTier", "status"
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
                a.applyWorkshop(),
                a.vehicleUsage().name(),
                nullToEmpty(a.vehicleNumber()),
                a.totalPrice(),
                nullToEmpty(a.memo()),
                a.agreedToTerms(),
                nullToEmpty(a.priceTier())
                // status 는 의도적으로 기록하지 않음 (운영자가 시트에서 직접 관리, append 시 빈 값 덮어쓰기 방지)
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
