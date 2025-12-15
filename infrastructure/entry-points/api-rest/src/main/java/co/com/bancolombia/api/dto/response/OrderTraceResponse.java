package co.com.bancolombia.api.dto.response;

import co.com.bancolombia.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderTraceResponse {
    private Long id;
    private Long orderId;
    private Long clientId;
    private String clientEmail;
    private OrderStatus previousStatus;
    private OrderStatus newStatus;
    private Long employeeId;
    private String employeeEmail;
    private LocalDateTime timestamp;
}