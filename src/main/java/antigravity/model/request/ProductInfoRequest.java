package antigravity.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductInfoRequest {

    @NotBlank
    private int productId;

    @NotBlank
    private int[] couponIds;
}
