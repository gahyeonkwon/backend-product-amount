package antigravity.service;

import antigravity.domain.dto.PromotionDto;
import antigravity.domain.entity.Promotion;
import antigravity.exception.CustomException;
import antigravity.exception.code.ProductAmountErrorCode5xx;
import antigravity.model.request.ProductInfoRequest;
import antigravity.repository.PromotionRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static antigravity.domain.dto.PromotionDto.entityToDto;
import static java.lang.Math.floor;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
class ProductServiceTest {

    @Autowired
    PromotionRepository promotionRepository;

    @Autowired
    ProductService productService;


    @Test
    @DisplayName("코드별 할인혜택 분리")
    void classifyPromotions() {

        //given
        int productId  = 1;
        int[] couponIds = {3, 4};
        List<Integer> ids = Arrays.stream(couponIds).boxed().toList();
        List<Promotion> promotions = promotionRepository.findPromotionByProductId(productId, ids, LocalDate.now(ZoneId.of("Asia/Seoul")));
        List<PromotionDto> promotionsDto = promotions.stream().map(p -> entityToDto(p)).toList();

        //when
        List<PromotionDto> codes = promotionsDto.stream().filter(p -> p.getPromotion_type().equalsIgnoreCase("CODE")).toList();
        List<PromotionDto> coupons = promotionsDto.stream().filter(p -> p.getPromotion_type().equalsIgnoreCase("COUPON")).toList();
        List<PromotionDto> sortedCodes = codes.stream().sorted().toList();

        assertThat(codes.size()).isEqualTo(1);
        assertThat(coupons.size()).isEqualTo(1);
        assertThat(sortedCodes.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("천단위 절삭")
    void cutFinalPrice() {
        //given
        int price = 500;
        int price2 = 1500;

        //when
        int finalPrice = cutFinalPrice(price);
        int finalPrice2 = cutFinalPrice(price2);

        //then
        assertThat(finalPrice).isEqualTo(0);
        assertThat(finalPrice2).isEqualTo(1000);
    }


    @Test
    @DisplayName("할인적용")
    void getProductAmount() {

        //given
        int productId  = 1;
        int[] couponIds = {3, 4};
        List<Integer> ids = Arrays.stream(couponIds).boxed().toList();

        List<Promotion> promotions = promotionRepository.findPromotionByProductId(productId, ids, LocalDate.now(ZoneId.of("Asia/Seoul")));
        List<PromotionDto> promotionsDto = promotions.stream().map(p -> entityToDto(p)).toList();

        List<PromotionDto> codes = promotionsDto.stream().filter(p -> p.getPromotion_type().equalsIgnoreCase("CODE")).toList();
        List<PromotionDto> coupons = promotionsDto.stream().filter(p -> p.getPromotion_type().equalsIgnoreCase("COUPON")).toList();
        int originPrice = 215000;
        int finalPrice = originPrice;
        int totalDiscountPrice = 0;
        boolean nextDiscount = true;

        //when
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

        log.info("nextDiscount = {}", nextDiscount);
        totalDiscountPrice = codeDiscountPrice + couponDiscountPrice;

        // 1000 단위 절삭
        finalPrice = cutFinalPrice(finalPrice);

        //then
        assertThat(originPrice).isEqualTo(215000);
        assertThat(codeDiscountPrice).isEqualTo(32250);
        assertThat(couponDiscountPrice).isEqualTo(30000);
        assertThat(totalDiscountPrice).isEqualTo(62250);
        assertThat(finalPrice).isEqualTo(152000);

    }

    @Test
    @DisplayName("FINAL_PRICE_IS_MINUS 에러 발생 테스트 ")
    void make5xxError_1() {
        //given
        int finalPrice = -10000;

        //when, then
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> minusPriceTest(finalPrice));
        assertThat(customException.getErrorCode().name()).isEqualTo("FINAL_PRICE_IS_MINUS");

    }
    @Test
    @DisplayName("CANNOT_FOUND_PRODUCT 에러 발생 테스트 ")
    void make5xxError_2() {
        //given
        int[] couponIds = {1, 2};
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(2)
                .couponIds(couponIds)
                .build();

        //when, then
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> productService.getProductAmount(request));
        assertThat(customException.getErrorCode().name()).isEqualTo("CANNOT_FOUND_PRODUCT");

    }

