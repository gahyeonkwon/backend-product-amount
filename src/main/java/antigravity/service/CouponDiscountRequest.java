package antigravity.service;

import antigravity.domain.dto.PromotionDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static antigravity.service.DiscountRequest.isDiscountAvailable;
import static antigravity.service.DiscountRequest.priceIsNotZero;


@Slf4j
public class CouponDiscountRequest implements DiscountRequest{

    @Override
    public int getDiscount(List<PromotionDto> promotionDtoList, int inputPrice) {

        int totalDiscountPrice = 0;

        for(PromotionDto promotionDto : promotionDtoList) {
            int discountPrice = promotionDto.getDiscount_value();

            if(priceIsNotZero(inputPrice) && isDiscountAvailable(inputPrice, discountPrice)) {
                inputPrice = inputPrice - discountPrice;
                totalDiscountPrice += discountPrice;
            } else { break; }
        }
        return totalDiscountPrice;
    }
}
