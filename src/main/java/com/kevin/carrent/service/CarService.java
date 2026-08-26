package com.kevin.carrent.service;

import com.kevin.carrent.dto.CarCreateRequest;
import com.kevin.carrent.dto.CarResponse;
import com.kevin.carrent.dto.CarUpdateRequest;
import com.kevin.carrent.entity.Car;
import com.kevin.carrent.entity.CarModel;
import com.kevin.carrent.entity.Office;
import com.kevin.carrent.exception.CarModelNotFoundException;
import com.kevin.carrent.exception.LicensePlateAlreadyExistsException;
import com.kevin.carrent.exception.OfficeNotFoundException;
import com.kevin.carrent.mapper.CarMapper;
import com.kevin.carrent.repository.CarModelRepository;
import com.kevin.carrent.repository.CarRepository;
import com.kevin.carrent.repository.OfficeRepository;
import org.springframework.stereotype.Service;
import com.kevin.carrent.exception.CarNotFoundException;

import java.util.List;

@Service
public class CarService {
    private final CarRepository carRepository;
    private final CarModelRepository carModelRepository;
    private final OfficeRepository officeRepository;
    private final CarMapper carMapper;

    public CarService(CarRepository carRepository, CarModelRepository carModelRepository, OfficeRepository officeRepository, CarMapper carMapper) {
        this.carRepository = carRepository;
        this.carModelRepository = carModelRepository;
        this.officeRepository = officeRepository;
        this.carMapper = carMapper;
    }

    public CarResponse createCar(CarCreateRequest request) {
        if (carRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new LicensePlateAlreadyExistsException("License plate '" + request.getLicensePlate() + "' is already registered");
        }
        CarModel carModel = carModelRepository.findById(request.getCarModelId()).orElseThrow(() -> new CarModelNotFoundException(
                "CarModel with id " + request.getCarModelId() + " not found"
        ));

        Office office = officeRepository.findById(request.getOfficeId()).orElseThrow(() -> new OfficeNotFoundException(
                "Office with id " + request.getOfficeId() + " not found"
        ));

        Car car = carMapper.toEntity(request, carModel, office);
        Car savedCar = carRepository.save(car);

        return carMapper.toResponse(savedCar);
    }

    public List<CarResponse> getCars() {
        List<Car> cars = carRepository.findAll();
        return cars.stream().map(carMapper::toResponse).toList();
    }

    public CarResponse getCarById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new CarNotFoundException("Car with id " + id + " not found"));
        return carMapper.toResponse(car);
    }

    public void deleteCar(Long id) {
        carRepository.findById(id).orElseThrow(() -> new CarNotFoundException("Car with id " + id + " not found"));
        carRepository.deleteById(id);
    }

    public CarResponse updateCar(Long id, CarUpdateRequest request) {

        Car car = carRepository.findById(id).orElseThrow(() -> new CarNotFoundException("Car with id " + id + " not found"));

        if (carRepository.existsByLicensePlateAndIdNot(request.getLicensePlate(), id)) {

            throw new LicensePlateAlreadyExistsException("License plate '" + request.getLicensePlate() + "' is already registered");
        }
        CarModel carModel = carModelRepository.findById(request.getCarModelId()).orElseThrow(() -> new CarModelNotFoundException(
                "CarModel with id " + request.getCarModelId() + " not found"
        ));

        Office office = officeRepository.findById(request.getOfficeId()).orElseThrow(() -> new OfficeNotFoundException(
                "Office with id " + request.getOfficeId() + " not found"
        ));

        carMapper.updateEntity(request, car, carModel, office);

        Car updatedCar = carRepository.save(car);
        return carMapper.toResponse(updatedCar);
    }
}
