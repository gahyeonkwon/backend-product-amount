package antigravity.repository;

import antigravity.config.QuerydslConfigTest;
import antigravity.domain.entity.Promotion;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import net.bytebuddy.asm.Advice;
import org.assertj.core.api.Assertions;
import org.hibernate.grammars.hql.HqlParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.beans.Expression;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static antigravity.domain.entity.QPromotion.promotion;
import static antigravity.domain.entity.QPromotionProducts.promotionProducts;
import static java.time.LocalDate.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("dev")
@Import(QuerydslConfigTest.class)
class PromotionRepositoryTest {
    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    JPAQueryFactory queryFactory;

    @Test
    @DisplayName("프로모션조회")
    void getPromotion() {
        Promotion promotion = promotionRepository
                    .findById(1).orElseThrow(() -> new NullPointerException("promotion is null"));
        assertThat(promotion.getName()).isEqualTo("30000원 할인쿠폰");

    }

    @Test
    @DisplayName("쿠폰유효기간확인")
    void getValidPromotion() {

        //given
        int productId = 1;
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        //when
        List<Promotion> promotions = queryFactory.select(promotion)
                .from(promotion)
                .leftJoin(promotionProducts).on(promotion.id.eq(promotionProducts.promotionId))
                .where(promotionProducts.productId.eq(productId))
                .where(promotion.use_started_at.before(today).and(promotion.use_ended_at.after(today))).fetch();

        List<String> promotionNames = promotions.stream().map(p -> p.getName()).collect(Collectors.toList());

        //then
        assertThat(promotionNames).contains("30000원 할인쿠폰2",  "15% 할인코드2");

    }



}