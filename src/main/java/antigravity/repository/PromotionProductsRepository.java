package antigravity.repository;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.PromotionProducts;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface PromotionProductsRepository extends JpaRepository<PromotionProducts, Integer> {
    List<PromotionProducts> findByProductId(Integer productId);
}
