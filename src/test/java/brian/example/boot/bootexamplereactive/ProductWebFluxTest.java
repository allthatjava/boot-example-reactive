package brian.example.boot.bootexamplereactive;

import brian.example.boot.bootexamplereactive.controller.ProductController;
import brian.example.boot.bootexamplereactive.model.Product;
import brian.example.boot.bootexamplereactive.repo.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@WebFluxTest(controllers = {ProductController.class})
class ProductWebFluxTest {

    @MockBean
    private ProductRepository repo;
    @Autowired
    private WebTestClient webClient;

    @Test
    void testFlux(){
        Flux<String> publisher = Flux.just("Apple", "Banana", "Pineapple");

        StepVerifier.create(publisher).expectNextCount(3).verifyComplete();
    }

    @Test
    void testProductPost(){
        // Given
        Product product = new Product(1L,"iPhone",199L);
        // When
        Mockito.when(repo.save(product)).thenReturn(Mono.just(product));
        // Then
        webClient.post().uri("/products").contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(product))
                .exchange().expectStatus().is2xxSuccessful();
    }

    @Test
    void testProductGet(){
        // Given
        Product product = new Product(1L,"iPhone",199L);
        // When
        Mockito.when(repo.findById(1L)).thenReturn(Mono.just(product));
        // Then
        webClient.get().uri("/products/{id}", 1L)
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.id").isEqualTo(1L)
                .jsonPath("$.productName").isEqualTo("iPhone");
    }
}
