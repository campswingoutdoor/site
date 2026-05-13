package com.campswing.service.sheets;

import com.campswing.common.exception.SheetsApiException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Component
public class GoogleSheetsClient {

    private static final Logger log = LoggerFactory.getLogger(GoogleSheetsClient.class);

    private final Sheets sheets;
    private final String spreadsheetId;
    private final String settingsSpreadsheetId;
    private final boolean enabled;

    public GoogleSheetsClient(
            @Value("${google.sheets.spreadsheet-id:}") String spreadsheetId,
            @Value("${google.sheets.settings-spreadsheet-id:}") String settingsSpreadsheetId,
            @Value("${google.sheets.credentials-location:}") String credentialsLocation,
            @Value("${google.sheets.credentials-json:}") String credentialsJsonBase64
    ) {
        this.spreadsheetId = spreadsheetId;
        // 미설정 시 신청 시트 ID로 폴백 — 단일 스프레드시트 운영도 가능
        this.settingsSpreadsheetId = (settingsSpreadsheetId != null && !settingsSpreadsheetId.isBlank())
                ? settingsSpreadsheetId : spreadsheetId;
        Sheets initialized = null;
        boolean ready = false;

        if (spreadsheetId == null || spreadsheetId.isBlank()) {
            log.warn("Google Sheets disabled: GOOGLE_SHEET_ID is not set. " +
                    "Form submissions will fail with SHEETS_API_ERROR until configured.");
        } else {
            try {
                GoogleCredentials credentials = loadCredentials(credentialsLocation, credentialsJsonBase64)
                        .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
                initialized = new Sheets.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        new HttpCredentialsAdapter(credentials)
                ).setApplicationName("camp-swing-outdoor").build();
                ready = true;
                log.info("Google Sheets client initialized for spreadsheetId={}", spreadsheetId);
            } catch (Exception e) {
                log.warn("Google Sheets disabled: failed to initialize client ({}). " +
                        "Form submissions will fail with SHEETS_API_ERROR until configured.", e.getMessage());
            }
        }
        this.sheets = initialized;
        this.enabled = ready;
    }

    private GoogleCredentials loadCredentials(String location, String base64Json) throws IOException {
        InputStream is;
        if (base64Json != null && !base64Json.isBlank()) {
            is = new ByteArrayInputStream(Base64.getDecoder().decode(base64Json));
        } else if (location != null && !location.isBlank()) {
            is = new FileInputStream(location);
        } else {
            throw new IllegalStateException("Google credentials not configured " +
                    "(set GOOGLE_APPLICATION_CREDENTIALS or GOOGLE_CREDENTIALS_JSON)");
        }
        return GoogleCredentials.fromStream(is);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String settingsSpreadsheetId() {
        return settingsSpreadsheetId;
    }

    public AppendValuesResponse appendRow(String sheetName, List<Object> rowValues) {
        ensureEnabled();
        try {
            ValueRange body = new ValueRange().setValues(List.of(rowValues));
            return sheets.spreadsheets().values()
                    .append(spreadsheetId, sheetName + "!A:A", body)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .execute();
        } catch (IOException e) {
            throw new SheetsApiException("Failed to append row to sheet '" + sheetName + "'", e);
        }
    }

    /** 신청 시트(spreadsheet-id) 기준 읽기. 기존 호출 호환. */
    public List<List<Object>> readRange(String sheetName, String range) {
        return readRange(spreadsheetId, sheetName, range);
    }

    /** 임의 스프레드시트 ID로 읽기 (Settings 시트 등). */
    public List<List<Object>> readRange(String targetSpreadsheetId, String sheetName, String range) {
        ensureEnabled();
        try {
            ValueRange resp = sheets.spreadsheets().values()
                    .get(targetSpreadsheetId, sheetName + "!" + range)
                    .execute();
            return resp.getValues() == null ? List.of() : resp.getValues();
        } catch (IOException e) {
            throw new SheetsApiException("Failed to read range '" + range + "' from sheet '" + sheetName + "'", e);
        }
    }

    private void ensureEnabled() {
        if (!enabled) {
            throw new SheetsApiException(
                    "Google Sheets is not configured. Set GOOGLE_SHEET_ID and " +
                            "GOOGLE_APPLICATION_CREDENTIALS (or GOOGLE_CREDENTIALS_JSON) environment variables."
            );
        }
    }
}
