package antigravity.service;

import antigravity.domain.dto.PromotionDto;
import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.exception.CustomException;
import antigravity.exception.code.ProductAmountErrorCode5xx;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static antigravity.domain.dto.PromotionDto.entityToDto;
import static java.lang.Math.floor;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductService  {

    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;

    public ProductAmountResponse getProductAmount(ProductInfoRequest request) {

        // System.out.println("상품 가격 추출 로직을 완성 시켜주세요.");
        Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new CustomException(ProductAmountErrorCode5xx.CANNOT_FOUND_PRODUCT));
        List<Promotion> promotions = promotionRepository.findPromotionByProductId(product.getId(), getCouponIds(request), LocalDate.now(ZoneId.of("Asia/Seoul")));
        if(promotions.isEmpty()) throw new CustomException(ProductAmountErrorCode5xx.CANNOT_FOUND_PROMOTION);
        List<PromotionDto> promotionsDto = promotions.stream().map(p -> entityToDto(p)).toList();

        List<PromotionDto> codes = promotionsDto.stream().filter(p -> p.getPromotion_type().equalsIgnoreCase("CODE")).toList();
        List<PromotionDto> coupons = promotionsDto.stream().filter(p -> p.getPromotion_type().equalsIgnoreCase("COUPON")).toList();

        int originPrice = product.getPrice();
        int finalPrice = originPrice;
        int totalDiscountPrice = 0;
        boolean nextDiscount = true;

        List<PromotionDto> sortedCodes = codes.stream().sorted().toList();
        int codeDiscountPrice = 0;

        for(PromotionDto promotionDto : sortedCodes) {
            int discountPercent = promotionDto.getDiscount_value();
            if(priceIsNotZero(finalPrice)) {
                int discountPrice = finalPrice *  discountPercent / 100 ;
                log.info(" code.discountPercent = {}", discountPercent);
                log.info(" code.discountPrice = {}", discountPrice);
                if(isDiscountAvailable(finalPrice, discountPrice)) {
                    finalPrice = finalPrice - discountPrice;
                    codeDiscountPrice += discountPrice;
                } else {
                    nextDiscount = false;
                    break;
                }
            } else {
                nextDiscount = false;
                break;
            }
        } // code promotion

        int couponDiscountPrice = 0;
        if(nextDiscount) {
            for(PromotionDto promotionDto : coupons) {
                int discountPrice = promotionDto.getDiscount_value();
                if(priceIsNotZero(finalPrice) && isDiscountAvailable(finalPrice, discountPrice)) {
                    finalPrice = finalPrice - discountPrice;
                    couponDiscountPrice += discountPrice;
                } else { break; }
            }
        } // coupon promotion

        totalDiscountPrice = codeDiscountPrice + couponDiscountPrice;
        log.info("nextDiscount = {}", nextDiscount);
        log.info("originPrice = {} ", originPrice);
        log.info("discountPrice = {}", totalDiscountPrice);

        if(!priceIsNotZero(finalPrice)) throw new CustomException(ProductAmountErrorCode5xx.FINAL_PRICE_IS_MINUS);

        finalPrice = cutFinalPrice(finalPrice);

        log.info("finalPrice = {}", finalPrice);

        return ProductAmountResponse.builder().
                name(product.getName())
                .originPrice(originPrice)
                .discountPrice(totalDiscountPrice)
                .finalPrice(finalPrice).build();
    }

    private static List<Integer> getCouponIds(ProductInfoRequest request) {
        return Arrays.stream(request.getCouponIds()).boxed().toList();
    }

    private static int cutFinalPrice(int finalPrice) throws CustomException {
        finalPrice = (int) (floor(finalPrice / 1000) * 1000);
        return finalPrice;
    }

    private boolean priceIsNotZero(int price) {
        return price > 0;
    }
    private boolean isDiscountAvailable(int productPrice, int discountPrice) {
        return productPrice > discountPrice;
    }


}
