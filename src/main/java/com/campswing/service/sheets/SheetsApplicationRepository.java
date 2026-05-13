package com.campswing.service.sheets;

import com.campswing.common.exception.SheetsApiException;
import com.campswing.domain.application.CampsiteApplication;
import com.campswing.domain.application.DormitoryApplication;
import com.campswing.domain.application.PartyPassApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class SheetsApplicationRepository {

    private static final Logger log = LoggerFactory.getLogger(SheetsApplicationRepository.class);

    private static final String SHEET_PARTY_PASS = "PartyPass";
    private static final String SHEET_CAMPSITE = "Campsite";
    private static final String SHEET_DORMITORY = "Dormitory";

    private final GoogleSheetsClient client;

    public SheetsApplicationRepository(GoogleSheetsClient client) {
        this.client = client;
    }

    public void savePartyPass(PartyPassApplication app) {
        appendOrFail(SHEET_PARTY_PASS, SheetRowMapper.toRow(app), app.id());
    }

    public void saveCampsite(CampsiteApplication app) {
        appendOrFail(SHEET_CAMPSITE, SheetRowMapper.toRow(app), app.id());
    }

    public void saveDormitory(DormitoryApplication app) {
        appendOrFail(SHEET_DORMITORY, SheetRowMapper.toRow(app), app.id());
    }

    private void appendOrFail(String sheetName, java.util.List<Object> row, String applicationId) {
        try {
            client.appendRow(sheetName, row);
            log.info("Application appended to sheet '{}' id={}", sheetName, applicationId);
        } catch (SheetsApiException e) {
            log.error("Failed to append application to sheet '{}' id={}: {}", sheetName, applicationId, e.getMessage());
            throw e;
        }
    }
}
