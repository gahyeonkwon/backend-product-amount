package antigravity.domain.dto;

import antigravity.domain.entity.PromotionProducts;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PromotionProductsDto {
    private int id;
    private int promotionId;
    private int productId;

    public static PromotionProductsDto entityToDto(PromotionProducts promotionProducts) {
        return builder().id(promotionProducts.getId())
                .promotionId(promotionProducts.getPromotionId())
                .productId(promotionProducts.getProductId()).build();
    }
}
