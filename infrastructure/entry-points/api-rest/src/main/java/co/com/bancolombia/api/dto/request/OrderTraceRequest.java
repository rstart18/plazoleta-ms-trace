package co.com.bancolombia.api.dto.request;

import co.com.bancolombia.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderTraceRequest {
    private Long orderId;
    private Long clientId;
    private String clientEmail;
    private OrderStatus previousStatus;
    private OrderStatus newStatus;
    private Long employeeId;
    private String employeeEmail;
}