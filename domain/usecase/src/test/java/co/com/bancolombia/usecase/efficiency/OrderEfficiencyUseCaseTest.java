package co.com.bancolombia.usecase.efficiency;

import co.com.bancolombia.model.efficiency.EmployeeEfficiency;
import co.com.bancolombia.model.efficiency.OrderEfficiency;
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
class OrderEfficiencyUseCaseTest {

    @Mock
    private OrderTraceRepository orderTraceRepository;

    @InjectMocks
    private OrderEfficiencyUseCase orderEfficiencyUseCase;

    private OrderTrace pendingTrace;
    private OrderTrace deliveredTrace;
    private OrderTrace inPreparationTrace;
    private LocalDateTime baseTime;

    @BeforeEach
    void setUp() {
        baseTime = LocalDateTime.of(2024, 12, 17, 10, 0, 0);

        pendingTrace = OrderTrace.builder()
                .id(1L)
                .orderId(1L)
                .clientId(100L)
                .clientEmail("client@test.com")
                .previousStatus(null)
                .newStatus(OrderStatus.PENDING)
                .employeeId(200L)
                .employeeEmail("employee1@test.com")
                .timestamp(baseTime)
                .build();

        inPreparationTrace = OrderTrace.builder()
                .id(2L)
                .orderId(1L)
                .clientId(100L)
                .clientEmail("client@test.com")
                .previousStatus(OrderStatus.PENDING)
                .newStatus(OrderStatus.IN_PREPARATION)
                .employeeId(200L)
                .employeeEmail("employee1@test.com")
                .timestamp(baseTime.plusMinutes(10))
                .build();

        deliveredTrace = OrderTrace.builder()
                .id(3L)
                .orderId(1L)
                .clientId(100L)
                .clientEmail("client@test.com")
                .previousStatus(OrderStatus.READY)
                .newStatus(OrderStatus.DELIVERED)
                .employeeId(200L)
                .employeeEmail("employee1@test.com")
                .timestamp(baseTime.plusMinutes(30))
                .build();
    }

    // ==================== getOrderEfficiency Tests ====================

