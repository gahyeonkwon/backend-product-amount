package antigravity.domain.entity;

import antigravity.domain.dto.ProductDto;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private int price;

    public static Product dtoToEntity(ProductDto productDto) {
        return builder().id(productDto.getId())
                .name(productDto.getName())
                .price(productDto.getPrice())
                .build();
    }
}
