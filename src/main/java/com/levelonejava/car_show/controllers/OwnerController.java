package com.levelonejava.car_show.controllers;

import com.levelonejava.car_show.dtos.OwnerRequest;
import com.levelonejava.car_show.services.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerService ownerService;

    @GetMapping(value = {"/", ""})
    public String ownerIndex(Model model){
        model.addAttribute("listOfOwners", ownerService.getAllOwners());
        return "owner/index";
    }

    @GetMapping(value = {"/create", "/create/"})
    public String ownerForm(Model model){
        model.addAttribute(
                "newOwner", new OwnerRequest(
                        0,
                        "",
                        "",
                        null,
                        null));
        model.addAttribute("genderOptions", com.levelonejava.car_show.enums.Gender.values());
        return "owner/form";
    }

    @PostMapping(value = {"/create", "/create/"})
    public String createOwner(@Valid OwnerRequest ownerRequest, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            model.addAttribute("newOwner", ownerRequest);
            model.addAttribute("genderOptions", com.levelonejava.car_show.enums.Gender.values());
            return "owner/form";
        }
        ownerService.createOwner(ownerRequest);
        return "redirect:/owner/";
    }

    @PostMapping("/delete/{id}")
    public String deleteOwner(@PathVariable long id){
        ownerService.deleteOwnerById(id);
        return "redirect:/owner/";
    }
}
