package com.campswing.domain.application;

/**
 * 파티패스 신청 리스트 뷰모델 — Sheets 행을 그대로 문자열로 노출.
 * enum 파싱 실패가 페이지 렌더를 깨지 않도록 String 으로 보관.
 */
public record PartyPassListItem(
        int seq,
        String submittedAt,
        String realName,
        String nickname,
        String passType,
        String club,
        String role,
        String useVehicle,
        String vehicleNumber,
        String dietaryNote,
        String memo
) {
}
