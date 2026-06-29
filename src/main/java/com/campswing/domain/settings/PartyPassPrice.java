package com.campswing.domain.settings;

/**
 * 파티패스 가격표 한 행 (PARTY PASS PRICE). 운영팀이 Settings 시트에서 편집.
 * key 는 자동 계산 연결용 식별자(PRE_PARTY_ONLY/MAIN_ONLY/FULL/WORKSHOP),
 * label 은 화면 표시명, 금액은 원 단위 정수.
 */
public record PartyPassPrice(
        int displayOrder,
        String key,
        String label,
        int earlyBird,
        int standard,
        int onsite
) {
}
