package com.campswing.service;

import com.campswing.config.EventProperties;
import com.campswing.domain.settings.EventInfo;
import com.campswing.domain.settings.PartyPassBenefit;
import com.campswing.domain.settings.PickupBusTrip;
import com.campswing.domain.settings.ScheduleItem;
import com.campswing.service.sheets.SheetsSettingsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 정적 콘텐츠(이벤트 메타, 스케줄, 패스 혜택, 픽업버스)를 Google Sheets에서 읽어 캐싱.
 *
 * 캐시 정책:
 *   - 부팅 시점에 yml 폴백으로 즉시 채움 → 시트 호출 실패해도 사이트 동작
 *   - @Scheduled 주기로 백그라운드 갱신 (요청 경로엔 시트 호출 X — 항상 캐시 응답)
 *   - 주기는 settings.cache.refresh-interval-ms 환경변수로 제어
 *     · application-local.yml: 5_000  (5초, 거의 즉시)
 *     · application-prod.yml : 600_000 (10분, 안정적)
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

    public SettingsService(SheetsSettingsRepository repo, EventProperties fallback) {
        this.repo = repo;
        this.fallback = fallback;
    }

    @PostConstruct
    public void warmUp() {
        eventCache.set(fromProperties(fallback));
        refresh();
    }

    @Scheduled(fixedDelayString = "${settings.cache.refresh-interval-ms:600000}",
               initialDelayString = "${settings.cache.refresh-interval-ms:600000}")
    public void refresh() {
        try {
            EventInfo loaded = repo.readEvent();
            if (loaded != null && loaded.name() != null) {
                eventCache.set(loaded);
            }
        } catch (Exception e) {
            log.warn("Event settings refresh failed: {}", e.getMessage());
        }
        try {
            scheduleCache.set(repo.readSchedule());
        } catch (Exception e) {
            log.warn("Schedule refresh failed: {}", e.getMessage());
        }
        try {
            benefitsCache.set(repo.readBenefits());
        } catch (Exception e) {
            log.warn("PartyPassBenefit refresh failed: {}", e.getMessage());
        }
        try {
            pickupCache.set(repo.readPickupBus());
        } catch (Exception e) {
            log.warn("PickupBus refresh failed: {}", e.getMessage());
        }
    }

    public EventInfo event() {
        return eventCache.get();
    }

    public List<ScheduleItem> schedule() {
        return scheduleCache.get();
    }

    public List<PartyPassBenefit> partyPassBenefits() {
        return benefitsCache.get();
    }

    public List<PickupBusTrip> pickupBus() {
        return pickupCache.get();
    }

    private EventInfo fromProperties(EventProperties p) {
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
                "Swing Dance · Camping · Music · Community"
        );
    }
}
