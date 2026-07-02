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
import com.campswing.domain.settings.PartyPassBenefit;
import com.campswing.domain.settings.PartyPassPrice;
import com.campswing.domain.settings.PickupBusTrip;
import com.campswing.domain.settings.ScheduleItem;
import com.campswing.domain.settings.SettingsSnapshot;
import com.campswing.domain.settings.VenueDetail;
import com.campswing.service.sheets.SheetsSettingsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 정적 콘텐츠를 Google Sheets에서 읽어 캐싱.
 *
 * 캐시 정책:
 *   - 부팅 시 yml + 코드 폴백으로 즉시 채움 → 시트 unreachable해도 사이트 동작
 *   - @Scheduled 주기로 백그라운드 갱신 (요청 경로엔 시트 호출 X)
 *   - 주기는 settings.cache.refresh-interval-ms 환경변수로 제어 (local 5초, prod 10분)
 *   - 갱신 실패 시 기존 캐시 유지 — 시트 장애가 사이트 장애로 전염되지 않음
 */
@Service
public class SettingsService {

    private static final Logger log = LoggerFactory.getLogger(SettingsService.class);

    private final SheetsSettingsRepository repo;
    private final EventProperties fallback;

    private final AtomicReference<EventInfo> eventCache = new AtomicReference<>();
    private final AtomicReference<List<ScheduleItem>> scheduleCache = new AtomicReference<>(List.of());
    private final AtomicReference<List<PartyPassBenefit>> benefitsCache = new AtomicReference<>(List.of());
    private final AtomicReference<List<PickupBusTrip>> pickupCache = new AtomicReference<>(List.of());
    private final AtomicReference<Map<String, PageMeta>> pageMetaCache = new AtomicReference<>(Map.of());
    private final AtomicReference<List<IndexHighlight>> indexHighlightCache = new AtomicReference<>(List.of());
    private final AtomicReference<List<VenueDetail>> venueDetailCache = new AtomicReference<>(List.of());
    private final AtomicReference<List<ApplyCard>> applyCardCache = new AtomicReference<>(List.of());
    private final AtomicReference<LocationGuide> locationGuideCache = new AtomicReference<>();
    private final AtomicReference<ConceptCopy> conceptCopyCache = new AtomicReference<>();
    private final AtomicReference<Map<String, ComingSoonItem>> comingSoonCache = new AtomicReference<>(Map.of());
    private final AtomicReference<List<NoticeLine>> campsiteNoticeCache = new AtomicReference<>(List.of());
    private final AtomicReference<List<NoticeLine>> dormitoryNoticeCache = new AtomicReference<>(List.of());
    private final AtomicReference<List<PartyPassPrice>> partyPassPriceCache = new AtomicReference<>(List.of());
    private final AtomicReference<List<NoticeLine>> partyPassPriceNoteCache = new AtomicReference<>(List.of());
    private final AtomicReference<LodgingInfo> lodgingInfoCache = new AtomicReference<>();

    public SettingsService(SheetsSettingsRepository repo, EventProperties fallback) {
        this.repo = repo;
        this.fallback = fallback;
    }

    @PostConstruct
    public void warmUp() {
        // 부팅 시점에 코드 폴백 채움 → 시트 호출 실패해도 사이트 정상 동작
        eventCache.set(SettingsFallbacks.eventInfo(fallback));
        pageMetaCache.set(SettingsFallbacks.pageMetas());
        indexHighlightCache.set(SettingsFallbacks.indexHighlights());
        venueDetailCache.set(SettingsFallbacks.venueDetails());
        applyCardCache.set(SettingsFallbacks.applyCards());
        locationGuideCache.set(SettingsFallbacks.locationGuide());
        conceptCopyCache.set(SettingsFallbacks.conceptCopy());
        comingSoonCache.set(SettingsFallbacks.comingSoon());
        campsiteNoticeCache.set(SettingsFallbacks.campsiteNotice());
        dormitoryNoticeCache.set(SettingsFallbacks.dormitoryNotice());
        partyPassPriceCache.set(SettingsFallbacks.partyPassPrices());
        partyPassPriceNoteCache.set(SettingsFallbacks.partyPassPriceNotes());
        lodgingInfoCache.set(SettingsFallbacks.lodgingInfo());
        refresh();
    }

