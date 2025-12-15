package co.com.bancolombia.usecase.ordertraceability;

import co.com.bancolombia.model.ordertrace.OrderTrace;

public interface OrderTraceabilityService {
    OrderTrace createTrace(OrderTrace orderTrace);
}
