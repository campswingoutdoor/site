package com.campswing.domain.application;

public enum VehicleUsage {
    NONE,      // 차량 이용 안 함 (+0)
    GENERAL,   // 일반 차량 이용 (+5,000원, 주차비)
    CAMPSITE   // 캠핑사이트 이용 차량 (+0, 캠핑사이트 신청자 1대 무료)
}
