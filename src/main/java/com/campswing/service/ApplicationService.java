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
import com.campswing.service.sheets.SheetsApplicationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {

    // ===== 요금 정책 (단위: 원). 템플릿의 자동 계산 표시와 값이 일치해야 함 =====
    /** 캠핑사이트 기본 이용료 (1사이트). */
    public static final int CAMPSITE_SITE_FEE = 35_000;
    /** 캠핑사이트 금요일 19:00 이후 선입실 추가요금. */
    public static final int CAMPSITE_EARLY_CHECKIN_FEE = 10_000;
    /** 도미토리 1박당 이용료. */
    public static final int DORMITORY_PER_NIGHT_FEE = 10_000;

    private final SheetsApplicationRepository repository;
    private final KstClock clock;

    public ApplicationService(SheetsApplicationRepository repository, KstClock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public ApplicationCreatedResponse submitPartyPass(PartyPassApplicationRequest req) {
        LocalDateTime submittedAt = clock.now();
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
                Boolean.TRUE.equals(req.useVehicle()),
                req.vehicleNumber(),
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

    /** 캠핑사이트 총액 = 사이트 이용료 + (금요일 선입실 시 추가요금). 픽업버스 비용은 미포함. */
    public static int campsiteTotalPrice(ArrivalTime arrivalTime) {
        int total = CAMPSITE_SITE_FEE;
        if (arrivalTime == ArrivalTime.FRI_EVENING) {
            total += CAMPSITE_EARLY_CHECKIN_FEE;
        }
        return total;
    }

    /** 도미토리 총액 = 1박당 이용료 × 박수 (2박만 2배, 1박류는 1배). */
    public static int dormitoryTotalPrice(Nights nights) {
        int multiplier = (nights == Nights.TWO_NIGHTS) ? 2 : 1;
        return DORMITORY_PER_NIGHT_FEE * multiplier;
    }
}
