package com.kevin.carrent.repository;

import com.kevin.carrent.entity.Office;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class officeRepositoryTest {
    @Autowired
    private OfficeRepository officeRepository;

    @Test
    void shouldSaveAndFindOfficeById() {


        Office office = new Office();
        office.setName("Oficina Las Palmas");
        office.setAddress("Calle León y Castillo 10");
        office.setCity("Las Palmas");
        office.setPhone("928123456");


        Office savedOffice = officeRepository.save(office);

        Optional<Office> result =
                officeRepository.findById(savedOffice.getId());


        assertTrue(result.isPresent());
        assertEquals("Oficina Las Palmas", result.get().getName());
        assertEquals("Calle León y Castillo 10", result.get().getAddress());
        assertEquals("Las Palmas", result.get().getCity());
        assertEquals("928123456", result.get().getPhone());
    }

    @Test
    void shouldReturnEmptyWhenOfficeDoesNotExist() {

        Optional<Office> result = officeRepository.findById(999L);

        assertTrue(result.isEmpty());
    }
}
