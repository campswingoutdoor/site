package com.campswing.service;

import com.campswing.api.dto.ApplicationCreatedResponse;
import com.campswing.api.dto.CampsiteApplicationRequest;
import com.campswing.api.dto.DormitoryApplicationRequest;
import com.campswing.api.dto.PartyPassApplicationRequest;
import com.campswing.common.util.KstClock;
import com.campswing.domain.application.ArrivalTime;
import com.campswing.domain.application.CampsiteApplication;
import com.campswing.domain.application.CampsiteListItem;
import com.campswing.domain.application.DormitoryApplication;
import com.campswing.domain.application.DormitoryListItem;
import com.campswing.domain.application.Nights;
import com.campswing.domain.application.PartyPassApplication;
import com.campswing.domain.application.PartyPassListItem;
import com.campswing.domain.application.PassType;
import com.campswing.domain.application.VehicleUsage;
import com.campswing.service.sheets.SheetsApplicationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {

    // ===== 요금 정책 (단위: 원). 템플릿의 자동 계산 표시와 값이 일치해야 함 =====
    // 파티패스 — 일반(standard) 가격 기준 자동 계산
    /** 전야제(PRE_PARTY_ONLY) 일반가. */
    public static final int PARTY_PASS_PRE_PARTY_PRICE = 35_000;
    /** 본파티(MAIN_ONLY) 일반가. */
    public static final int PARTY_PASS_MAIN_PRICE = 100_000;
    /** 올패스(FULL) 일반가. */
    public static final int PARTY_PASS_FULL_PRICE = 125_000;
    /** 워크숍 추가 요금. */
    public static final int WORKSHOP_FEE = 40_000;
    /** 일반 차량 이용 주차비 (캠핑사이트 이용 차량은 무료). */
    public static final int PARTY_PASS_VEHICLE_GENERAL_FEE = 5_000;

    /** 캠핑사이트 기본 이용료 (1사이트). */
    public static final int CAMPSITE_SITE_FEE = 35_000;
    /** 캠핑사이트 금요일 19:00 이후 선입실 추가요금. */
    public static final int CAMPSITE_EARLY_CHECKIN_FEE = 10_000;
    /** 도미토리 1박 이용료 (전야제/본파티 공통). */
    public static final int DORMITORY_ONE_NIGHT_FEE = 10_000;
    /** 도미토리 2박(전야제 + 본파티) 이용료 (정액). */
    public static final int DORMITORY_TWO_NIGHTS_FEE = 15_000;

    private final SheetsApplicationRepository repository;
    private final SettingsService settings;
    private final KstClock clock;

    public ApplicationService(SheetsApplicationRepository repository, SettingsService settings, KstClock clock) {
        this.repository = repository;
        this.settings = settings;
        this.clock = clock;
    }

    public ApplicationCreatedResponse submitPartyPass(PartyPassApplicationRequest req) {
        LocalDateTime submittedAt = clock.now();
        boolean applyWorkshop = Boolean.TRUE.equals(req.applyWorkshop());
        VehicleUsage vehicleUsage = req.vehicleUsage() != null ? req.vehicleUsage() : VehicleUsage.NONE;
        PartyPassApplication app = new PartyPassApplication(
                UUID.randomUUID().toString(),
                submittedAt,
                req.realName(),
                req.nickname(),
                req.phone(),
                req.email(),
                req.passType(),
                req.club(),
                req.role(),
                applyWorkshop,
                vehicleUsage,
                vehicleUsage == VehicleUsage.NONE ? null : req.vehicleNumber(),
                partyPassTotalPrice(req.passType(), applyWorkshop, vehicleUsage),  // 일반가 기준 (Settings 시트 연동)
                req.dietaryNote(),
                req.memo(),
                Boolean.TRUE.equals(req.agreedToTerms())
        );
        repository.savePartyPass(app);
        return new ApplicationCreatedResponse(app.id(), app.submittedAt().atOffset(KstClock.KST_OFFSET));
    }

    public ApplicationCreatedResponse submitCampsite(CampsiteApplicationRequest req) {
        LocalDateTime submittedAt = clock.now();
        CampsiteApplication app = new CampsiteApplication(
                UUID.randomUUID().toString(),
                submittedAt,
                req.realName(),
                req.nickname(),
                req.phone(),
                req.email(),
                req.partySize(),
                req.arrivalTime(),
                Boolean.TRUE.equals(req.usePickupBus()),
                campsiteTotalPrice(req.arrivalTime()),
                req.memo(),
                Boolean.TRUE.equals(req.agreedToTerms())
        );
        repository.saveCampsite(app);
        return new ApplicationCreatedResponse(app.id(), app.submittedAt().atOffset(KstClock.KST_OFFSET));
    }

    public ApplicationCreatedResponse submitDormitory(DormitoryApplicationRequest req) {
        LocalDateTime submittedAt = clock.now();
        DormitoryApplication app = new DormitoryApplication(
                UUID.randomUUID().toString(),
                submittedAt,
                req.realName(),
                req.nickname(),
                req.phone(),
                req.email(),
                req.gender(),
                req.nights(),
                dormitoryTotalPrice(req.nights()),
                req.memo(),
                Boolean.TRUE.equals(req.agreedToTerms())
        );
        repository.saveDormitory(app);
        return new ApplicationCreatedResponse(app.id(), app.submittedAt().atOffset(KstClock.KST_OFFSET));
    }

    public List<PartyPassListItem> listPartyPass() {
        return repository.findAllPartyPass();
    }

    public List<CampsiteListItem> listCampsite() {
        return repository.findAllCampsite();
    }

    public List<DormitoryListItem> listDormitory() {
        return repository.findAllDormitory();
    }

    /** 파티패스 총액 = 패스 일반가 + (워크숍 추가) + (일반 차량 주차비). 가격은 Settings 시트 연동. */
    public int partyPassTotalPrice(PassType passType, boolean applyWorkshop, VehicleUsage vehicleUsage) {
        int total = partyPassBasePrice(passType);
        if (applyWorkshop) {
            total += partyPassWorkshopFee();
        }
        if (vehicleUsage == VehicleUsage.GENERAL) {
            total += PARTY_PASS_VEHICLE_GENERAL_FEE;
        }
        return total;
    }

    /** 패스 종류별 가격 — Settings 시트 + 적용 등급(partyPassPriceTier) 기준. 없으면 코드 폴백(일반가). */
    public int partyPassBasePrice(PassType passType) {
        if (passType == null) return 0;
        int fromSheet = settings.partyPassCalcPrice(passType.name());
        if (fromSheet > 0) return fromSheet;
        return switch (passType) {
            case PRE_PARTY_ONLY -> PARTY_PASS_PRE_PARTY_PRICE;
            case MAIN_ONLY -> PARTY_PASS_MAIN_PRICE;
            case FULL -> PARTY_PASS_FULL_PRICE;
        };
    }

    /** 워크숍 추가 요금 — Settings 시트(WORKSHOP) + 적용 등급 기준. 없으면 코드 폴백. */
    public int partyPassWorkshopFee() {
        int fromSheet = settings.partyPassCalcPrice("WORKSHOP");
        return fromSheet > 0 ? fromSheet : WORKSHOP_FEE;
    }

    /** 캠핑사이트 총액 = 사이트 이용료 + (금요일 선입실 시 추가요금). 픽업버스 비용은 미포함. */
    public static int campsiteTotalPrice(ArrivalTime arrivalTime) {
        int total = CAMPSITE_SITE_FEE;
        if (arrivalTime == ArrivalTime.FRI_EVENING) {
            total += CAMPSITE_EARLY_CHECKIN_FEE;
        }
        return total;
    }

    /** 도미토리 총액 = 1박 10,000원 / 2박 15,000원(정액). */
    public static int dormitoryTotalPrice(Nights nights) {
        return (nights == Nights.TWO_NIGHTS) ? DORMITORY_TWO_NIGHTS_FEE : DORMITORY_ONE_NIGHT_FEE;
    }
}
