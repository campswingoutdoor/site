package com.campswing.service;

import com.campswing.config.EventProperties;
import com.campswing.domain.settings.ApplyCard;
import com.campswing.domain.settings.ComingSoonItem;
import com.campswing.domain.settings.ConceptCopy;
import com.campswing.domain.settings.EventInfo;
import com.campswing.domain.settings.IndexHighlight;
import com.campswing.domain.settings.LocationGuide;
import com.campswing.domain.settings.LodgingInfo;
import com.campswing.domain.settings.NoticeLine;
import com.campswing.domain.settings.PageMeta;
import com.campswing.domain.settings.PartyPassPrice;
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
                "카카오뱅크",
                "3333-34-5645832",
                "장국찬",
                "",
                "Swing Dance · Camping · Music · Community",
                "신청자 이름 로 입금해주세요",
                "행사 2주 전 (운영팀 공지에 따름)",
                "STANDARD"
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
        m.put("staff.invited-dancers", new PageMeta("staff.invited-dancers", "Invited Dancers", "INVITED DANCERS", "Invited Dancers", "Camp Swing Outdoor 2026 초대 댄서 — Legacy Dancers & Special Guest Dancers 소개."));
        m.put("staff.dj",           new PageMeta("staff.dj",           "DJ 소개",            "DJ LINE-UP",         "DJ 소개", ""));
        m.put("staff.instructors",  new PageMeta("staff.instructors",  "강사 소개",          "INSTRUCTORS",        "강사 소개", ""));
        m.put("staff.staff",        new PageMeta("staff.staff",        "스태프 소개",        "STAFF",              "스태프 소개", ""));
        m.put("flea-market",        new PageMeta("flea-market",        "플리마켓",           "FLEA MARKET",        "플리마켓", "Camp Swing Outdoor 2026 플리마켓 — 춤추는 사람들을 위한 셀러들의 부스를 소개합니다."));
        m.put("events",             new PageMeta("events",             "이벤트",             "EVENTS",             "이벤트", "Camp Swing Outdoor 2026 이벤트 — 행사 기간 중 진행되는 다양한 이벤트를 소개합니다."));
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
                        "10.30 · 19:00 ~", "/img/venue-preparty.jpeg"),
                new VenueDetail(2, "main",     "SATURDAY MAIN VENUE", "토요일 메인 행사장",   "상주우산오토캠핑장",
                        "낙동강을 끼고 펼쳐지는 아웃도어 오토캠핑장. 텐트·모닥불·DJ 부스를 갖춘 별빛 야외 스윙장으로 변신합니다.",
                        "", "/img/hero-full.jpeg")
        );
    }

    static List<ApplyCard> applyCards() {
        return List.of(
                new ApplyCard(1, "partyPass", "STEP 1",      "파티패스",   "전야제 + 메인 행사 입장 패스", "/party-pass",         "/img/hero-main.jpeg"),
                new ApplyCard(2, "campsite",  "STEP 2 · A",  "캠핑사이트", "본인 텐트로 야영하는 분",      "/lodging/campsite",   "/img/venue-intro.jpeg"),
                new ApplyCard(3, "dormitory", "STEP 2 · B",  "도미토리",   "공용 숙소로 편하게 묵는 분",   "/lodging/dormitory",  "/img/venue-preparty.jpeg")
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
        m.put("legacyDancers",       new ComingSoonItem("legacyDancers",       "COMING SOON", "초대 댄서 라인업이 곧 공개될 예정입니다."));
        m.put("specialGuestDancers", new ComingSoonItem("specialGuestDancers", "COMING SOON", "스페셜 게스트 댄서가 곧 공개될 예정입니다."));
        m.put("fleaMarket",  new ComingSoonItem("fleaMarket",  "COMING SOON", "플리마켓 셀러 라인업이 곧 공개될 예정입니다."));
        m.put("events",      new ComingSoonItem("events",      "COMING SOON", "행사 기간 중 진행되는 이벤트가 곧 공개될 예정입니다."));
        return m;
    }

    static List<NoticeLine> campsiteNotice() {
        return List.of(
                new NoticeLine(1, "캠핑 사이트 크기 : 10m × 10m"),
                new NoticeLine(2, "대부분의 텐트 설치가 가능하므로 텐트 크기 입력은 받지 않습니다."),
                new NoticeLine(3, "캠핑 사이트 이용료 : 35,000원 / 1사이트"),
                new NoticeLine(4, "1사이트 최대 이용 가능 인원 : 4명"),
                new NoticeLine(5, "토요일 오전 8시부터 입실 가능합니다."),
                new NoticeLine(6, "금요일 19:00 이후 선입실 시 추가요금 10,000원이 발생합니다."),
                new NoticeLine(7, "차량(주차) 등록 및 주차 신청은 메인 참가 신청(파티패스) 페이지에서 진행됩니다.")
        );
    }

    static List<PartyPassPrice> partyPassPrices() {
        return List.of(
                new PartyPassPrice(1, "PRE_PARTY_ONLY", "전야제 (뒤풀이 포함)",        30_000,  35_000,  40_000),
                new PartyPassPrice(2, "MAIN_ONLY",      "본파티 (뒤풀이 포함)",        95_000, 100_000, 110_000),
                new PartyPassPrice(3, "FULL",           "올패스 (전야제 + 본파티)",   120_000, 125_000, 135_000),
                new PartyPassPrice(4, "WORKSHOP",       "워크숍 (선택)",               35_000,  40_000,  45_000)
        );
    }

    static List<NoticeLine> partyPassPriceNotes() {
        return List.of(
                new NoticeLine(1, "얼리버드는 선착순 50명 한정입니다. (단위: 원)"),
                new NoticeLine(2, "신청 폼의 자동 결제금액은 일반가 기준으로 계산됩니다.")
        );
    }

    static List<NoticeLine> dormitoryNotice() {
        return List.of(
                new NoticeLine(1, "도미토리는 남녀 구분 배정으로 운영됩니다."),
                new NoticeLine(2, "개인 침구류(이불, 베개, 매트 등)는 제공되지 않습니다."),
                new NoticeLine(3, "반드시 개인 침구류를 준비해 주세요."),
                new NoticeLine(4, "도미토리 이용료 : 1인 10,000원"),
                new NoticeLine(5, "전야제 참가자도 안전한 귀가를 위해 도미토리 이용이 가능합니다."),
                new NoticeLine(6, "차량(주차) 이용 시 주차비는 1일 5,000원입니다."),
                new NoticeLine(7, "차량(주차) 등록은 메인 참가 신청 페이지에서 진행됩니다.")
        );
    }

    static LodgingInfo lodgingInfo() {
        return new LodgingInfo(
                "토요일 메인 행사가 끝나면 캠핑장에서 별빛 아래 1박. 두 가지 숙박 옵션 중 선택해주세요.",
                "캠핑사이트",
                "본인 캠핑 장비(텐트·침낭 등)를 지참하는 옵션. 자유롭게 사이트를 꾸미고 모닥불·별빛과 함께 야영하는 정통 캠핑 경험.",
                "",
                "캠핑 사이트 10m × 10m · 1사이트 최대 4명",
                "입실 시간 선택 (토 오전 8시 이후 / 금 19:00 이후 +10,000원)",
                "이용료 35,000원 / 1사이트 · 픽업버스 이용 가능",
                "도미토리",
                "장비가 없거나 편안한 공용 숙소를 원하는 분을 위한 옵션. 운영팀이 마련한 도미토리에서 같은 행사를 즐긴 친구들과 1박.",
                "",
                "성별 구분 배정 · 개인 침구류 지참",
                "1박(전야제/본파티) 또는 2박 선택",
                "이용료 1인 10,000원 (1박당)"
        );
    }
}