    @Test
    void getOrderEfficiency_WhenOrderHasPendingAndDeliveredStates_ShouldCalculateDuration() {
        // Arrange
        Long orderId = 1L;
        List<OrderTrace> traces = Arrays.asList(pendingTrace, inPreparationTrace, deliveredTrace);

        when(orderTraceRepository.findByOrderId(orderId)).thenReturn(traces);

        // Act
        OrderEfficiency result = orderEfficiencyUseCase.getOrderEfficiency(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals(30L, result.getDurationInMinutes());
        verify(orderTraceRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void getOrderEfficiency_WhenOrderHasNoPendingState_ShouldReturnNullDuration() {
        // Arrange
        Long orderId = 2L;
        List<OrderTrace> traces = Collections.singletonList(deliveredTrace.toBuilder().orderId(orderId).build());

        when(orderTraceRepository.findByOrderId(orderId)).thenReturn(traces);

        // Act
        OrderEfficiency result = orderEfficiencyUseCase.getOrderEfficiency(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertNull(result.getDurationInMinutes());
        verify(orderTraceRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void getOrderEfficiency_WhenOrderHasNoDeliveredState_ShouldReturnNullDuration() {
        // Arrange
        Long orderId = 3L;
        List<OrderTrace> traces = Arrays.asList(pendingTrace.toBuilder().orderId(orderId).build(),
                inPreparationTrace.toBuilder().orderId(orderId).build());

        when(orderTraceRepository.findByOrderId(orderId)).thenReturn(traces);

        // Act
        OrderEfficiency result = orderEfficiencyUseCase.getOrderEfficiency(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertNull(result.getDurationInMinutes());
        verify(orderTraceRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void getOrderEfficiency_WhenOrderDoesNotExist_ShouldReturnNullDuration() {
        // Arrange
        Long orderId = 999L;
        when(orderTraceRepository.findByOrderId(orderId)).thenReturn(Collections.emptyList());

        // Act
        OrderEfficiency result = orderEfficiencyUseCase.getOrderEfficiency(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertNull(result.getDurationInMinutes());
        verify(orderTraceRepository, times(1)).findByOrderId(orderId);
    }

    // ==================== getAllOrdersEfficiency Tests ====================

    @Test
    void getAllOrdersEfficiency_WhenMultipleOrdersAreDelivered_ShouldReturnEfficiencyListSortedByOrderId() {
        // Arrange
        OrderTrace order1Pending = pendingTrace.toBuilder().orderId(1L).build();
        OrderTrace order1Delivered = deliveredTrace.toBuilder().orderId(1L).timestamp(baseTime.plusMinutes(30)).build();

        OrderTrace order2Pending = pendingTrace.toBuilder().id(4L).orderId(2L).employeeId(201L).employeeEmail("emp2@test.com").build();
        OrderTrace order2Delivered = deliveredTrace.toBuilder().id(5L).orderId(2L).employeeId(201L).employeeEmail("emp2@test.com").timestamp(baseTime.plusMinutes(45)).build();

        List<OrderTrace> allTraces = Arrays.asList(order1Pending, order1Delivered, order2Pending, order2Delivered);

        when(orderTraceRepository.findAll()).thenReturn(allTraces);
        when(orderTraceRepository.findByOrderId(1L)).thenReturn(Arrays.asList(order1Pending, order1Delivered));
        when(orderTraceRepository.findByOrderId(2L)).thenReturn(Arrays.asList(order2Pending, order2Delivered));

        // Act
        List<OrderEfficiency> result = orderEfficiencyUseCase.getAllOrdersEfficiency();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getOrderId());
        assertEquals(2L, result.get(1).getOrderId());
        assertEquals(30L, result.get(0).getDurationInMinutes());
        assertEquals(45L, result.get(1).getDurationInMinutes());
        verify(orderTraceRepository, times(1)).findAll();
    }

    @Test
    void getAllOrdersEfficiency_WhenNoOrdersAreDelivered_ShouldReturnEmptyList() {
        // Arrange
        List<OrderTrace> allTraces = Arrays.asList(pendingTrace, inPreparationTrace);

        when(orderTraceRepository.findAll()).thenReturn(allTraces);

        // Act
        List<OrderEfficiency> result = orderEfficiencyUseCase.getAllOrdersEfficiency();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderTraceRepository, times(1)).findAll();
    }

    @Test
    void getAllOrdersEfficiency_WhenNoTracesExist_ShouldReturnEmptyList() {
        // Arrange
        when(orderTraceRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<OrderEfficiency> result = orderEfficiencyUseCase.getAllOrdersEfficiency();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderTraceRepository, times(1)).findAll();
    }

    @Test
    void getAllOrdersEfficiency_ShouldFilterOutOrdersWithoutValidDuration() {
        // Arrange
        OrderTrace order1Pending = pendingTrace.toBuilder().orderId(1L).build();
        OrderTrace order1Delivered = deliveredTrace.toBuilder().orderId(1L).build();

        OrderTrace order2Delivered = deliveredTrace.toBuilder().id(4L).orderId(2L).employeeId(201L).employeeEmail("emp2@test.com").build();

        List<OrderTrace> allTraces = Arrays.asList(order1Pending, order1Delivered, order2Delivered);

        when(orderTraceRepository.findAll()).thenReturn(allTraces);
        when(orderTraceRepository.findByOrderId(1L)).thenReturn(Arrays.asList(order1Pending, order1Delivered));
        when(orderTraceRepository.findByOrderId(2L)).thenReturn(Collections.singletonList(order2Delivered));

        // Act
        List<OrderEfficiency> result = orderEfficiencyUseCase.getAllOrdersEfficiency();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getOrderId());
        verify(orderTraceRepository, times(1)).findAll();
    }

    // ==================== getEmployeesEfficiencyRanking Tests ====================

    @Test
    void getEmployeesEfficiencyRanking_WhenMultipleEmployeesHaveOrders_ShouldReturnRankingSortedByEfficiency() {
        // Arrange
        OrderTrace emp1Order1Pending = pendingTrace.toBuilder().orderId(1L).employeeId(200L).employeeEmail("emp1@test.com").build();
        OrderTrace emp1Order1Delivered = deliveredTrace.toBuilder().orderId(1L).employeeId(200L).employeeEmail("emp1@test.com").timestamp(baseTime.plusMinutes(30)).build();

        OrderTrace emp2Order1Pending = pendingTrace.toBuilder().id(4L).orderId(2L).employeeId(201L).employeeEmail("emp2@test.com").build();
        OrderTrace emp2Order1Delivered = deliveredTrace.toBuilder().id(5L).orderId(2L).employeeId(201L).employeeEmail("emp2@test.com").timestamp(baseTime.plusMinutes(20)).build();

        List<OrderTrace> allTraces = Arrays.asList(emp1Order1Pending, emp1Order1Delivered, emp2Order1Pending, emp2Order1Delivered);

        when(orderTraceRepository.findAll()).thenReturn(allTraces);
        when(orderTraceRepository.findByOrderId(1L)).thenReturn(Arrays.asList(emp1Order1Pending, emp1Order1Delivered));
        when(orderTraceRepository.findByOrderId(2L)).thenReturn(Arrays.asList(emp2Order1Pending, emp2Order1Delivered));

        // Act
        List<EmployeeEfficiency> result = orderEfficiencyUseCase.getEmployeesEfficiencyRanking();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(201L, result.get(0).getEmployeeId());
        assertEquals(20.0, result.get(0).getAverageDurationInMinutes());
        assertEquals(200L, result.get(1).getEmployeeId());
        assertEquals(30.0, result.get(1).getAverageDurationInMinutes());
        verify(orderTraceRepository, times(1)).findAll();
    }

    @Test
    void getEmployeesEfficiencyRanking_ShouldFilterOutTracesWithNullEmployeeId() {
        // Arrange
        OrderTrace traceWithNullEmployeeId = pendingTrace.toBuilder().employeeId(null).employeeEmail(null).build();
        OrderTrace validTrace1 = pendingTrace.toBuilder().orderId(1L).employeeId(200L).employeeEmail("emp1@test.com").build();
        OrderTrace validTrace2 = deliveredTrace.toBuilder().orderId(1L).employeeId(200L).employeeEmail("emp1@test.com").build();

        List<OrderTrace> allTraces = Arrays.asList(traceWithNullEmployeeId, validTrace1, validTrace2);

        when(orderTraceRepository.findAll()).thenReturn(allTraces);
        when(orderTraceRepository.findByOrderId(1L)).thenReturn(Arrays.asList(validTrace1, validTrace2));

        // Act
        List<EmployeeEfficiency> result = orderEfficiencyUseCase.getEmployeesEfficiencyRanking();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(200L, result.get(0).getEmployeeId());
        verify(orderTraceRepository, times(1)).findAll();
    }

    @Test
    void getEmployeesEfficiencyRanking_WhenNoValidTracesExist_ShouldReturnEmptyList() {
        // Arrange
        when(orderTraceRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<EmployeeEfficiency> result = orderEfficiencyUseCase.getEmployeesEfficiencyRanking();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderTraceRepository, times(1)).findAll();
    }

    @Test
    void getEmployeesEfficiencyRanking_ShouldExcludeEmployeesWithNoProcessedOrders() {
        // Arrange
        OrderTrace traceWithoutDelivery = inPreparationTrace.toBuilder().employeeId(200L).employeeEmail("emp1@test.com").build();

        List<OrderTrace> allTraces = Collections.singletonList(traceWithoutDelivery);

        when(orderTraceRepository.findAll()).thenReturn(allTraces);

        // Act
        List<EmployeeEfficiency> result = orderEfficiencyUseCase.getEmployeesEfficiencyRanking();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderTraceRepository, times(1)).findAll();
    }

    // ==================== getEmployeeEfficiency Tests ====================

    @Test
    void getEmployeeEfficiency_WhenEmployeeHasDeliveredOrders_ShouldCalculateAverageEfficiency() {
        // Arrange
        Long employeeId = 200L;
        OrderTrace emp1Order1Pending = pendingTrace.toBuilder().orderId(1L).employeeId(employeeId).build();
        OrderTrace emp1Order1Delivered = deliveredTrace.toBuilder().orderId(1L).employeeId(employeeId).timestamp(baseTime.plusMinutes(30)).build();

        OrderTrace emp1Order2Pending = pendingTrace.toBuilder().id(4L).orderId(2L).employeeId(employeeId).timestamp(baseTime.plusHours(1)).build();
        OrderTrace emp1Order2Delivered = deliveredTrace.toBuilder().id(5L).orderId(2L).employeeId(employeeId).timestamp(baseTime.plusHours(1).plusMinutes(20)).build();

        List<OrderTrace> employeeTraces = Arrays.asList(emp1Order1Pending, emp1Order1Delivered, emp1Order2Pending, emp1Order2Delivered);

        when(orderTraceRepository.findByEmployeeId(employeeId)).thenReturn(employeeTraces);
        when(orderTraceRepository.findByOrderId(1L)).thenReturn(Arrays.asList(emp1Order1Pending, emp1Order1Delivered));
        when(orderTraceRepository.findByOrderId(2L)).thenReturn(Arrays.asList(emp1Order2Pending, emp1Order2Delivered));

        // Act
        EmployeeEfficiency result = orderEfficiencyUseCase.getEmployeeEfficiency(employeeId);

        // Assert
        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals(25.0, result.getAverageDurationInMinutes());
        assertEquals(2L, result.getProcessedOrders());
        verify(orderTraceRepository, times(1)).findByEmployeeId(employeeId);
    }

    @Test
    void getEmployeeEfficiency_WhenEmployeeHasNoOrders_ShouldReturnZeroEfficiency() {
        // Arrange
        Long employeeId = 999L;
        when(orderTraceRepository.findByEmployeeId(employeeId)).thenReturn(Collections.emptyList());

        // Act
        EmployeeEfficiency result = orderEfficiencyUseCase.getEmployeeEfficiency(employeeId);

        // Assert
        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals(0.0, result.getAverageDurationInMinutes());
        assertEquals(0L, result.getProcessedOrders());
        verify(orderTraceRepository, times(1)).findByEmployeeId(employeeId);
    }

    @Test
    void getEmployeeEfficiency_WhenEmployeeHasNoDeliveredOrders_ShouldReturnZeroEfficiency() {
        // Arrange
        Long employeeId = 200L;
        List<OrderTrace> employeeTraces = Collections.singletonList(
                inPreparationTrace.toBuilder().employeeId(employeeId).build()
        );

        when(orderTraceRepository.findByEmployeeId(employeeId)).thenReturn(employeeTraces);

        // Act
        EmployeeEfficiency result = orderEfficiencyUseCase.getEmployeeEfficiency(employeeId);

        // Assert
        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals(0.0, result.getAverageDurationInMinutes());
        assertEquals(0L, result.getProcessedOrders());
        verify(orderTraceRepository, times(1)).findByEmployeeId(employeeId);
    }

    @Test
    void getEmployeeEfficiency_WhenEmployeeEmailIsPresent_ShouldIncludeInResponse() {
        // Arrange
        Long employeeId = 200L;
        String employeeEmail = "john@restaurant.com";
        OrderTrace trace1 = pendingTrace.toBuilder().orderId(1L).employeeId(employeeId).employeeEmail(employeeEmail).build();
        OrderTrace trace2 = deliveredTrace.toBuilder().orderId(1L).employeeId(employeeId).employeeEmail(employeeEmail).build();

        List<OrderTrace> employeeTraces = Arrays.asList(trace1, trace2);

        when(orderTraceRepository.findByEmployeeId(employeeId)).thenReturn(employeeTraces);
        when(orderTraceRepository.findByOrderId(1L)).thenReturn(Arrays.asList(trace1, trace2));

        // Act
        EmployeeEfficiency result = orderEfficiencyUseCase.getEmployeeEfficiency(employeeId);

        // Assert
        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals(employeeEmail, result.getEmployeeEmail());
        verify(orderTraceRepository, times(1)).findByEmployeeId(employeeId);
    }
}