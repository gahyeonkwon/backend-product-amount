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
import static antigravity.service.DiscountRequest.priceIsNotZero;
import static java.lang.Math.floor;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductService  {

    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;

    public ProductAmountResponse getProductAmount(ProductInfoRequest request) {

        // System.out.println("상품 가격 추출 로직을 완성 시켜주세요.");
        if (request == null) throw new CustomException(ProductAmountErrorCode5xx.INVALID_REQUEST);

        Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new CustomException(ProductAmountErrorCode5xx.CANNOT_FOUND_PRODUCT));

         if(!productPriceValidCheck(product)) throw new CustomException(ProductAmountErrorCode5xx.INVALID_PRODUCT_PRICE);

        List<Promotion> promotions = promotionRepository.findPromotionByProductId(product.getId(), getCouponIds(request), LocalDate.now(ZoneId.of("Asia/Seoul")));
        if(promotions.isEmpty()) throw new CustomException(ProductAmountErrorCode5xx.CANNOT_FOUND_PROMOTION);
        List<PromotionDto> promotionsDto = promotions.stream().map(p -> entityToDto(p)).toList();

        List<PromotionDto> codes = promotionsDto.stream().filter(p -> p.getPromotion_type().equalsIgnoreCase("CODE")).toList();
        List<PromotionDto> coupons = promotionsDto.stream().filter(p -> p.getPromotion_type().equalsIgnoreCase("COUPON")).toList();

        int originPrice = product.getPrice();
        int finalPrice = originPrice;
        int totalDiscountPrice = 0;

        List<PromotionDto> sortedCodes = codes.stream().sorted().toList();
        int codeDiscountPrice = new CodeDiscountRequest().getDiscount(sortedCodes, finalPrice);

        finalPrice = minusPromotionalPrice(finalPrice, codeDiscountPrice);
        int couponDiscountPrice = new CouponDiscountRequest().getDiscount(coupons, finalPrice);

        finalPrice = minusPromotionalPrice(finalPrice, couponDiscountPrice);

        if(!priceIsNotZero(finalPrice)) throw new CustomException(ProductAmountErrorCode5xx.FINAL_PRICE_IS_MINUS);

        finalPrice = cutFinalPrice(finalPrice);

        totalDiscountPrice = codeDiscountPrice + couponDiscountPrice;

        return ProductAmountResponse.builder().
                name(product.getName())
                .originPrice(originPrice)
                .discountPrice(totalDiscountPrice)
                .finalPrice(finalPrice).build();
    }


    private int minusPromotionalPrice(int finalPrice, int discountPrice) {
        finalPrice = finalPrice - discountPrice;
        return finalPrice;
    }

    private boolean productPriceValidCheck(Product product) {
        return product.getPrice() <= ValidProductPrice.MAX.getPrice();
    }

    private List<Integer> getCouponIds(ProductInfoRequest request) {
        return Arrays.stream(request.getCouponIds()).boxed().toList();
    }

    private int cutFinalPrice(int finalPrice) throws CustomException {
        finalPrice = (int) (floor(finalPrice / 1000) * 1000);
        return finalPrice;
    }




}
