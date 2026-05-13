package com.campswing.web;

import com.campswing.api.dto.ApplicationCreatedResponse;
import com.campswing.api.dto.CampsiteApplicationRequest;
import com.campswing.api.dto.DormitoryApplicationRequest;
import com.campswing.domain.application.ArrivalTime;
import com.campswing.domain.application.Gender;
import com.campswing.domain.application.Nights;
import com.campswing.domain.application.TentSize;
import com.campswing.service.ApplicationService;
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

    public LodgingController(ApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("pageTitle", "숙박 안내");
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

    private void addCampsiteOptions(Model model) {
        model.addAttribute("tentSizes", TentSize.values());
        model.addAttribute("arrivalTimes", ArrivalTime.values());
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

    private void addDormitoryOptions(Model model) {
        model.addAttribute("genders", Gender.values());
        model.addAttribute("nightsOptions", Nights.values());
        model.addAttribute("pageTitle", "도미토리 신청");
    }

    // ===== Empty form factories =====

    private static CampsiteApplicationRequest emptyCampsiteForm() {
        return new CampsiteApplicationRequest(null, null, null, null, null, null, null, null, null, null);
    }

    private static DormitoryApplicationRequest emptyDormitoryForm() {
        return new DormitoryApplicationRequest(null, null, null, null, null, null, null, null, null);
    }
}
