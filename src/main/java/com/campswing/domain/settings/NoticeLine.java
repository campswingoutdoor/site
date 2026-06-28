package com.campswing.domain.settings;

/**
 * 숙박(캠핑/도미토리) 안내 문구 한 줄. 운영팀이 Settings 시트에서 자유롭게 편집.
 * displayOrder 로 정렬, text 가 실제 노출 문구.
 */
public record NoticeLine(int displayOrder, String text) {
}
