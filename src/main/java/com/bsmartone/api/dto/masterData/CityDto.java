/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.dto.masterData;

import com.bsmartone.api.model.*;
import com.bsmartone.api.model.masterData.City;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CityDto extends BaseEntity {

    private Long id;
    private String code;
    private String city;
    private String department;

    public static City CityDtoToCity(CityDto recordDto) {
        City city = City.builder()
                .id(recordDto.getId())
                .code(recordDto.getCode())
                .city(recordDto.getCity())
                .department(recordDto.getDepartment())
                .creationDate(recordDto.getCreationDate())
                .lastUpdatedDate(recordDto.getLastUpdatedDate())
                .createdBy(recordDto.getCreatedBy())
                .lastUpdatedBy(recordDto.getLastUpdatedBy())
                .deleted(recordDto.getDeleted())
                .build();
        return city;
    }

    public static CityDto CityToCityDto(City record) {
        CityDto citydto = CityDto.builder()
                .id(record.getId())
                .code(record.getCode())
                .city(record.getCity())
                .department(record.getDepartment())
                .creationDate(record.getCreationDate())
                .lastUpdatedDate(record.getLastUpdatedDate())
                .createdBy(record.getCreatedBy())
                .lastUpdatedBy(record.getLastUpdatedBy())
                .deleted(record.getDeleted())
                .build();
        return citydto;
    }

    public CityDto() {
    }

    @Builder
    public CityDto(Long id, String code, String city, String department, LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        super(creationDate, lastUpdatedDate, createdBy, lastUpdatedBy, deleted);
        this.id = id;
        this.code = code;
        this.city = city;
        this.department = department;
    }

}
