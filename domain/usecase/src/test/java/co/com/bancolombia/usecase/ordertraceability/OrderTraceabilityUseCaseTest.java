package co.com.bancolombia.usecase.ordertraceability;

import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.ordertrace.OrderTrace;
import co.com.bancolombia.model.ordertrace.gateways.OrderTraceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTraceabilityUseCaseTest {

    @Mock
    private OrderTraceRepository orderTraceRepository;

    @InjectMocks
    private OrderTraceabilityUseCase orderTraceabilityUseCase;

    private OrderTrace orderTrace1;
    private OrderTrace orderTrace2;
    private Long orderId;

    @BeforeEach
    void setUp() {
        orderId = 1L;
        LocalDateTime now = LocalDateTime.now();

        orderTrace1 = OrderTrace.builder()
                .id(1L)
                .orderId(orderId)
                .clientId(100L)
                .clientEmail("client@test.com")
                .previousStatus(OrderStatus.PENDING)
                .newStatus(OrderStatus.IN_PREPARATION)
                .employeeId(200L)
                .employeeEmail("employee@test.com")
                .timestamp(now.minusHours(2))
                .build();

        orderTrace2 = OrderTrace.builder()
                .id(2L)
                .orderId(orderId)
                .clientId(100L)
                .clientEmail("client@test.com")
                .previousStatus(OrderStatus.IN_PREPARATION)
                .newStatus(OrderStatus.READY)
                .employeeId(200L)
                .employeeEmail("employee@test.com")
                .timestamp(now.minusHours(1))
                .build();
    }

    @Test
    void getOrderHistory_WhenOrderHasHistory_ShouldReturnListOfTraces() {
        // Arrange
        List<OrderTrace> expectedTraces = Arrays.asList(orderTrace1, orderTrace2);
        when(orderTraceRepository.findByOrderId(orderId)).thenReturn(expectedTraces);

        // Act
        List<OrderTrace> result = orderTraceabilityUseCase.getOrderHistory(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedTraces, result);
        verify(orderTraceRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void getOrderHistory_WhenOrderHasNoHistory_ShouldReturnEmptyList() {
        // Arrange
        when(orderTraceRepository.findByOrderId(orderId)).thenReturn(Collections.emptyList());

        // Act
        List<OrderTrace> result = orderTraceabilityUseCase.getOrderHistory(orderId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderTraceRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void getOrderHistory_ShouldCallRepositoryWithCorrectOrderId() {
        // Arrange
        Long specificOrderId = 999L;
        when(orderTraceRepository.findByOrderId(specificOrderId)).thenReturn(Collections.emptyList());

        // Act
        orderTraceabilityUseCase.getOrderHistory(specificOrderId);

        // Assert
        verify(orderTraceRepository, times(1)).findByOrderId(specificOrderId);
        verify(orderTraceRepository, times(1)).findByOrderId(any(Long.class));
    }

    @Test
    void getOrderHistory_WhenOrderHasSingleTrace_ShouldReturnListWithOneElement() {
        // Arrange
        List<OrderTrace> expectedTraces = Collections.singletonList(orderTrace1);
        when(orderTraceRepository.findByOrderId(orderId)).thenReturn(expectedTraces);

        // Act
        List<OrderTrace> result = orderTraceabilityUseCase.getOrderHistory(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderTrace1, result.get(0));
        assertEquals(OrderStatus.PENDING, result.get(0).getPreviousStatus());
        assertEquals(OrderStatus.IN_PREPARATION, result.get(0).getNewStatus());
        verify(orderTraceRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void createTrace_ShouldSaveTraceWithTimestamp() {
        // Arrange
        OrderTrace inputTrace = OrderTrace.builder()
                .orderId(orderId)
                .clientId(100L)
                .clientEmail("client@test.com")
                .previousStatus(OrderStatus.PENDING)
                .newStatus(OrderStatus.IN_PREPARATION)
                .employeeId(200L)
                .employeeEmail("employee@test.com")
                .build();

        OrderTrace savedTrace = inputTrace.toBuilder()
                .id(1L)
                .timestamp(LocalDateTime.now())
                .build();

        when(orderTraceRepository.save(any(OrderTrace.class))).thenReturn(savedTrace);

        // Act
        OrderTrace result = orderTraceabilityUseCase.createTrace(inputTrace);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getTimestamp());
        assertEquals(savedTrace.getId(), result.getId());
        verify(orderTraceRepository, times(1)).save(any(OrderTrace.class));
    }

    @Test
    void createTrace_ShouldNotModifyOriginalTraceObject() {
        // Arrange
        OrderTrace inputTrace = OrderTrace.builder()
                .orderId(orderId)
                .clientId(100L)
                .clientEmail("client@test.com")
                .previousStatus(OrderStatus.PENDING)
                .newStatus(OrderStatus.IN_PREPARATION)
                .employeeId(200L)
                .employeeEmail("employee@test.com")
                .build();

        OrderTrace savedTrace = inputTrace.toBuilder()
                .id(1L)
                .timestamp(LocalDateTime.now())
                .build();

        when(orderTraceRepository.save(any(OrderTrace.class))).thenReturn(savedTrace);

        // Act
        orderTraceabilityUseCase.createTrace(inputTrace);

        // Assert
        assertNull(inputTrace.getTimestamp(), "Original trace should not have timestamp modified");
        verify(orderTraceRepository, times(1)).save(any(OrderTrace.class));
    }
}