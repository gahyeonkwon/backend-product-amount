package antigravity.domain.dto;

import antigravity.domain.entity.Product;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ProductDto {
    private int id;
    private String name;
    private int price;

    public static ProductDto entityToDto(Product product) {
        return builder().id(product.getId())
                .name(product.getName())
                .price(product.getPrice()).build();
    }
}
