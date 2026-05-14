package com.campswing.domain.settings;

import java.util.List;
import java.util.Map;

/**
 * Settings 시트 한 번의 batchGet 결과를 묶은 스냅샷.
 * SettingsService.refresh()가 단일 API 호출로 모든 영역을 가져와 캐시에 분배할 때 사용.
 */
public record SettingsSnapshot(
        EventInfo event,
        List<ScheduleItem> schedule,
        List<PartyPassBenefit> benefits,
        List<PickupBusTrip> pickupBus,
        Map<String, PageMeta> pageMetas,
        List<IndexHighlight> indexHighlights,
        List<VenueDetail> venueDetails,
        List<ApplyCard> applyCards,
        LocationGuide locationGuide,
        ConceptCopy conceptCopy,
        Map<String, ComingSoonItem> comingSoon
) {
}
