package com.onlineshop.productservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.onlineshop.productservice.dto.ProductRequestDTO;
import com.onlineshop.productservice.dto.ProductResponseDTO;
import com.onlineshop.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = PostgresqlContainerInitializer.class)
class ProductServiceApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;
	@LocalServerPort
	private int port;
	@Autowired
	ProductRepository productRepository;

	private static final String PRODUCT_PREFIX = "/api/product";

	@Test
	void Should_CreateProduct_When_RequestBodyIsValid() throws URISyntaxException {

		final String baseUrl = "http://localhost:" + port + PRODUCT_PREFIX;
		URI uri = new URI(baseUrl);
		ProductRequestDTO productRequestDTO = createProduct("iPhone13", "iPhone13", BigDecimal.valueOf(1200));

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<ProductRequestDTO> requestEntity = new HttpEntity<>(productRequestDTO, headers);
		ResponseEntity<String> result = restTemplate.postForEntity(uri, requestEntity, String.class);

		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		assertEquals(1, productRepository.findAll().size());
	}

	@Test
	void Should_ReturnProductList_When_GetAllProducts() throws URISyntaxException {
		final String baseUrl = "http://localhost:" + port + PRODUCT_PREFIX;
		URI uri = new URI(baseUrl);
		ProductRequestDTO productRequestDTO = createProduct("iPhone12", "iPhone12", BigDecimal.valueOf(1000));

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<ProductRequestDTO> requestEntity = new HttpEntity<>(productRequestDTO, headers);
		restTemplate.postForEntity(uri, requestEntity, String.class);
		ResponseEntity<List> response = restTemplate.getForEntity(uri, List.class);
		List<ProductResponseDTO> products = response.getBody();

		assertEquals(1, products.size());
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	private ProductRequestDTO createProduct(String name, String description, BigDecimal price) {
		return ProductRequestDTO.builder()
			.name(name)
			.description(description)
			.price(price)
			.build();
	}

}
