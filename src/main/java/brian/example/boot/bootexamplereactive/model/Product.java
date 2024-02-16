package brian.example.boot.bootexamplereactive.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    private Long id;
    @NotBlank
    @Size(max=140)
    private String productName;
    private Long price;

    public Product(String productName){
        this.productName = productName;
    }
}