    @Scheduled(fixedDelayString = "${settings.cache.refresh-interval-ms:600000}",
               initialDelayString = "${settings.cache.refresh-interval-ms:600000}")
    public void refresh() {
        SettingsSnapshot snap;
        try {
            // 11개 시트를 batchGet 1회 호출로 가져옴 — quota 절약 (N → 1).
            snap = repo.readAll();
        } catch (Exception e) {
            log.warn("Settings refresh failed (kept previous cache): {}", e.getMessage());
            return;
        }

        if (snap.event() != null && snap.event().name() != null) {
            eventCache.set(snap.event());
        }
        scheduleCache.set(snap.schedule());
        benefitsCache.set(snap.benefits());
        pickupCache.set(snap.pickupBus());

        if (!snap.pageMetas().isEmpty())        pageMetaCache.set(snap.pageMetas());
        if (!snap.indexHighlights().isEmpty())  indexHighlightCache.set(snap.indexHighlights());
        if (!snap.venueDetails().isEmpty())     venueDetailCache.set(snap.venueDetails());
        if (!snap.applyCards().isEmpty())       applyCardCache.set(snap.applyCards());
        if (snap.locationGuide() != null && !isAllEmpty(snap.locationGuide())) {
            locationGuideCache.set(snap.locationGuide());
        }
        if (snap.conceptCopy() != null && !isAllEmpty(snap.conceptCopy())) {
            conceptCopyCache.set(snap.conceptCopy());
        }
        if (!snap.comingSoon().isEmpty())       comingSoonCache.set(snap.comingSoon());

        // 숙박 안내 문구는 메인 batch 밖에서 개별 갱신 — 신규 탭 미존재가 다른 설정 갱신을 막지 않도록 격리.
        // 각 read 실패 시 기존 캐시(코드 폴백) 유지.
        try {
            List<NoticeLine> campsite = repo.readCampsiteNotice();
            if (!campsite.isEmpty()) campsiteNoticeCache.set(campsite);
        } catch (Exception e) {
            log.debug("CampsiteNotice refresh skipped (kept cache): {}", e.getMessage());
        }
        try {
            List<NoticeLine> dormitory = repo.readDormitoryNotice();
            if (!dormitory.isEmpty()) dormitoryNoticeCache.set(dormitory);
        } catch (Exception e) {
            log.debug("DormitoryNotice refresh skipped (kept cache): {}", e.getMessage());
        }
        try {
            List<PartyPassPrice> prices = repo.readPartyPassPrices();
            if (!prices.isEmpty()) partyPassPriceCache.set(prices);
        } catch (Exception e) {
            log.debug("PartyPassPrice refresh skipped (kept cache): {}", e.getMessage());
        }
        try {
            List<NoticeLine> priceNotes = repo.readPartyPassPriceNotes();
            if (!priceNotes.isEmpty()) partyPassPriceNoteCache.set(priceNotes);
        } catch (Exception e) {
            log.debug("PartyPassPriceNote refresh skipped (kept cache): {}", e.getMessage());
        }
        try {
            LodgingInfo lodging = repo.readLodgingInfo();
            // 핵심 필드(캠핑 제목)가 채워진 경우에만 반영 — 빈 탭이 폴백 카피를 덮어쓰지 않도록
            if (lodging != null && lodging.campsiteTitle() != null && !lodging.campsiteTitle().isBlank()) {
                lodgingInfoCache.set(lodging);
            }
        } catch (Exception e) {
            log.debug("LodgingInfo refresh skipped (kept cache): {}", e.getMessage());
        }
    }

