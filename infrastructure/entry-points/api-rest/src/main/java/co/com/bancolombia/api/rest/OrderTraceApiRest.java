package co.com.bancolombia.api.rest;

import co.com.bancolombia.api.dto.request.OrderTraceRequest;
import co.com.bancolombia.api.dto.response.ApiResponseData;
import co.com.bancolombia.api.dto.response.OrderTraceResponse;
import co.com.bancolombia.api.mapper.dto.OrderTraceMapper;
import co.com.bancolombia.model.ordertrace.OrderTrace;
import co.com.bancolombia.usecase.ordertraceability.OrderTraceabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/traces")
@RequiredArgsConstructor
public class OrderTraceApiRest {

    private final OrderTraceabilityService orderTraceabilityService;
    private final OrderTraceMapper orderTraceMapper;

    @PostMapping
    public ResponseEntity<ApiResponseData<OrderTraceResponse>> createTrace(
            @RequestBody OrderTraceRequest request) {

        OrderTrace orderTrace = orderTraceMapper.toModel(request);
        OrderTrace createdTrace = orderTraceabilityService.createTrace(orderTrace);
        OrderTraceResponse response = orderTraceMapper.toResponseDto(createdTrace);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseData.of(response));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponseData<List<OrderTraceResponse>>> getOrderHistory(
            @PathVariable("orderId") Long orderId) {

        List<OrderTrace> orderHistory = orderTraceabilityService.getOrderHistory(orderId);
        List<OrderTraceResponse> response = orderHistory.stream()
                .map(orderTraceMapper::toResponseDto)
                .toList();

        return ResponseEntity.ok(ApiResponseData.of(response));
    }
}
