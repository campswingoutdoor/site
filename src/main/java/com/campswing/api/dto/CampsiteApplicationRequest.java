package com.campswing.api.dto;

import com.campswing.domain.application.ArrivalTime;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CampsiteApplicationRequest(

        @NotBlank
        @Size(min = 2, max = 30)
        String realName,

        @NotBlank
        @Size(min = 1, max = 30)
        String nickname,

        @NotBlank
        @Pattern(regexp = "^010-?\\d{4}-?\\d{4}$", message = "연락처 형식이 올바르지 않습니다.")
        String phone,

        @Email
        String email,

        @NotNull
        @Min(1)
        @Max(4)
        Integer partySize,

        @NotNull
        ArrivalTime arrivalTime,

        @NotNull
        Boolean usePickupBus,

        @Size(max = 500)
        String memo,

        @AssertTrue(message = "개인정보 수집·이용에 동의해주세요.")
        Boolean agreedToTerms
) {
}
