package co.com.bancolombia.usecase.efficiency;

import co.com.bancolombia.model.efficiency.EmployeeEfficiency;
import co.com.bancolombia.model.efficiency.OrderEfficiency;

import java.util.List;

public interface OrderEfficiencyService {
    List<OrderEfficiency> getAllOrdersEfficiency();
    OrderEfficiency getOrderEfficiency(Long orderId);
    List<EmployeeEfficiency> getEmployeesEfficiencyRanking();
    EmployeeEfficiency getEmployeeEfficiency(Long employeeId);
}