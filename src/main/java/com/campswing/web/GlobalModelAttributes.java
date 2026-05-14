package com.campswing.web;

import com.campswing.domain.settings.EventInfo;
import com.campswing.domain.settings.PageMeta;
import com.campswing.service.SettingsService;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 모든 Web 컨트롤러 응답에 공통 모델 속성을 주입.
 * 템플릿 어디서나 ${event.*}, ${conceptCopy.*}, ${locationGuide.*} 등으로 접근 가능 (footer fragment 포함).
 */
@ControllerAdvice(basePackages = "com.campswing.web")
public class GlobalModelAttributes {

    private final SettingsService settings;
    private final PageMetaHelper pageMetaHelper;

    public GlobalModelAttributes(SettingsService settings) {
        this.settings = settings;
        this.pageMetaHelper = new PageMetaHelper(settings);
    }

    @ModelAttribute("event")
    public EventInfo event() {
        return settings.event();
    }

    @ModelAttribute("conceptCopy")
    public Object conceptCopy() {
        return settings.conceptCopy();
    }

    @ModelAttribute("locationGuide")
    public Object locationGuide() {
        return settings.locationGuide();
    }

    /**
     * 컨트롤러에서 `pageMetaHelper.apply(model, "event.overview")` 형태로 호출 가능하도록 빈 제공.
     */
    @ModelAttribute("__pageMetaHelper")
    public PageMetaHelper pageMetaHelper() {
        return pageMetaHelper;
    }

    /** 컨트롤러에서 호출하는 헬퍼 — pageMeta/pageTitle/pageDescription을 모델에 설정. */
    @Component
    public static class PageMetaHelper {
        private final SettingsService settings;

        public PageMetaHelper(SettingsService settings) {
            this.settings = settings;
        }

        public void apply(Model model, String key) {
            PageMeta meta = settings.pageMeta(key);
            model.addAttribute("pageMeta", meta);
            if (meta == null) return;
            if (meta.title() != null && !meta.title().isBlank()) {
                model.addAttribute("pageTitle", meta.title());
            }
            if (meta.description() != null && !meta.description().isBlank()) {
                model.addAttribute("pageDescription", meta.description());
            }
        }
    }
}
