package com.campswing.service;

import com.campswing.config.EventProperties;
import com.campswing.domain.settings.ApplyCard;
import com.campswing.domain.settings.ComingSoonItem;
import com.campswing.domain.settings.ConceptCopy;
import com.campswing.domain.settings.EventInfo;
import com.campswing.domain.settings.IndexHighlight;
import com.campswing.domain.settings.LocationGuide;
import com.campswing.domain.settings.PageMeta;
import com.campswing.domain.settings.VenueDetail;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Settings 시트 미설정·미생성·장애 시 폴백 값. 시트 운영팀이 카피를 옮기지 않아도 사이트가 동작하도록 보존.
 * 사이트의 현재 하드코딩 카피를 그대로 유지.
 */
final class SettingsFallbacks {

    private SettingsFallbacks() {}

    static EventInfo eventInfo(EventProperties p) {
        EventProperties.Venue main = p.getMainVenue() != null ? p.getMainVenue() : new EventProperties.Venue();
        EventProperties.Venue pre = p.getPrePartyVenue() != null ? p.getPrePartyVenue() : new EventProperties.Venue();
        return new EventInfo(
                p.getName(),
                "Swing Out Under The Stars",
                p.getStartDate(),
                p.getEndDate(),
                new EventInfo.Venue(main.getName(), main.getAddress()),
                new EventInfo.Venue(pre.getName(), pre.getAddress()),
                "contact@campswing.example",
                "@campswingoutdoor",
                "국민은행",
                "000-000-000000",
                "캠프스윙아웃도어",
                "",
                "Swing Dance · Camping · Music · Community",
                "신청자 이름 로 입금해주세요",
                "행사 2주 전 (운영팀 공지에 따름)"
        );
    }

    static Map<String, PageMeta> pageMetas() {
        Map<String, PageMeta> m = new LinkedHashMap<>();
        m.put("home",               new PageMeta("home",               "",                 "CAMP SWING OUTDOOR", "홈", ""));
        m.put("event.overview",     new PageMeta("event.overview",     "행사 개요",          "VENUE INTRODUCTION", "행사 개요", "Camp Swing Outdoor 2026 행사 개요 — 스윙댄스와 캠핑이 만나는 1박 2일 야외 이벤트."));
        m.put("event.venue",        new PageMeta("event.venue",        "행사장 소개",        "VENUE INTRODUCTION", "행사장 소개", "금요일 전야제(느티나무 카페)와 토요일 메인 행사장(상주우산오토캠핑장) 소개."));
        m.put("event.location",     new PageMeta("event.location",     "오시는 길",          "LOCATION GUIDE",     "오시는 길", "상주우산오토캠핑장 위치, 픽업버스 시간표, 대중교통 안내."));
        m.put("party-pass",         new PageMeta("party-pass",         "파티패스 안내·신청", "PARTY PASS",         "파티패스 안내·신청", ""));
        m.put("party-pass.list",    new PageMeta("party-pass.list",    "파티패스 신청 확인",  "APPLICATION LIST",   "파티패스 신청 확인", "파티패스 신청자 명단을 확인합니다."));
        m.put("lodging",            new PageMeta("lodging",            "숙박 안내",          "LODGING",            "숙박 안내", ""));
        m.put("lodging.campsite.list",  new PageMeta("lodging.campsite.list",  "캠핑사이트 신청 확인", "APPLICATION LIST", "캠핑사이트 신청 확인", "캠핑사이트 신청자 명단을 확인합니다."));
        m.put("lodging.dormitory.list", new PageMeta("lodging.dormitory.list", "도미토리 신청 확인",   "APPLICATION LIST", "도미토리 신청 확인",   "도미토리 신청자 명단을 확인합니다."));
        m.put("apply",              new PageMeta("apply",              "바로 신청하기",      "APPLY",              "바로 신청하기", ""));
        m.put("apply.success",      new PageMeta("apply.success",      "신청이 접수되었습니다", "APPLICATION COMPLETE", "신청 완료", ""));
        m.put("staff.dj",           new PageMeta("staff.dj",           "DJ 소개",            "DJ LINE-UP",         "DJ 소개", ""));
        m.put("staff.instructors",  new PageMeta("staff.instructors",  "강사 소개",          "INSTRUCTORS",        "강사 소개", ""));
        m.put("staff.staff",        new PageMeta("staff.staff",        "스태프 소개",        "STAFF",              "스태프 소개", ""));
        return m;
    }

