package brian.example.boot.bootexamplereactive.repo;

import brian.example.boot.bootexamplereactive.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, Long> {
}
