package co.com.bancolombia.api.mapper.dto;

import co.com.bancolombia.api.dto.response.EmployeeEfficiencyResponse;
import co.com.bancolombia.api.dto.response.OrderEfficiencyResponse;
import co.com.bancolombia.model.efficiency.EmployeeEfficiency;
import co.com.bancolombia.model.efficiency.OrderEfficiency;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EfficiencyMapper {
    OrderEfficiencyResponse toOrderEfficiencyResponse(OrderEfficiency efficiency);
    EmployeeEfficiencyResponse toEmployeeEfficiencyResponse(EmployeeEfficiency efficiency);
}