    static List<IndexHighlight> indexHighlights() {
        return List.of(
                new IndexHighlight(1, "when",     "WHEN",      "", "금요일 전야제 · 토요일 메인"),
                new IndexHighlight(2, "where",    "WHERE",     "", ""),
                new IndexHighlight(3, "preParty", "PRE-PARTY", "", "10.30 BLUES PARTY")
        );
    }

    static List<VenueDetail> venueDetails() {
        return List.of(
                new VenueDetail(1, "preParty", "FRIDAY PRE-PARTY",     "금요일 전야제 행사장", "느티나무 카페",
                        "Cozy Blues Night at the Cafe. 은은한 조명과 빈티지한 분위기의 카페에서 열리는 BLUES PARTY.",
                        "10.30 · 19:00 ~"),
                new VenueDetail(2, "main",     "SATURDAY MAIN VENUE", "토요일 메인 행사장",   "상주우산오토캠핑장",
                        "낙동강을 끼고 펼쳐지는 아웃도어 오토캠핑장. 텐트·모닥불·DJ 부스를 갖춘 별빛 야외 스윙장으로 변신합니다.",
                        "")
        );
    }

    static List<ApplyCard> applyCards() {
        return List.of(
                new ApplyCard(1, "partyPass", "STEP 1",      "파티패스",   "전야제 + 메인 행사 입장 패스", "/party-pass"),
                new ApplyCard(2, "campsite",  "STEP 2 · A",  "캠핑사이트", "본인 텐트로 야영하는 분",      "/lodging/campsite"),
                new ApplyCard(3, "dormitory", "STEP 2 · B",  "도미토리",   "공용 숙소로 편하게 묵는 분",   "/lodging/dormitory")
        );
    }

    static LocationGuide locationGuide() {
        return new LocationGuide(
                "대중교통 안내",
                "동서울터미널 → 성주봉한방사우나 직통버스",
                "약 3시간",
                "도로명: 채룡산로 752",
                "PICKUP AVAILABLE",
                "은척 정류장 하차 → 행사장까지 차량 약 9분 · 사전 신청 시 픽업 가능",
                "※ 은척 하차 시 캠핑장까지 픽업 가능 (사전 신청 시).",
                "※ '건대'는 서울 건대가 아닌 충주 건국대 경유입니다."
        );
    }

    static ConceptCopy conceptCopy() {
        return new ConceptCopy(
                "스윙댄스 · 캠핑 · 음악 · 커뮤니티가 만나는 1박 2일 야외 이벤트.",
                "빛나는 조명, 따뜻한 모닥불, 그리고 별빛 아래의 스윙.",
                "1박 2일 · 전야제 + 메인 행사",
                "* 타임테이블은 운영상 사정에 따라 일부 변경될 수 있습니다."
        );
    }

    static Map<String, ComingSoonItem> comingSoon() {
        Map<String, ComingSoonItem> m = new LinkedHashMap<>();
        m.put("dj",          new ComingSoonItem("dj",          "COMING SOON", "DJ 라인업이 곧 공개될 예정입니다."));
        m.put("instructors", new ComingSoonItem("instructors", "COMING SOON", "스윙 댄스 워크숍 강사 라인업이 곧 공개될 예정입니다."));
        m.put("staff",       new ComingSoonItem("staff",       "COMING SOON", "행사를 만드는 운영팀 소개가 곧 공개될 예정입니다."));
        return m;
    }
}
