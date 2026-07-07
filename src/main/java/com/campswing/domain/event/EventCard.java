package com.campswing.domain.event;

/**
 * 이벤트 카드 — Settings 스프레드시트 `Events` 탭에서 읽음 (KV 형식의 `Event` 탭과 별개).
 * 컬럼: id, title(이벤트명), imageUrl(대표이미지), description(설명),
 *      schedule(일시/장소), link(상세 링크), displayOrder
 */
public record EventCard(
        String id,
        String title,
        String imageUrl,
        String description,
        String schedule,
        String link,
        int displayOrder
) {
}
