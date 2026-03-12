package com.levelonejava.car_show.controllers;

import com.levelonejava.car_show.dtos.CarRequest;
import com.levelonejava.car_show.services.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/car")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @GetMapping(value = {"/", ""})
    public String carIndex(Model model){
        model.addAttribute("listOfCars", carService.getAllCars());
        return "car/index";
    }

    @GetMapping(value = {"/create", "/create/"})
    public String carForm(Model model){
        model.addAttribute(
                "newCar", new CarRequest(
                        "",
                        "",
                        "",
                        "",
                        (byte) 0));
        return "car/form";
    }

    @PostMapping(value = {"/create", "/create/"})
    public String createCar(@Valid CarRequest carRequest, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            model.addAttribute("newCar", carRequest);
            return "car/form";
        }
        carService.createCar(carRequest);
        return "redirect:/car/";
    }

    @PostMapping("/delete/{id}")
    public String deleteCar(@PathVariable long id){
        carService.deleteCarById(id);
        return "redirect:/car/";
    }
}
