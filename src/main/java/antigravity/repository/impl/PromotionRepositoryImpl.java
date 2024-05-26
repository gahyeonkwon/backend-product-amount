package antigravity.repository.impl;

import antigravity.domain.entity.Promotion;
import antigravity.repository.PromotionRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static antigravity.domain.entity.QPromotion.promotion;
import static antigravity.domain.entity.QPromotionProducts.promotionProducts;


@Repository
@RequiredArgsConstructor
public class PromotionRepositoryImpl implements PromotionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Promotion> findPromotionByProductId(Integer productId, LocalDate validDate) {

        List<Promotion> promotions = queryFactory.select(promotion)
                .from(promotion)
                .leftJoin(promotionProducts).on(promotion.id.eq(promotionProducts.promotionId))
                .where(promotionProducts.productId.eq(productId))
                .where(promotion.use_started_at.before(validDate).and(promotion.use_ended_at.after(validDate))).fetch();

        return promotions;

    }
}
