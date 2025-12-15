package co.com.bancolombia.jpa.entity.ordertrace;

import co.com.bancolombia.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_traces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderTraceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "client_email", nullable = false)
    private String clientEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private OrderStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private OrderStatus newStatus;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_email")
    private String employeeEmail;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
