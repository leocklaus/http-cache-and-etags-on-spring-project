package io.github.leocklaus.cache_etag_demo;

import io.github.leocklaus.cache_etag_demo.domain.entity.PaymentType;
import io.github.leocklaus.cache_etag_demo.domain.entity.Product;
import io.github.leocklaus.cache_etag_demo.domain.repository.PaymentTypeRepository;
import io.github.leocklaus.cache_etag_demo.domain.repository.ProductRepository;
import io.github.leocklaus.cache_etag_demo.domain.service.PaymentTypeService;
import io.github.leocklaus.cache_etag_demo.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@SpringBootApplication
public class CacheEtagDemoApplication implements CommandLineRunner {

	private final ProductRepository productRepository;
	private final PaymentTypeRepository paymentTypeRepository;

	public static void main(String[] args) {

		SpringApplication.run(CacheEtagDemoApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		Product product1 = Product.builder()
				.id(1L)
				.name("Computer")
				.price(BigDecimal.valueOf(2500L))
				.createdAt(LocalDateTime.now())
				.build();

		Product product2 = Product.builder()
				.id(2L)
				.name("Mouse")
				.price(BigDecimal.valueOf(100L))
				.createdAt(LocalDateTime.now())
				.build();

		Product product3 = Product.builder()
				.id(3L)
				.name("Keyboard")
				.price(BigDecimal.valueOf(200L))
				.createdAt(LocalDateTime.now())
				.build();

		PaymentType cash = PaymentType.builder()
				.id(1L)
				.name("Cash")
				.createdAt(LocalDateTime.now())
				.build();

		PaymentType card = PaymentType.builder()
				.id(2L)
				.name("Card")
				.createdAt(LocalDateTime.now())
				.build();

		productRepository.save(product1);
		productRepository.save(product2);
		productRepository.save(product3);
		paymentTypeRepository.save(cash);
		paymentTypeRepository.save(card);

	}
}
