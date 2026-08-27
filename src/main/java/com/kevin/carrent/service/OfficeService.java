package com.kevin.carrent.service;

import com.kevin.carrent.dto.OfficeCreateRequest;
import com.kevin.carrent.dto.OfficeResponse;
import com.kevin.carrent.entity.Office;
import com.kevin.carrent.exception.OfficeInUseException;
import com.kevin.carrent.exception.OfficeNotFoundException;
import com.kevin.carrent.mapper.OfficeMapper;
import com.kevin.carrent.repository.CarRepository;
import com.kevin.carrent.repository.OfficeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfficeService {

    private final OfficeRepository officeRepository;
    private final OfficeMapper officeMapper;
    private final CarRepository carRepository;

    public OfficeService(
            OfficeRepository officeRepository,
            OfficeMapper officeMapper, CarRepository carRepository) {

        this.officeRepository = officeRepository;
        this.officeMapper = officeMapper;
        this.carRepository = carRepository;
    }

    public OfficeResponse createOffice(OfficeCreateRequest request) {

        Office office = officeMapper.toEntity(request);

        Office savedOffice = officeRepository.save(office);

        return officeMapper.toResponse(savedOffice);
    }

    public List<OfficeResponse> getAllOffices() {
        List<Office> offices = officeRepository.findAll();
        return offices.stream().map(officeMapper::toResponse).toList();
    }

    public void deleteOffice(Long id) {

        officeRepository.findById(id)
                .orElseThrow(() ->
                        new OfficeNotFoundException(
                                "Office with id " + id + " not found"
                        ));

        if (carRepository.existsByOfficeId(id)) {
            throw new OfficeInUseException(
                    "Office with id " + id + " is currently assigned to cars"
            );
        }

        officeRepository.deleteById(id);
    }
}