package co.com.bancolombia.jpa.entity.ordertrace;

import co.com.bancolombia.jpa.helper.AdapterOperations;
import co.com.bancolombia.model.ordertrace.OrderTrace;
import co.com.bancolombia.model.ordertrace.gateways.OrderTraceRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderTraceJPARepositoryAdapter extends AdapterOperations<OrderTrace, OrderTraceEntity, Long, OrderTraceJPARepository>
        implements OrderTraceRepository {

    public OrderTraceJPARepositoryAdapter(OrderTraceJPARepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, OrderTrace.class));
    }

    @Override
    public OrderTrace save(OrderTrace orderTrace) {
        OrderTraceEntity entity = mapper.map(orderTrace, OrderTraceEntity.class);
        OrderTraceEntity savedEntity = repository.save(entity);
        return mapper.map(savedEntity, OrderTrace.class);
    }

    @Override
    public List<OrderTrace> findByOrderId(Long orderId) {
        List<OrderTraceEntity> entities = repository.findByOrderIdOrderByTimestampAsc(orderId);
        return entities.stream()
                .map(entity -> mapper.map(entity, OrderTrace.class))
                .toList();
    }
}
