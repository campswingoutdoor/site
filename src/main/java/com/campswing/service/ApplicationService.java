package com.campswing.service;

import com.campswing.api.dto.ApplicationCreatedResponse;
import com.campswing.api.dto.CampsiteApplicationRequest;
import com.campswing.api.dto.DormitoryApplicationRequest;
import com.campswing.api.dto.PartyPassApplicationRequest;
import com.campswing.common.util.KstClock;
import com.campswing.domain.application.CampsiteApplication;
import com.campswing.domain.application.CampsiteListItem;
import com.campswing.domain.application.DormitoryApplication;
import com.campswing.domain.application.DormitoryListItem;
import com.campswing.domain.application.PartyPassApplication;
import com.campswing.domain.application.PartyPassListItem;
import com.campswing.service.sheets.SheetsApplicationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {

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
                req.applicantName(),
                req.phone(),
                req.email(),
                req.passType(),
                req.partySize(),
                req.tshirtSize(),
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
                req.applicantName(),
                req.phone(),
                req.email(),
                req.partySize(),
                req.tentSize(),
                req.vehicleCount(),
                req.arrivalTime(),
                Boolean.TRUE.equals(req.usePickupBus()),
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
                req.applicantName(),
                req.phone(),
                req.email(),
                req.gender(),
                req.nights(),
                Boolean.TRUE.equals(req.usePickupBus()),
                req.roommatePreference(),
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
}
