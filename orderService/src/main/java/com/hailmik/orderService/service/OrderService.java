package com.hailmik.orderService.service;

import com.hailmik.orderService.dto.InventoryResponse;
import com.hailmik.orderService.dto.OrderLineItemsDto;
import com.hailmik.orderService.dto.OrderRequest;
import com.hailmik.orderService.model.Order;
import com.hailmik.orderService.model.OrderLineItems;
import com.hailmik.orderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	private final OrderRepository orderRepository;

	private final WebClient.Builder webClientBuilder;

	public void placeOrder(OrderRequest orderRequest) throws IllegalAccessException {

		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());

		List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtoList()
				.stream()
				.map(this::mapToDto)
				.toList();

		order.setOrderLineItemsList(orderLineItemsList);

		List<String> skuCodes = order.getOrderLineItemsList()
				.stream()
				.map(OrderLineItems::getSkuCode)
				.toList();
		//Call inventory service and place order if the product is in stock
		InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
				.uri(
						"http://inventory-service/api/inventory",
						uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build()
						)
				.retrieve()
				.bodyToMono(InventoryResponse[].class)
				.block();

		boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);


		if (allProductsInStock) {
			orderRepository.save(order);
		} else {
			throw new IllegalAccessException("Product is not in stock, please try again later");
		}
	}

	private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		return orderLineItems;
	}

}
