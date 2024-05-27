package antigravity.service;

import antigravity.domain.dto.PromotionDto;

import java.util.List;

public interface DiscountRequest {

    int getDiscount(List<PromotionDto> promotionDtoList, int inputPrice);

    static boolean priceIsNotZero(int price) {
        return price > 0;
    }
    static boolean isDiscountAvailable(int productPrice, int discountPrice) {
        return productPrice > discountPrice;
    }
}
