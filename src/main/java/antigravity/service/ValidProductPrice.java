package antigravity.service;

import lombok.Getter;

@Getter
public enum ValidProductPrice {
    MIN(10000), MAX(10000000);
    private final int price;

    ValidProductPrice(int price) {
        this.price = price;
    }

}
