/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.service.masterData;

import com.bsmartone.api.model.masterData.City;
import com.bsmartone.api.repository.masterData.CityRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author jaime
 */
@Service
public class CityService implements CityServiceInterface {

    @Autowired
    private CityRepository cityRepository;

    @Override
    public City create(City city) {
        return cityRepository.save(city);
        //return CityRepository.save(new City(city.getId(),city.getCodigo(),city.getDescripcion()));
    }

    @Override
    public List<City> getAll() {
        return cityRepository.findAll();
    }

    @Override
    public void delete(City city) {
        cityRepository.delete(city);
    }

    @Override
    public void deleteAll() {
        cityRepository.deleteAll();
    }

    @Override
    public City findById(Long id) {
        return cityRepository.findById(id).get();
    }

    public List<City> getAllActive() {
        return cityRepository.findAllByDeletedFalse();
    }

}
