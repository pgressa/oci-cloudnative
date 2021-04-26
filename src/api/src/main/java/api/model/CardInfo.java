package api.model;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.Size;
import java.util.Objects;

@Introspected
public class CardInfo {
    private final String ccv;

    @Size(min = 16, max = 16)
    private final String longNum;

    @Size(min = 4, max = 4)
    private final String expires;

    public CardInfo(String ccv, String longNum, String expires) {
        this.ccv = ccv;
        this.longNum = longNum;
        this.expires = expires;
    }

    public String getCcv() {
        return ccv;
    }

    public String getLongNum() {
        return longNum;
    }

    public String getExpires() {
        return expires;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardInfo cardInfo = (CardInfo) o;
        return longNum.equals(cardInfo.longNum) && expires.equals(cardInfo.expires);
    }

    @Override
    public int hashCode() {
        return Objects.hash(longNum, expires);
    }
}
