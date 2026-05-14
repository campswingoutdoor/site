package com.campswing.service.sheets;

import com.campswing.common.exception.SheetsApiException;
import com.campswing.domain.application.CampsiteApplication;
import com.campswing.domain.application.CampsiteListItem;
import com.campswing.domain.application.DormitoryApplication;
import com.campswing.domain.application.DormitoryListItem;
import com.campswing.domain.application.PartyPassApplication;
import com.campswing.domain.application.PartyPassListItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * PartyPass 시트의 모든 신청 행을 읽어 리스트 뷰모델로 반환.
     * 1행은 헤더(영문 컬럼명)로 가정하여 A2:K부터 읽음. 추가 한글 라벨 행(1행 한글, 2행 영문)이
     * 있어도 헤더 행이면 id 값이 비어있어 스킵된다.
     */
    public List<PartyPassListItem> findAllPartyPass() {
        List<List<Object>> rows = client.readRange(SHEET_PARTY_PASS, "A2:K");
        List<PartyPassListItem> result = new ArrayList<>(rows.size());
        int seq = 0;
        for (List<Object> r : rows) {
            String id = cell(r, 0);
            if (id.isBlank()) continue; // 빈 행 또는 헤더 잔재 스킵
            if ("id".equalsIgnoreCase(id)) continue;
            seq++;
            result.add(new PartyPassListItem(
                    seq,
                    cell(r, 1),  // submittedAt
                    cell(r, 2),  // applicantName
                    cell(r, 5),  // passType
                    cell(r, 7),  // tshirtSize
                    cell(r, 8),  // dietaryNote
                    cell(r, 9)   // memo
            ));
        }
        return result;
    }

    /**
     * Campsite 시트의 모든 신청 행을 읽어 리스트 뷰모델로 반환.
     * 컬럼 순서: id, submittedAt, applicantName, phone, email, partySize, tentSize,
     *           vehicleCount, arrivalTime, usePickupBus, memo, agreedToTerms (A2:L)
     */
    public List<CampsiteListItem> findAllCampsite() {
        List<List<Object>> rows = client.readRange(SHEET_CAMPSITE, "A2:L");
        List<CampsiteListItem> result = new ArrayList<>(rows.size());
        int seq = 0;
        for (List<Object> r : rows) {
            String id = cell(r, 0);
            if (id.isBlank()) continue;
            if ("id".equalsIgnoreCase(id)) continue;
            seq++;
            result.add(new CampsiteListItem(
                    seq,
                    cell(r, 1),  // submittedAt
                    cell(r, 2),  // applicantName
                    cell(r, 5),  // partySize
                    cell(r, 6),  // tentSize
                    cell(r, 7),  // vehicleCount
                    cell(r, 8),  // arrivalTime
                    cell(r, 9),  // usePickupBus
                    cell(r, 10)  // memo
            ));
        }
        return result;
    }

    /**
     * Dormitory 시트의 모든 신청 행을 읽어 리스트 뷰모델로 반환.
     * 컬럼 순서: id, submittedAt, applicantName, phone, email, gender, nights,
     *           usePickupBus, roommatePreference, memo, agreedToTerms (A2:K)
     */
    public List<DormitoryListItem> findAllDormitory() {
        List<List<Object>> rows = client.readRange(SHEET_DORMITORY, "A2:K");
        List<DormitoryListItem> result = new ArrayList<>(rows.size());
        int seq = 0;
        for (List<Object> r : rows) {
            String id = cell(r, 0);
            if (id.isBlank()) continue;
            if ("id".equalsIgnoreCase(id)) continue;
            seq++;
            result.add(new DormitoryListItem(
                    seq,
                    cell(r, 1),  // submittedAt
                    cell(r, 2),  // applicantName
                    cell(r, 5),  // gender
                    cell(r, 6),  // nights
                    cell(r, 7),  // usePickupBus
                    cell(r, 8),  // roommatePreference
                    cell(r, 9)   // memo
            ));
        }
        return result;
    }

    private static String cell(List<Object> r, int idx) {
        if (idx >= r.size()) return "";
        Object v = r.get(idx);
        return v == null ? "" : v.toString().trim();
    }

    private void appendOrFail(String sheetName, List<Object> row, String applicationId) {
        try {
            client.appendRow(sheetName, row);
            log.info("Application appended to sheet '{}' id={}", sheetName, applicationId);
        } catch (SheetsApiException e) {
            log.error("Failed to append application to sheet '{}' id={}: {}", sheetName, applicationId, e.getMessage());
            throw e;
        }
    }
}
