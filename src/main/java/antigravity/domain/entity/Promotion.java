package antigravity.domain.entity;

import antigravity.domain.dto.PromotionDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String promotion_type; //쿠폰 타입 (쿠폰, 코드)
    private String name;
    private String discount_type; // WON : 금액 할인, PERCENT : %할인
    private int discount_value; // 할인 금액 or 할인 %
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate use_started_at; // 쿠폰 사용가능 시작 기간
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate use_ended_at; // 쿠폰 사용가능 종료 기간


    public static Promotion dtoToEntity(PromotionDto promotionDto){

        return builder().id(promotionDto.getId())
                .promotion_type(promotionDto.getPromotion_type())
                .name(promotionDto.getName())
                .discount_type(promotionDto.getDiscount_type())
                .discount_value(promotionDto.getDiscount_value())
                .use_started_at(promotionDto.getUse_started_at())
                .use_ended_at(promotionDto.getUse_ended_at())
                .build();
    }

}
