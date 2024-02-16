### Spring Boot Reactive example 

Including following end point as example.
```java
@GetMapping("/reactive")    // example of Flux<Object> returns
void getProducts(){...}

@GetMapping(value="/reactive/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
Publisher<String> getProductsWithFlux(){...}    // Continously returns response values as Stream

@GetMapping(value="/reactive/mono", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
Mono<Long> getProductsWithMono(){...}       // Returns single value as Stream

//////////////////////// CRUD examples with MongoDB //////////////////////////////////
@GetMapping("")     // Returns all data from MongoDB as reactive response
Flux<Product> getProductsAll(){...}

@GetMapping("/{id}")    // Return single data object as result
public Mono<ResponseEntity<Product>> getProductById(@PathVariable("id") Long productId){...}

@PostMapping("")    // Adds posted data into database returns result 
Mono<Product> postProducts(@RequestBody Product product){...}

@PutMapping("/{id}")        // Updates existing data in MongoDB.
public Mono<ResponseEntity<Product>> updateProduct(@PathVariable("id") Long id,
                                                   @Valid @RequestBody Product product)


@DeleteMapping("/{id}")     // Delete the existing data
public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable("id") Long id){...}

@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<Product> streamAllProducts(){...}   // Stream the all data found from MongoDB
``` 