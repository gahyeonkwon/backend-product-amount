package antigravity.repository;

import antigravity.domain.entity.Promotion;

import java.time.LocalDate;
import java.util.List;

public interface PromotionRepositoryCustom {

    List<Promotion> findPromotionByProductId(Integer productId,  List<Integer> couponIds, LocalDate validDate);
}
