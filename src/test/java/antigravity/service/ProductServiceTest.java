package antigravity.service;

import antigravity.config.QuerydslConfigTest;
import antigravity.domain.dto.PromotionDto;
import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.PromotionRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.ZoneId;
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


    @Test
    @DisplayName("코드별 할인혜택 분리")
    void classifyPromotions() {

        //given
        int productId  = 1;
        List<Promotion> promotions = promotionRepository.findPromotionByProductId(productId, LocalDate.now(ZoneId.of("Asia/Seoul")));
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
    @DisplayName("할인적용")
    void getProductAmount() {

        //given
        int productId  = 1;

        List<Promotion> promotions = promotionRepository.findPromotionByProductId(productId, LocalDate.now(ZoneId.of("Asia/Seoul")));
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
        finalPrice = getFinalPrice(finalPrice);

        //then
        assertThat(originPrice).isEqualTo(215000);
        assertThat(codeDiscountPrice).isEqualTo(32250);
        assertThat(couponDiscountPrice).isEqualTo(30000);
        assertThat(totalDiscountPrice).isEqualTo(62250);
        assertThat(finalPrice).isEqualTo(152000);

    }

    private static int getFinalPrice(int finalPrice) {
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