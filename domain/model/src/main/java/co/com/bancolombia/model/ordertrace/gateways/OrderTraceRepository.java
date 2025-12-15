package co.com.bancolombia.model.ordertrace.gateways;

import co.com.bancolombia.model.ordertrace.OrderTrace;

public interface OrderTraceRepository {
    OrderTrace save(OrderTrace orderTrace);
}
