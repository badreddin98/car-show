package com.levelonejava.car_utils;

import com.levelonejava.car_show.dtos.CarResponse;
import com.levelonejava.car_show.dtos.OwnerRequest;
import com.levelonejava.car_show.dtos.OwnerResponse;
import com.levelonejava.car_show.entities.Owner;
import com.levelonejava.car_show.enums.Gender;

public class OwnerMapper {

    public static Owner fromDto(OwnerRequest request){
        return new Owner(
                request.carId(),
                request.firstName(),
                request.lastName(),
                request.gender(),
                request.dateOfBirth()
                ,null
        );
    }


    public static OwnerResponse toDto(Owner owner){

        return new OwnerResponse(
                owner.getOwnerId(),
                owner.getFirstName(),
                owner.getLastName(),
                owner.getGender(),
                owner.getDateOfBirth()
        );
    }


}
