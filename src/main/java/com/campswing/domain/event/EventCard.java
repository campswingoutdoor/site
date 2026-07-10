package com.campswing.domain.event;

/**
 * 이벤트 카드 — Settings 스프레드시트 `Events` 탭에서 읽음 (KV 형식의 `Event` 탭과 별개).
 * 목록(Todo-list) + 상세 페이지 공용 뷰모델.
 * 컬럼: id, status(진행중/종료), title(이벤트명), summary(목록용 간단 설명),
 *      period(기간), imageUrl(상세 배너·비면 ComingSoon), description(상세 안내 본문),
 *      howTo(참여 방법·줄바꿈 여러 줄), ctaLabel(버튼 텍스트), ctaLink(버튼 링크), displayOrder
 */
public record EventCard(
        String id,
        String status,
        String title,
        String summary,
        String period,
        String imageUrl,
        String description,
        String howTo,
        String ctaLabel,
        String ctaLink,
        int displayOrder
) {
    /** 진행중 여부 — status 문자열에 '진행'이 포함되면 true (태그 색상·정렬용). */
    public boolean isOngoing() {
        return status != null && status.contains("진행");
    }
}
