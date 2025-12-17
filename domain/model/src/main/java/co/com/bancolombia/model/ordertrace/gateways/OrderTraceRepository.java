package co.com.bancolombia.model.ordertrace.gateways;

import co.com.bancolombia.model.ordertrace.OrderTrace;

import java.util.List;

public interface OrderTraceRepository {
    OrderTrace save(OrderTrace orderTrace);
    List<OrderTrace> findByOrderId(Long orderId);
}
