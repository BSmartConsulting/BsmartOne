/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.bsmartone.api.service.masterData;

import com.bsmartone.api.model.masterData.City;
import java.util.List;

/**
 *
 * @author jaime
 */
public interface CityServiceInterface {

    City create(City city);

    void delete(City city);

    void deleteAll();

    City findById(Long id);

    List<City> getAll();

}
