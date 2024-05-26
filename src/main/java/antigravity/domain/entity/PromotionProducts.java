package antigravity.domain.entity;

import antigravity.domain.dto.PromotionProductsDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionProducts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "promotion_id")
    private int promotionId;

    @Column(name = "product_id")
    private int productId;

    public static PromotionProducts dtoToEntity(PromotionProductsDto promotionProductsDto) {
        return builder().id(promotionProductsDto.getId())
                .promotionId(promotionProductsDto.getPromotionId())
                .productId(promotionProductsDto.getProductId()).build();
    }

}


