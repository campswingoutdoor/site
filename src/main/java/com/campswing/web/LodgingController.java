package com.campswing.web;

import com.campswing.api.dto.ApplicationCreatedResponse;
import com.campswing.api.dto.CampsiteApplicationRequest;
import com.campswing.api.dto.DormitoryApplicationRequest;
import com.campswing.domain.application.ArrivalTime;
import com.campswing.domain.application.Gender;
import com.campswing.domain.application.Nights;
import com.campswing.service.ApplicationService;
import com.campswing.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/lodging")
public class LodgingController {

    private final ApplicationService service;
    private final SettingsService settings;
    private final GlobalModelAttributes.PageMetaHelper pageMeta;

    public LodgingController(ApplicationService service, SettingsService settings,
                            GlobalModelAttributes.PageMetaHelper pageMeta) {
        this.service = service;
        this.settings = settings;
        this.pageMeta = pageMeta;
    }

    @GetMapping
    public String index(Model model) {
        pageMeta.apply(model, "lodging");
        return "lodging/index";
    }

    // ===== Campsite =====

    @GetMapping("/campsite")
    public String campsiteForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", emptyCampsiteForm());
        }
        addCampsiteOptions(model);
        return "lodging/campsite";
    }

    @PostMapping("/campsite")
    public String submitCampsite(@Valid @ModelAttribute("form") CampsiteApplicationRequest form,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes ra) {
        if (result.hasErrors()) {
            addCampsiteOptions(model);
            return "lodging/campsite";
        }
        ApplicationCreatedResponse response = service.submitCampsite(form);
        ra.addAttribute("type", "CAMPSITE");
        ra.addAttribute("id", response.applicationId());
        return "redirect:/apply/success";
    }

    @GetMapping("/campsite/list")
    public String campsiteList(Model model) {
        pageMeta.apply(model, "lodging.campsite.list");
        model.addAttribute("items", service.listCampsite());
        return "lodging/campsite-list";
    }

    private void addCampsiteOptions(Model model) {
        pageMeta.apply(model, "lodging");
        model.addAttribute("arrivalTimes", ArrivalTime.values());
        model.addAttribute("notices", settings.campsiteNotice());
        model.addAttribute("siteFee", ApplicationService.CAMPSITE_SITE_FEE);
        model.addAttribute("earlyCheckinFee", ApplicationService.CAMPSITE_EARLY_CHECKIN_FEE);
        model.addAttribute("pageTitle", "캠핑사이트 신청");
    }

    // ===== Dormitory =====

    @GetMapping("/dormitory")
    public String dormitoryForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", emptyDormitoryForm());
        }
        addDormitoryOptions(model);
        return "lodging/dormitory";
    }

    @PostMapping("/dormitory")
    public String submitDormitory(@Valid @ModelAttribute("form") DormitoryApplicationRequest form,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes ra) {
        if (result.hasErrors()) {
            addDormitoryOptions(model);
            return "lodging/dormitory";
        }
        ApplicationCreatedResponse response = service.submitDormitory(form);
        ra.addAttribute("type", "DORMITORY");
        ra.addAttribute("id", response.applicationId());
        return "redirect:/apply/success";
    }

    @GetMapping("/dormitory/list")
    public String dormitoryList(Model model) {
        pageMeta.apply(model, "lodging.dormitory.list");
        model.addAttribute("items", service.listDormitory());
        return "lodging/dormitory-list";
    }

    private void addDormitoryOptions(Model model) {
        pageMeta.apply(model, "lodging");
        model.addAttribute("genders", Gender.values());
        model.addAttribute("nightsOptions", Nights.values());
        model.addAttribute("notices", settings.dormitoryNotice());
        model.addAttribute("oneNightFee", ApplicationService.DORMITORY_ONE_NIGHT_FEE);
        model.addAttribute("twoNightsFee", ApplicationService.DORMITORY_TWO_NIGHTS_FEE);
        model.addAttribute("pageTitle", "도미토리 신청");
    }

    // ===== Empty form factories =====

    private static CampsiteApplicationRequest emptyCampsiteForm() {
        // realName, nickname, phone, email, partySize, arrivalTime, usePickupBus, memo, agreedToTerms
        return new CampsiteApplicationRequest(null, null, null, null, null, null, null, null, null);
    }

    private static DormitoryApplicationRequest emptyDormitoryForm() {
        // realName, nickname, phone, email, gender, nights, memo, agreedToTerms
        return new DormitoryApplicationRequest(null, null, null, null, null, null, null, null);
    }
}
