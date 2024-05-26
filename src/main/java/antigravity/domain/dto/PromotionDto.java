package antigravity.domain.dto;

import antigravity.domain.entity.Promotion;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PromotionDto implements Comparable<PromotionDto> {
    private int id;
    private String promotion_type; //쿠폰 타입 (쿠폰, 코드)
    private String name;
    private String discount_type; // WON : 금액 할인, PERCENT : %할인
    private int discount_value; // 할인 금액 or 할인 %
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate use_started_at; // 쿠폰 사용가능 시작 기간
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate use_ended_at; // 쿠폰 사용가능 종료 기간

    public static PromotionDto entityToDto(Promotion promotion) {
        return builder().id(promotion.getId())
                .promotion_type(promotion.getPromotion_type())
                .name(promotion.getName())
                .discount_type(promotion.getDiscount_type())
                .discount_value(promotion.getDiscount_value())
                .use_started_at(promotion.getUse_started_at())
                .use_ended_at(promotion.getUse_ended_at()).build();
    }

    @Override
    public int compareTo(PromotionDto o) {
        //  내림차순 (desc) 로 정렬
        return o.discount_value - this.discount_value;
    }
}
