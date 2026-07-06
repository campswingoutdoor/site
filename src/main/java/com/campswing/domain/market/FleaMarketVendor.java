package com.campswing.domain.market;

/**
 * 플리마켓 셀러(부스) 카드 — Settings 스프레드시트 `FleaMarket` 탭에서 읽음.
 * 컬럼: id, shopName(상호명), imageUrl(대표이미지), productIntro(판매제품소개),
 *      tagline(한 줄 소개), promoLink(홍보 링크/태그 계정), displayOrder
 */
public record FleaMarketVendor(
        String id,
        String shopName,
        String imageUrl,
        String productIntro,
        String tagline,
        String promoLink,
        int displayOrder
) {
}
