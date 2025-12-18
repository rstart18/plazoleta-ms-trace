package co.com.bancolombia.usecase.efficiency;

import co.com.bancolombia.model.efficiency.EmployeeEfficiency;
import co.com.bancolombia.model.efficiency.OrderEfficiency;
import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.ordertrace.OrderTrace;
import co.com.bancolombia.model.ordertrace.gateways.OrderTraceRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderEfficiencyUseCase implements OrderEfficiencyService {

    private final OrderTraceRepository orderTraceRepository;

    @Override
    public List<OrderEfficiency> getAllOrdersEfficiency() {
        List<OrderTrace> allTraces = orderTraceRepository.findAll();

        return allTraces.stream()
                .filter(trace -> trace.getNewStatus() == OrderStatus.DELIVERED)
                .map(this::calculateOrderEfficiency)
                .filter(efficiency -> efficiency.getDurationInMinutes() != null && efficiency.getDurationInMinutes() >= 0)
                .sorted(Comparator.comparingLong(OrderEfficiency::getOrderId))
                .collect(Collectors.toList());
    }

    @Override
    public OrderEfficiency getOrderEfficiency(Long orderId) {
        List<OrderTrace> orderTraces = orderTraceRepository.findByOrderId(orderId);

        LocalDateTime pendingTime = null;
        LocalDateTime deliveredTime = null;

        for (OrderTrace trace : orderTraces) {
            if (trace.getNewStatus() == OrderStatus.PENDING) {
                pendingTime = trace.getTimestamp();
            }
            if (trace.getNewStatus() == OrderStatus.DELIVERED) {
                deliveredTime = trace.getTimestamp();
            }
        }

        long durationInMinutes = -1;
        if (pendingTime != null && deliveredTime != null) {
            durationInMinutes = java.time.temporal.ChronoUnit.MINUTES.between(pendingTime, deliveredTime);
        }

        return OrderEfficiency.builder()
                .orderId(orderId)
                .durationInMinutes(durationInMinutes >= 0 ? durationInMinutes : null)
                .build();
    }

    @Override
    public List<EmployeeEfficiency> getEmployeesEfficiencyRanking() {
        List<OrderTrace> allTraces = orderTraceRepository.findAll();

        Map<Long, List<OrderTrace>> tracesByEmployeeId = allTraces.stream()
                .filter(trace -> trace.getEmployeeId() != null)
                .collect(Collectors.groupingBy(OrderTrace::getEmployeeId));

        return tracesByEmployeeId.entrySet().stream()
                .map(entry -> calculateEmployeeEfficiency(entry.getKey(), entry.getValue()))
                .filter(efficiency -> efficiency.getProcessedOrders() > 0)
                .sorted(Comparator.comparingDouble(EmployeeEfficiency::getAverageDurationInMinutes))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeEfficiency getEmployeeEfficiency(Long employeeId) {
        List<OrderTrace> employeeTraces = orderTraceRepository.findByEmployeeId(employeeId);

        if (employeeTraces.isEmpty()) {
            return EmployeeEfficiency.builder()
                    .employeeId(employeeId)
                    .averageDurationInMinutes(0.0)
                    .processedOrders(0L)
                    .build();
        }

        return calculateEmployeeEfficiency(employeeId, employeeTraces);
    }

    private OrderEfficiency calculateOrderEfficiency(OrderTrace deliveredTrace) {
        List<OrderTrace> orderTraces = orderTraceRepository.findByOrderId(deliveredTrace.getOrderId());

        LocalDateTime pendingTime = null;
        LocalDateTime deliveredTime = deliveredTrace.getTimestamp();

        for (OrderTrace trace : orderTraces) {
            if (trace.getNewStatus() == OrderStatus.PENDING) {
                pendingTime = trace.getTimestamp();
                break;
            }
        }

        long durationInMinutes = -1;
        if (pendingTime != null) {
            durationInMinutes = java.time.temporal.ChronoUnit.MINUTES.between(pendingTime, deliveredTime);
        }

        return OrderEfficiency.builder()
                .orderId(deliveredTrace.getOrderId())
                .durationInMinutes(durationInMinutes >= 0 ? durationInMinutes : null)
                .build();
    }

    private EmployeeEfficiency calculateEmployeeEfficiency(Long employeeId, List<OrderTrace> employeeTraces) {
        String employeeEmail = employeeTraces.stream()
                .map(OrderTrace::getEmployeeEmail)
                .filter(email -> email != null)
                .findFirst()
                .orElse("");

        List<Long> durations = employeeTraces.stream()
                .filter(trace -> trace.getNewStatus() == OrderStatus.DELIVERED)
                .map(trace -> {
                    List<OrderTrace> allOrderTraces = orderTraceRepository.findByOrderId(trace.getOrderId());
                    LocalDateTime pendingTime = null;
                    LocalDateTime deliveredTime = trace.getTimestamp();

                    for (OrderTrace t : allOrderTraces) {
                        if (t.getNewStatus() == OrderStatus.PENDING) {
                            pendingTime = t.getTimestamp();
                            break;
                        }
                    }

                    if (pendingTime != null) {
                        return java.time.temporal.ChronoUnit.MINUTES.between(pendingTime, deliveredTime);
                    }
                    return -1L;
                })
                .filter(duration -> duration >= 0)
                .collect(Collectors.toList());

        Double averageDuration = durations.isEmpty()
                ? 0.0
                : durations.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);

        return EmployeeEfficiency.builder()
                .employeeId(employeeId)
                .employeeEmail(employeeEmail)
                .averageDurationInMinutes(averageDuration)
                .processedOrders((long) durations.size())
                .build();
    }
}