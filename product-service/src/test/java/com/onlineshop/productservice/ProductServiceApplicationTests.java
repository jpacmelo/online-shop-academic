package com.onlineshop.productservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

import com.onlineshop.productservice.dto.ProductRequestDTO;
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
	}

	private ProductRequestDTO createProduct(String name, String description, BigDecimal price) {
		return ProductRequestDTO.builder()
			.name(name)
			.description(description)
			.price(price)
			.build();
	}

}
