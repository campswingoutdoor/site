package com.campswing.api.dto;

import com.campswing.domain.application.DanceRole;
import com.campswing.domain.application.PassType;
import com.campswing.domain.application.VehicleUsage;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PartyPassApplicationRequest(

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
        PassType passType,

        @Size(max = 50)
        String club,

        @NotNull
        DanceRole role,

        Boolean applyWorkshop,

        @NotNull
        VehicleUsage vehicleUsage,

        @Size(max = 20)
        String vehicleNumber,

        @Size(max = 500)
        String memo,

        @AssertTrue(message = "개인정보 수집·이용에 동의해주세요.")
        Boolean agreedToTerms
) {
        /** 차량 이용(일반/캠핑사이트) 선택 시 차량번호 필수. */
        @AssertTrue(message = "차량(주차) 이용 시 차량번호를 입력해주세요.")
        public boolean isVehicleNumberValid() {
                if (vehicleUsage != null && vehicleUsage != VehicleUsage.NONE) {
                        return vehicleNumber != null && !vehicleNumber.isBlank();
                }
                return true;
        }
}
