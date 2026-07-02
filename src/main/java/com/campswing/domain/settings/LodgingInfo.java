package com.campswing.domain.settings;

/**
 * 숙박 안내(/lodging) 페이지의 카피·썸네일. 운영팀이 Settings 시트 LodgingInfo 탭(KV)에서 편집.
 * imagePath 는 /img/... (사이트 업로드) 또는 https://... (외부 직접 링크) 모두 허용.
 * bullet 은 비어 있으면 화면에서 자동 생략.
 */
public record LodgingInfo(
        String intro,
        String campsiteTitle,
        String campsiteDescription,
        String campsiteImage,
        String campsiteBullet1,
        String campsiteBullet2,
        String campsiteBullet3,
        String dormitoryTitle,
        String dormitoryDescription,
        String dormitoryImage,
        String dormitoryBullet1,
        String dormitoryBullet2,
        String dormitoryBullet3
) {
}
