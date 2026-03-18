package com.levelonejava.car_show.controllers;

import com.levelonejava.car_show.dtos.CarResponse;
import com.levelonejava.car_show.dtos.OwnerRequest;
import com.levelonejava.car_show.dtos.OwnerResponse;
import com.levelonejava.car_show.services.CarService;
import com.levelonejava.car_show.services.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/owner")
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerService ownerService;
    private final CarService carService;

    @GetMapping(value = {"/", ""})
    public ResponseEntity<List<OwnerResponse>> ownerIndex(){
        return ResponseEntity.ok(ownerService.getAllOwners());
    }

    @PostMapping(value = {"/create", "/create/"})
    public ResponseEntity<OwnerResponse> createOwner(@Valid @RequestBody OwnerRequest ownerRequest){
        return ResponseEntity.created(null).body(ownerService.createOwner(ownerRequest));
    }

    @PutMapping(value = {"{id}/update"})
    public ResponseEntity<OwnerResponse> updateOwnerRequest(@Valid @RequestBody OwnerRequest ownerRequest) {
        return ResponseEntity.created(null).body(ownerService.createOwner(ownerRequest));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOwner(@PathVariable long id){
        ownerService.deleteOwnerById(id);
        return ResponseEntity.ok().build();
    }

}