    @Test
    @DisplayName("CANNOT_FOUND_PROMOTION 에러 발생 테스트 ")
    void make5xxError_3() {
        //given
        int[] couponIds = {5, 6};
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(1)
                .couponIds(couponIds)
                .build();

        //when, then
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> productService.getProductAmount(request));
        assertThat(customException.getErrorCode().name()).isEqualTo("CANNOT_FOUND_PROMOTION");
    }

    @Test
    @DisplayName("INVALID_REQUEST 에러 발생 테스트")
    void make5xxError_4() {
        //given
        ProductInfoRequest request = null;

        //when, then
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> productService.getProductAmount(request));
        assertThat(customException.getErrorCode().name()).isEqualTo("INVALID_REQUEST");
    }

    @Test
    @DisplayName("INVALID_PRODUCT_PRICE 에러 발생 테스트")
    void make5xxError_5() {
        //given
        int[] couponIds = {5, 6};
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(2)
                .couponIds(couponIds)
                .build();

        //when, then
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> productService.getProductAmount(request));
        assertThat(customException.getErrorCode().name()).isEqualTo("INVALID_PRODUCT_PRICE");
    }

    @Test
    @DisplayName("할인적용_2_프로모션타입별_객체분리")
    void getProductAmount2() {

        //given
        int productId  = 1;
        int[] couponIds = {3, 4};
        List<Integer> ids = Arrays.stream(couponIds).boxed().toList();

        List<Promotion> promotions = promotionRepository.findPromotionByProductId(productId, ids, LocalDate.now(ZoneId.of("Asia/Seoul")));
        List<PromotionDto> promotionsDto = promotions.stream().map(p -> entityToDto(p)).toList();

        List<PromotionDto> codes = promotionsDto.stream().filter(p -> p.getPromotion_type().equalsIgnoreCase("CODE")).toList();
        List<PromotionDto> coupons = promotionsDto.stream().filter(p -> p.getPromotion_type().equalsIgnoreCase("COUPON")).toList();
        int originPrice = 215000;
        int finalPrice = originPrice;
        int totalDiscountPrice = 0;

        //when
        List<PromotionDto> sortedCodes = codes.stream().sorted().toList();
        int codeDiscountPrice = new CodeDiscountRequest().getDiscount(sortedCodes, finalPrice);
        log.info("codeDiscountPrice = {}", codeDiscountPrice);

        finalPrice = minusPromotionalPrice(finalPrice, codeDiscountPrice);
        int couponDiscountPrice = new CouponDiscountRequest().getDiscount(coupons, finalPrice);
        log.info("couponDiscountPrice = {}", couponDiscountPrice);

        finalPrice = minusPromotionalPrice(finalPrice, couponDiscountPrice);
        log.info("finalPrice = {}", finalPrice);

        totalDiscountPrice = codeDiscountPrice + couponDiscountPrice;

        // 1000 단위 절삭
        finalPrice = cutFinalPrice(finalPrice);

        //then
        assertThat(originPrice).isEqualTo(215000);
        assertThat(codeDiscountPrice).isEqualTo(32250);
        assertThat(couponDiscountPrice).isEqualTo(30000);
        assertThat(totalDiscountPrice).isEqualTo(62250);
        assertThat(finalPrice).isEqualTo(152000);

    }

    private int minusPromotionalPrice(int finalPrice, int discountPrice) {
        finalPrice = finalPrice - discountPrice;
        return finalPrice;
    }

    private void minusPriceTest(int finalPrice) {
        if (!priceIsNotZero(finalPrice)) throw new CustomException(ProductAmountErrorCode5xx.FINAL_PRICE_IS_MINUS);
    }
    private static int cutFinalPrice(int finalPrice) {
        finalPrice = (int) (floor(finalPrice /1000) * 1000);
        return finalPrice;
    }

    private boolean priceIsNotZero(int price) {
        return price > 0;
    }
    private boolean isDiscountAvailable(int productPrice, int discountPrice) {
        return productPrice > discountPrice;
    }

}