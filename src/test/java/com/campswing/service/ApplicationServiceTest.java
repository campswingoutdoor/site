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
import com.campswing.domain.application.PartyPassApplication;
import com.campswing.domain.application.PassType;
import com.campswing.domain.application.TentSize;
import com.campswing.domain.application.TshirtSize;
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
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    private static final Instant FIXED_INSTANT = Instant.parse("2026-10-30T03:00:00Z");

    @Mock
    private SheetsApplicationRepository repository;

    private ApplicationService service;

    private LocalDateTime expectedNow;

    @BeforeEach
    void setUp() throws Exception {
        Clock fixed = Clock.fixed(FIXED_INSTANT, KstClock.KST);
        Constructor<KstClock> ctor = KstClock.class.getDeclaredConstructor(Clock.class);
        ctor.setAccessible(true);
        KstClock clock = ctor.newInstance(fixed);
        service = new ApplicationService(repository, clock);
        expectedNow = LocalDateTime.now(fixed);
    }

    @Test
    void submitPartyPass_assignsUuidAndKstTimestamp() {
        PartyPassApplicationRequest req = new PartyPassApplicationRequest(
                "홍길동", "010-1234-5678", "hong@example.com",
                PassType.FULL, 2, TshirtSize.M,
                null, null, true);

        ApplicationCreatedResponse response = service.submitPartyPass(req);

        ArgumentCaptor<PartyPassApplication> captor = ArgumentCaptor.forClass(PartyPassApplication.class);
        then(repository).should().savePartyPass(captor.capture());
        PartyPassApplication saved = captor.getValue();

        assertThat(saved.id()).isNotNull().hasSize(36);
        assertThat(saved.submittedAt()).isEqualTo(expectedNow);
        assertThat(saved.applicantName()).isEqualTo("홍길동");
        assertThat(saved.passType()).isEqualTo(PassType.FULL);
        assertThat(saved.agreedToTerms()).isTrue();

        assertThat(response.applicationId()).isEqualTo(saved.id());
        assertThat(response.submittedAt().toLocalDateTime()).isEqualTo(expectedNow);
        assertThat(response.submittedAt().getOffset()).isEqualTo(KstClock.KST_OFFSET);
    }

    @Test
    void submitCampsite_persistsViaRepository() {
        CampsiteApplicationRequest req = new CampsiteApplicationRequest(
                "김캠퍼", "010-2222-3333", "kim@example.com",
                3, TentSize.MEDIUM, 1, ArrivalTime.SAT_MORNING,
                true, null, true);

        ApplicationCreatedResponse response = service.submitCampsite(req);

        ArgumentCaptor<CampsiteApplication> captor = ArgumentCaptor.forClass(CampsiteApplication.class);
        then(repository).should().saveCampsite(captor.capture());
        CampsiteApplication saved = captor.getValue();

        assertThat(saved.id()).isNotNull().hasSize(36);
        assertThat(saved.submittedAt()).isEqualTo(expectedNow);
        assertThat(saved.tentSize()).isEqualTo(TentSize.MEDIUM);
        assertThat(saved.usePickupBus()).isTrue();
        assertThat(response.applicationId()).isEqualTo(saved.id());
    }

    @Test
    void submitDormitory_persistsViaRepository() {
        DormitoryApplicationRequest req = new DormitoryApplicationRequest(
                "박도미", "010-9999-8888", "park@example.com",
                Gender.FEMALE, Nights.ONE_NIGHT, false,
                "조용한 룸메 선호", null, true);

        ApplicationCreatedResponse response = service.submitDormitory(req);

        ArgumentCaptor<DormitoryApplication> captor = ArgumentCaptor.forClass(DormitoryApplication.class);
        then(repository).should().saveDormitory(captor.capture());
        DormitoryApplication saved = captor.getValue();

        assertThat(saved.id()).isNotNull().hasSize(36);
        assertThat(saved.submittedAt()).isEqualTo(expectedNow);
        assertThat(saved.gender()).isEqualTo(Gender.FEMALE);
        assertThat(saved.nights()).isEqualTo(Nights.ONE_NIGHT);
        assertThat(saved.usePickupBus()).isFalse();
        assertThat(response.applicationId()).isEqualTo(saved.id());
    }
}
