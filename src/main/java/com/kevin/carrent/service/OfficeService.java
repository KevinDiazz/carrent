package com.kevin.carrent.service;

import com.kevin.carrent.dto.OfficeCreateRequest;
import com.kevin.carrent.dto.OfficeResponse;
import com.kevin.carrent.entity.Office;
import com.kevin.carrent.mapper.OfficeMapper;
import com.kevin.carrent.repository.OfficeRepository;
import org.springframework.stereotype.Service;

@Service
public class OfficeService {

    private final OfficeRepository officeRepository;
    private final OfficeMapper officeMapper;

    public OfficeService(
            OfficeRepository officeRepository,
            OfficeMapper officeMapper) {

        this.officeRepository = officeRepository;
        this.officeMapper = officeMapper;
    }

    public OfficeResponse createOffice(OfficeCreateRequest request) {

        Office office = officeMapper.toEntity(request);

        Office savedOffice = officeRepository.save(office);

        return officeMapper.toResponse(savedOffice);
    }
}