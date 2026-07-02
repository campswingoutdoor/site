package com.campswing.service;

import com.campswing.api.dto.ApplicationCreatedResponse;
import com.campswing.api.dto.CampsiteApplicationRequest;
import com.campswing.api.dto.DormitoryApplicationRequest;
import com.campswing.api.dto.PartyPassApplicationRequest;
import com.campswing.common.util.KstClock;
import com.campswing.domain.application.ArrivalTime;
import com.campswing.domain.application.CampsiteApplication;
import com.campswing.domain.application.DormitoryApplication;
import com.campswing.domain.application.Gender;
import com.campswing.domain.application.Nights;
import com.campswing.domain.application.DanceRole;
import com.campswing.domain.application.PartyPassApplication;
import com.campswing.domain.application.PassType;
import com.campswing.domain.application.VehicleUsage;
import com.campswing.service.sheets.SheetsApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    private static final Instant FIXED_INSTANT = Instant.parse("2026-10-30T03:00:00Z");

    @Mock
    private SheetsApplicationRepository repository;

    @Mock
    private SettingsService settings;

    private ApplicationService service;

    private LocalDateTime expectedNow;

    @BeforeEach
    void setUp() throws Exception {
        Clock fixed = Clock.fixed(FIXED_INSTANT, KstClock.KST);
        Constructor<KstClock> ctor = KstClock.class.getDeclaredConstructor(Clock.class);
        ctor.setAccessible(true);
        KstClock clock = ctor.newInstance(fixed);
        service = new ApplicationService(repository, settings, clock);
        expectedNow = LocalDateTime.now(fixed);
    }

    @Test
    void submitPartyPass_assignsUuidAndKstTimestamp() {
        // 가격은 Settings 시트(파티패스 가격표) 연동 — 일반가/워크숍가 stub
        given(settings.partyPassCalcPrice("FULL")).willReturn(125_000);
        given(settings.partyPassCalcPrice("WORKSHOP")).willReturn(40_000);
        given(settings.partyPassPriceTier()).willReturn("STANDARD");

        PartyPassApplicationRequest req = new PartyPassApplicationRequest(
                "홍길동", "길동", "010-1234-5678", "hong@example.com",
                PassType.FULL, "스윙홀릭", DanceRole.LEADER,
                true, VehicleUsage.GENERAL, "12가 3456", null, true);

        ApplicationCreatedResponse response = service.submitPartyPass(req);

        ArgumentCaptor<PartyPassApplication> captor = ArgumentCaptor.forClass(PartyPassApplication.class);
        then(repository).should().savePartyPass(captor.capture());
        PartyPassApplication saved = captor.getValue();

        assertThat(saved.id()).isNotNull().hasSize(36);
        assertThat(saved.submittedAt()).isEqualTo(expectedNow);
        assertThat(saved.realName()).isEqualTo("홍길동");
        assertThat(saved.nickname()).isEqualTo("길동");
        assertThat(saved.passType()).isEqualTo(PassType.FULL);
        assertThat(saved.club()).isEqualTo("스윙홀릭");
        assertThat(saved.role()).isEqualTo(DanceRole.LEADER);
        assertThat(saved.applyWorkshop()).isTrue();
        assertThat(saved.vehicleUsage()).isEqualTo(VehicleUsage.GENERAL);
        assertThat(saved.vehicleNumber()).isEqualTo("12가 3456");
        // 올패스(125,000) + 워크숍(40,000) + 일반차량(5,000)
        assertThat(saved.totalPrice()).isEqualTo(170_000);
        assertThat(saved.priceTier()).isEqualTo("STANDARD");
        assertThat(saved.agreedToTerms()).isTrue();

        assertThat(response.applicationId()).isEqualTo(saved.id());
        assertThat(response.submittedAt().toLocalDateTime()).isEqualTo(expectedNow);
        assertThat(response.submittedAt().getOffset()).isEqualTo(KstClock.KST_OFFSET);
    }

    @Test
    void submitCampsite_persistsViaRepository() {
        CampsiteApplicationRequest req = new CampsiteApplicationRequest(
                "김캠퍼", "캠퍼", "010-2222-3333", "kim@example.com",
                3, ArrivalTime.FRI_EVENING,
                true, null, true);

        ApplicationCreatedResponse response = service.submitCampsite(req);

        ArgumentCaptor<CampsiteApplication> captor = ArgumentCaptor.forClass(CampsiteApplication.class);
        then(repository).should().saveCampsite(captor.capture());
        CampsiteApplication saved = captor.getValue();

        assertThat(saved.id()).isNotNull().hasSize(36);
        assertThat(saved.submittedAt()).isEqualTo(expectedNow);
        assertThat(saved.realName()).isEqualTo("김캠퍼");
        assertThat(saved.nickname()).isEqualTo("캠퍼");
        assertThat(saved.arrivalTime()).isEqualTo(ArrivalTime.FRI_EVENING);
        assertThat(saved.usePickupBus()).isTrue();
        // 금요일 선입실 → 35,000 + 10,000
        assertThat(saved.totalPrice()).isEqualTo(45_000);
        assertThat(response.applicationId()).isEqualTo(saved.id());
    }

    @Test
    void submitDormitory_persistsViaRepository() {
        DormitoryApplicationRequest req = new DormitoryApplicationRequest(
                "박도미", "도미", "010-9999-8888", "park@example.com",
                Gender.FEMALE, Nights.TWO_NIGHTS,
                null, true);

        ApplicationCreatedResponse response = service.submitDormitory(req);

        ArgumentCaptor<DormitoryApplication> captor = ArgumentCaptor.forClass(DormitoryApplication.class);
        then(repository).should().saveDormitory(captor.capture());
        DormitoryApplication saved = captor.getValue();

        assertThat(saved.id()).isNotNull().hasSize(36);
        assertThat(saved.submittedAt()).isEqualTo(expectedNow);
        assertThat(saved.realName()).isEqualTo("박도미");
        assertThat(saved.nickname()).isEqualTo("도미");
        assertThat(saved.gender()).isEqualTo(Gender.FEMALE);
        assertThat(saved.nights()).isEqualTo(Nights.TWO_NIGHTS);
        // 2박 정액 15,000
        assertThat(saved.totalPrice()).isEqualTo(15_000);
        assertThat(response.applicationId()).isEqualTo(saved.id());
    }
}