    private static boolean isAllEmpty(LocationGuide g) {
        return isBlank(g.transportTitle()) && isBlank(g.transportRoute()) && isBlank(g.duration())
                && isBlank(g.roadAddress()) && isBlank(g.pickupHeadline()) && isBlank(g.pickupDescription())
                && isBlank(g.pickupNote1()) && isBlank(g.pickupNote2());
    }

    private static boolean isAllEmpty(ConceptCopy c) {
        return isBlank(c.concept()) && isBlank(c.tagline()) && isBlank(c.subjectLine()) && isBlank(c.scheduleNote());
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    // ===== Getters =====

    public EventInfo event()                          { return eventCache.get(); }
    public List<ScheduleItem> schedule()              { return scheduleCache.get(); }
    public List<PartyPassBenefit> partyPassBenefits() { return benefitsCache.get(); }
    public List<PickupBusTrip> pickupBus()            { return pickupCache.get(); }
    public List<IndexHighlight> indexHighlights()     { return indexHighlightCache.get(); }
    public List<VenueDetail> venueDetails()           { return venueDetailCache.get(); }
    public List<ApplyCard> applyCards()               { return applyCardCache.get(); }
    public LocationGuide locationGuide()              { return locationGuideCache.get(); }
    public ConceptCopy conceptCopy()                  { return conceptCopyCache.get(); }
    public Map<String, ComingSoonItem> comingSoon()   { return comingSoonCache.get(); }
    public List<NoticeLine> campsiteNotice()          { return campsiteNoticeCache.get(); }
    public List<NoticeLine> dormitoryNotice()         { return dormitoryNoticeCache.get(); }
    public List<PartyPassPrice> partyPassPrices()     { return partyPassPriceCache.get(); }
    public List<NoticeLine> partyPassPriceNotes()     { return partyPassPriceNoteCache.get(); }
    public LodgingInfo lodgingInfo()                  { return lodgingInfoCache.get(); }

    /**
     * 자동 계산에 적용할 가격 등급 — Event 시트 partyPassPriceTier (EARLYBIRD/STANDARD/ONSITE).
     * 미설정·오타 시 STANDARD.
     */
    public String partyPassPriceTier() {
        EventInfo e = eventCache.get();
        String tier = (e == null) ? null : e.partyPassPriceTier();
        if (tier == null) return "STANDARD";
        String t = tier.trim().toUpperCase();
        return switch (t) {
            case "EARLYBIRD", "STANDARD", "ONSITE" -> t;
            default -> "STANDARD";
        };
    }

    /**
     * 파티패스 가격표에서 key(PRE_PARTY_ONLY/MAIN_ONLY/FULL/WORKSHOP)의 가격을
     * 현재 적용 등급(partyPassPriceTier)에 맞춰 조회. 없으면 0.
     */
    public int partyPassCalcPrice(String key) {
        if (key == null) return 0;
        String tier = partyPassPriceTier();
        return partyPassPriceCache.get().stream()
                .filter(p -> key.equalsIgnoreCase(p.key()))
                .map(p -> switch (tier) {
                    case "EARLYBIRD" -> p.earlyBird();
                    case "ONSITE" -> p.onsite();
                    default -> p.standard();
                })
                .findFirst()
                .orElse(0);
    }

    public PageMeta pageMeta(String key) {
        Map<String, PageMeta> map = pageMetaCache.get();
        PageMeta meta = map.get(key);
        if (meta != null) return meta;
        // 시트에 키가 없으면 코드 폴백에서 찾기
        return SettingsFallbacks.pageMetas().getOrDefault(key,
                new PageMeta(key, "", "", "", ""));
    }

    public ComingSoonItem comingSoonFor(String key) {
        ComingSoonItem item = comingSoonCache.get().get(key);
        if (item != null) return item;
        return SettingsFallbacks.comingSoon().getOrDefault(key,
                new ComingSoonItem(key, "COMING SOON", ""));
    }
}
