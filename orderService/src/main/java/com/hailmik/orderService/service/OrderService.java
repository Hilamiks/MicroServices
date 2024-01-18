package com.hailmik.orderService.service;

import com.hailmik.orderService.dto.OrderLineItemsDto;
import com.hailmik.orderService.dto.OrderRequest;
import com.hailmik.orderService.model.Order;
import com.hailmik.orderService.model.OrderLineItems;
import com.hailmik.orderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	private final OrderRepository orderRepository;

	public void placeOrder(OrderRequest orderRequest) {

		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());

		List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtoList()
				.stream()
				.map(this::mapToDto)
				.toList();

		order.setOrderLineItemsList(orderLineItemsList);

		orderRepository.save(order);
	}

	private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		return orderLineItems;
	}

}
