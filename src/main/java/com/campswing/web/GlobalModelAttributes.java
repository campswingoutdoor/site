package com.campswing.web;

import com.campswing.domain.settings.EventInfo;
import com.campswing.service.SettingsService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 모든 Web 컨트롤러 응답에 공통 모델 속성을 주입.
 * 템플릿 어디서나 ${event.*} 로 접근 가능 (footer fragment 포함).
 */
@ControllerAdvice(basePackages = "com.campswing.web")
public class GlobalModelAttributes {

    private final SettingsService settings;

    public GlobalModelAttributes(SettingsService settings) {
        this.settings = settings;
    }

    @ModelAttribute("event")
    public EventInfo event() {
        return settings.event();
    }
}
