package co.com.bancolombia.api.rest;

import co.com.bancolombia.api.dto.response.ApiResponseData;
import co.com.bancolombia.api.dto.response.EmployeeEfficiencyResponse;
import co.com.bancolombia.api.dto.response.OrderEfficiencyResponse;
import co.com.bancolombia.api.mapper.dto.EfficiencyMapper;
import co.com.bancolombia.model.efficiency.EmployeeEfficiency;
import co.com.bancolombia.model.efficiency.OrderEfficiency;
import co.com.bancolombia.usecase.efficiency.OrderEfficiencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/efficiency")
@RequiredArgsConstructor
public class EfficiencyApiRest {

    private final OrderEfficiencyService orderEfficiencyService;
    private final EfficiencyMapper efficiencyMapper;

    @GetMapping
    public ResponseEntity<ApiResponseData<List<OrderEfficiencyResponse>>> getAllOrdersEfficiency() {
        List<OrderEfficiency> efficiencies = orderEfficiencyService.getAllOrdersEfficiency();
        List<OrderEfficiencyResponse> response = efficiencies.stream()
                .map(efficiencyMapper::toOrderEfficiencyResponse)
                .toList();

        return ResponseEntity.ok(ApiResponseData.of(response));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponseData<OrderEfficiencyResponse>> getOrderEfficiency(
            @PathVariable("orderId") Long orderId) {

        OrderEfficiency efficiency = orderEfficiencyService.getOrderEfficiency(orderId);
        OrderEfficiencyResponse response = efficiencyMapper.toOrderEfficiencyResponse(efficiency);

        return ResponseEntity.ok(ApiResponseData.of(response));
    }

    @GetMapping("/employees/ranking")
    public ResponseEntity<ApiResponseData<List<EmployeeEfficiencyResponse>>> getEmployeesEfficiencyRanking() {
        List<EmployeeEfficiency> efficiencies = orderEfficiencyService.getEmployeesEfficiencyRanking();
        List<EmployeeEfficiencyResponse> response = efficiencies.stream()
                .map(efficiencyMapper::toEmployeeEfficiencyResponse)
                .toList();

        return ResponseEntity.ok(ApiResponseData.of(response));
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<ApiResponseData<EmployeeEfficiencyResponse>> getEmployeeEfficiency(
            @PathVariable("employeeId") Long employeeId) {

        EmployeeEfficiency efficiency = orderEfficiencyService.getEmployeeEfficiency(employeeId);
        EmployeeEfficiencyResponse response = efficiencyMapper.toEmployeeEfficiencyResponse(efficiency);

        return ResponseEntity.ok(ApiResponseData.of(response));
    }
}