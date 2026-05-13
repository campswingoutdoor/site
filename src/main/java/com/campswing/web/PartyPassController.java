package com.campswing.web;

import com.campswing.api.dto.ApplicationCreatedResponse;
import com.campswing.api.dto.PartyPassApplicationRequest;
import com.campswing.domain.application.PassType;
import com.campswing.domain.application.TshirtSize;
import com.campswing.service.ApplicationService;
import com.campswing.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PartyPassController {

    private final ApplicationService service;
    private final SettingsService settings;

    public PartyPassController(ApplicationService service, SettingsService settings) {
        this.service = service;
        this.settings = settings;
    }

    @GetMapping("/party-pass")
    public String form(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", emptyForm());
        }
        addFormOptions(model);
        model.addAttribute("pageTitle", "파티패스 안내·신청");
        return "party-pass/index";
    }

    @PostMapping("/party-pass")
    public String submit(@Valid @ModelAttribute("form") PartyPassApplicationRequest form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            addFormOptions(model);
            model.addAttribute("pageTitle", "파티패스 안내·신청");
            return "party-pass/index";
        }
        ApplicationCreatedResponse response = service.submitPartyPass(form);
        ra.addAttribute("type", "PARTY_PASS");
        ra.addAttribute("id", response.applicationId());
        return "redirect:/apply/success";
    }

    private void addFormOptions(Model model) {
        model.addAttribute("passTypes", PassType.values());
        model.addAttribute("tshirtSizes", TshirtSize.values());
        model.addAttribute("benefits", settings.partyPassBenefits());
    }

    private static PartyPassApplicationRequest emptyForm() {
        return new PartyPassApplicationRequest(null, null, null, null, null, null, null, null, null);
    }
}
