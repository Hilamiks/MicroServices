package com.hailmik.productService.controller;

import com.hailmik.productService.dto.ProductRequest;
import com.hailmik.productService.dto.ProductResponse;
import com.hailmik.productService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void createProduct(@RequestBody ProductRequest product) {

		productService.createProduct(product);

	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<ProductResponse> getAllProducts() {

		return productService.getAllProducts();

	}


}
