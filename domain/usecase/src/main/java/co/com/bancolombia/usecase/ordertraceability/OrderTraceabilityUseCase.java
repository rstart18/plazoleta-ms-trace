package co.com.bancolombia.usecase.ordertraceability;

import co.com.bancolombia.model.ordertrace.OrderTrace;
import co.com.bancolombia.model.ordertrace.gateways.OrderTraceRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class OrderTraceabilityUseCase implements OrderTraceabilityService {

    private final OrderTraceRepository orderTraceRepository;

    @Override
    public OrderTrace createTrace(OrderTrace orderTrace) {
        OrderTrace traceToSave = orderTrace.toBuilder()
                .timestamp(LocalDateTime.now())
                .build();

        return orderTraceRepository.save(traceToSave);
    }
}