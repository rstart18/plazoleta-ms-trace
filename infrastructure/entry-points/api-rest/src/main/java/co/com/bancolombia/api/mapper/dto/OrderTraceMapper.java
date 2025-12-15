package co.com.bancolombia.api.mapper.dto;

import co.com.bancolombia.api.dto.request.OrderTraceRequest;
import co.com.bancolombia.api.dto.response.OrderTraceResponse;
import co.com.bancolombia.model.ordertrace.OrderTrace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderTraceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    OrderTrace toModel(OrderTraceRequest dto);

    OrderTraceResponse toResponseDto(OrderTrace orderTrace);
}
