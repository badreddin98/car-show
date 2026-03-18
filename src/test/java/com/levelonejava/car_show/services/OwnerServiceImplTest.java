package com.levelonejava.car_show.services;

import com.levelonejava.car_show.dtos.OwnerRequest;
import com.levelonejava.car_show.dtos.OwnerResponse;
import com.levelonejava.car_show.entities.Owner;
import com.levelonejava.car_show.enums.Gender;
import com.levelonejava.car_show.exception.InvalidOwnerIdException;
import com.levelonejava.car_show.repository.CarRepository;
import com.levelonejava.car_show.repository.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceImplTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private OwnerServiceImpl ownerService;

    @Test
    void createOwner_returnsSaved() {
        OwnerRequest req = new OwnerRequest(0L, "John", "Doe", Gender.MALE, LocalDate.of(1990,1,1));
        Owner owner = new Owner(1L, "John", "Doe", Gender.MALE, LocalDate.of(1990,1,1), null);
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);

        OwnerResponse resp = ownerService.createOwner(req);

        assertNotNull(resp);
        // OwnerResponse has field named 'onwerId' (typo in DTO), use its accessor
        assertEquals(1L, resp.onwerId());
        verify(ownerRepository, times(1)).save(any(Owner.class));
    }

    @Test
    void getOwnerById_notFound_throws() {
        when(ownerRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(InvalidOwnerIdException.class, () -> ownerService.getOwnerById(5L));
    }

    @Test
    void getAllOwners_returnsList() {
        Owner o1 = new Owner(1L, "A", "B", Gender.FEMALE, LocalDate.of(1980,1,1), null);
        when(ownerRepository.findAll()).thenReturn(List.of(o1));

        var list = ownerService.getAllOwners();

        assertEquals(1, list.size());
        verify(ownerRepository, times(1)).findAll();
    }

    @Test
    void deleteOwnerById_notExist_throws() {
        when(ownerRepository.existsById(99L)).thenReturn(false);

        assertThrows(InvalidOwnerIdException.class, () -> ownerService.deleteOwnerById(99L));
    }
}


