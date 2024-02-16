package brian.example.boot.bootexamplereactive.controller;

import brian.example.boot.bootexamplereactive.model.Product;
import brian.example.boot.bootexamplereactive.repo.ProductRepository;
import jakarta.validation.Valid;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductRepository repo;

    @GetMapping("/reactive")
    void getProducts(){

        List<String> products = new ArrayList<>();

//        Flux.just("Apple", "Banana", "Orange", "iPhone").log().subscribe(products::add);
        Flux.just("Apple", "Banana", "Orange", "iPhone").log().subscribe(new Subscriber<String>() {

            private Subscription s;
            private int count;
            @Override
            public void onSubscribe(Subscription subscription) {
//                subscription.request(Long.MAX_VALUE);
                subscription.request(2);
                this.s = subscription;
            }

            @Override
            public void onNext(String str) {
                products.add(str);
                count++;

                if( count == 2 ){
                    count = 0;
                    logger.info("Populated products :"+products);
                    this.s.request(2);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }


        });

        logger.info(products.toString());
    }

    @GetMapping(value="/reactive/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    Publisher<Product> getProductsWithFlux(){
//        Flux<Product> products = Flux.<Product>generate(sink -> sink.next(new Product("Product Name"))).take(10000);
    Publisher<String> getProductsWithFlux(){
        Random random = new Random();
        Flux<String> products = Flux.<String>generate(sink -> sink.next("Live Temperate:"+random.nextInt(50))).delayElements(Duration.ofSeconds(1));

        return products;
    }

    @GetMapping(value="/reactive/mono", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Mono<Long> getProductsWithMono(){
        Flux<Product> products = Flux.<Product>generate(sink -> sink.next(new Product("Product Name"))).take(10);

        return products.count();
    }


    //////////////////////// CRUD examples with MongoDB //////////////////////////////////
    @GetMapping("")
    Flux<Product> getProductsAll(){
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> getProductById(@PathVariable("id") Long productId){

        return repo.findById(productId).map(product -> ResponseEntity.ok(product))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    Mono<Product> postProducts(@RequestBody Product product){
        if(product != null ) {
            return repo.save(product);
        }

        return null;
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> updateProduct(@PathVariable("id") Long id,
                                                       @Valid @RequestBody Product product)
    {
        return repo.findById(id).flatMap(existingProduct -> {
            existingProduct.setProductName(product.getProductName());
            existingProduct.setPrice(product.getPrice());
            return repo.save(existingProduct);
        }).map(updatedProduct -> ResponseEntity.ok(updatedProduct))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable("id") Long id)
    {
        return repo.findById(id).flatMap(existingProduct -> repo.delete(existingProduct)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Product> streamAllProducts(){
        return repo.findAll().delayElements(Duration.ofSeconds(1));
    }
}
