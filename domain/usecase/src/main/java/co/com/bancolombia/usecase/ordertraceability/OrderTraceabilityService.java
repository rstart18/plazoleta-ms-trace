package co.com.bancolombia.usecase.ordertraceability;

import co.com.bancolombia.model.ordertrace.OrderTrace;

import java.util.List;

public interface OrderTraceabilityService {
    OrderTrace createTrace(OrderTrace orderTrace);
    List<OrderTrace> getOrderHistory(Long orderId);
}
