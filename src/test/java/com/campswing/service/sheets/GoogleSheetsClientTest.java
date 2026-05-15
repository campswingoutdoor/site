package com.campswing.service.sheets;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GoogleSheetsClientTest {

    @Test
    void sanitizeForLog_replaces_long_base64_blob_with_placeholder() {
        // 실제 사고 재현: FileNotFoundException 메시지가 "<base64>: (Filename too long)" 형태
        String leakedBase64 = "ewogICJ0eXBlIjogInNlcnZpY2VfYWNjb3VudCIsCiAgInByb2plY3RfaWQiOiAiY2FtcCJ9";
        String raw = leakedBase64 + " (Filename too long)";

        String safe = GoogleSheetsClient.sanitizeForLog(raw);

        assertThat(safe).contains("<redacted>");
        assertThat(safe).contains("(Filename too long)");
        assertThat(safe).doesNotContain(leakedBase64);
    }

    @Test
    void sanitizeForLog_preserves_short_normal_messages() {
        String safe = GoogleSheetsClient.sanitizeForLog("spreadsheet not found");
        assertThat(safe).isEqualTo("spreadsheet not found");
    }

    @Test
    void sanitizeForLog_handles_null_and_blank() {
        assertThat(GoogleSheetsClient.sanitizeForLog(null)).isEqualTo("(no message)");
        assertThat(GoogleSheetsClient.sanitizeForLog("")).isEqualTo("(no message)");
        assertThat(GoogleSheetsClient.sanitizeForLog("   ")).isEqualTo("(no message)");
    }

    @Test
    void sanitizeForLog_truncates_overlong_message() {
        // 영숫자 단일 연속은 redact로 짧아져서 truncation을 안 탐 → 공백·구두점 섞은 긴 문장으로 검증
        String longMsg = "error: ".repeat(60); // ~420자
        String safe = GoogleSheetsClient.sanitizeForLog(longMsg);
        assertThat(safe).hasSize(203); // 200 + "..."
        assertThat(safe).endsWith("...");
    }

    @Test
    void sanitizeForLog_keeps_short_alphanumeric_runs() {
        // 39자(임계값 미만)는 redact 대상이 아님
        String safe = GoogleSheetsClient.sanitizeForLog("error code ABC123abcDEF456ghiJKL789mnoPQR012stu");
        assertThat(safe).doesNotContain("<redacted>");
    }
}
