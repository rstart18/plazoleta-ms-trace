package co.com.bancolombia.jpa.entity.ordertrace;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

public interface OrderTraceJPARepository extends JpaRepository<OrderTraceEntity, Long>, QueryByExampleExecutor<OrderTraceEntity> {
    List<OrderTraceEntity> findByOrderIdOrderByTimestampAsc(Long orderId);
}
