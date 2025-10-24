package blackjack.models;

import blackjack.UiIo.I18n;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Hand {
    private final List<Card> cards = new ArrayList<>();

    private int cachedValue = 0;
    private boolean cacheValid = false;

    public void addCard(Card card) {
        cards.add(card);
        cacheValid = false; // инвалидируем кэш
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    private int computeValue() {
        int sum = 0, aces = 0;
        for (Card c : cards) {
            sum += c.getRank().value();
            if (c.getRank().isAce()) aces++;
        }
        while (sum > 21 && aces-- > 0) sum -= 10;
        return sum;
    }

    public int getValue() {
        if (!cacheValid) {
            cachedValue = computeValue();
            cacheValid = true;
        }
        return cachedValue;
    }

    public boolean isBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    public boolean isBust() {
        return getValue() > 21;
    }

    public void clear() {
        cards.clear();
        cacheValid = false;
    }

    @Override
    public String toString() {
        return cards.toString();
    }

    public String toDisplayLocalized(I18n.Lang lang) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < cards.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(lang.card(cards.get(i)));
        }
        sb.append(']');
        sb.append(" (").append(lang.t(I18n.Msg.SUM)).append(' ')
                .append(getValue()).append(')');
        return sb.toString();
    }

    public String toDisplayWithHoleHiddenLocalized(I18n.Lang lang) {
        if (cards.isEmpty()) return "[]";
        String pattern = lang.t(I18n.Msg.DEALER_HOLE_HIDDEN);
        String first = lang.card(cards.get(0));
        return new MessageFormat(pattern, lang.locale()).format(new Object[]{ first });
    }
}
