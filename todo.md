# TODO

## 신청 폼 검증 완화

### DTO 검증 어노테이션 제거 (3개 파일)

- `api/dto/PartyPassApplicationRequest.java`
  - `email`: `@NotBlank` 제거 (`@Email`은 유지 — 입력 시 포맷만 검증, 빈 값 허용)
  - `agreedToTerms`: `@AssertTrue` 제거 (필드 자체는 유지)
- `api/dto/CampsiteApplicationRequest.java` — 동일
- `api/dto/DormitoryApplicationRequest.java` — 동일

### 템플릿 변경 (3개 파일)

- 각 폼의 `이메일 *` → `이메일` (asterisk 제거)
- 개인정보 동의 체크박스는 유지하되 라벨에서 필수 뉘앙스 톤 다운(예: "수집·이용에 동의합니다 (선택)")
- 에러 메시지 표시 `<p class="field-error">` 줄은 그대로 둬도 됨 — `@AssertTrue` 없으면 트리거 안 됨

### 테스트 영향

- `test/.../PartyPassControllerTest.java`
  - `agreedToTerms=false` rerender 테스트 → 통과해야 하므로 제거 또는 다른 invalid 케이스로 교체
- `test/.../ApplicationApiControllerTest.java`
  - `partyPass_agreedToTermsFalse_returns400` → 제거 또는 다른 케이스로 교체
  - `email` 누락/형식 오류 케이스 재검토(@Email만 남기면 빈 값은 통과)

### 작업 시 주의

- **시트 컬럼은 변경 X**: `email`/`agreedToTerms` 컬럼은 그대로 유지 — 입력 안 한 사용자는 빈 값 또는 `false`로 append
- **PII 정책 재확인**: 이메일을 옵션화하면 안내·환불 채널이 줄어듦. 운영팀과 합의 후 진행
- **개인정보 수집 표시 자체는 유지**: "동의해야 가능"이 아니라 "동의해주시면 입금자 안내를 받습니다" 류 안내문으로 톤 변경 검토
