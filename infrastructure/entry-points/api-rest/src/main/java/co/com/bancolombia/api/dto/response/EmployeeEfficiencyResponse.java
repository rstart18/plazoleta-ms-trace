package co.com.bancolombia.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEfficiencyResponse {
    private Long employeeId;
    private String employeeEmail;
    private Double averageDurationInMinutes;
    private Long processedOrders;
}