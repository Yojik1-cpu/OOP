package blackjack.models;

import blackjack.UiIo.I18n;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hand {
    private final List<Card> cards = new ArrayList<>();

    private int cachedValue = 0;
    private boolean cacheValid = false;

    public void addCard(Card card) {
        cards.add(card);
        cacheValid = false;
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    private int computeValue() {
        int sum = 0, aces = 0;
        for (Card c : cards) {
            sum += c.getRank().value();
            if (c.getRank().isAce()) {
                aces++;
            }
        }
        while (sum > 21 && aces-- > 0) sum -= 10;
        return sum;
    }

    private int lowAcesCount() {
        int sum = 0, aces = 0, lowered = 0;
        for (Card c : cards) {
            sum += c.getRank().value();
            if (c.getRank().isAce()) {
                aces++;
            }
        }
        while (sum > 21 && aces - lowered > 0) {
            sum -= 10;
            lowered++;
        }
        return lowered;
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
        int lowAces = lowAcesCount();
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < cards.size(); i++) {
            if (i > 0) sb.append(", ");
            Card c = cards.get(i);
            sb.append(lang.card(c));

            int shown;
            if (c.getRank().isAce()) {
                if (lowAces > 0) {
                    shown = 1;
                    lowAces--;
                } else {
                    shown = 11;
                }
            } else {
                shown = c.getRank().value();
            }
            sb.append(" (").append(shown).append(')');
        }
        sb.append(']');
        sb.append(" (").append(lang.t(I18n.Msg.SUM)).append(' ')
                .append(getValue()).append(')');
        return sb.toString();
    }

    public String toDisplayWithHoleHiddenLocalized(I18n.Lang lang) {
        if (cards.isEmpty()) {
            return "[]";
        }
        int lowAces = lowAcesCount();
        Card firstCard = cards.get(0);
        String firstName = lang.card(firstCard);
        int firstShown;
        if (firstCard.getRank().isAce()) {
            int aceIndexSeen = 0;
            for (Card c : cards) {
                if (c.getRank().isAce()) {
                    if (c == firstCard) {
                        break;
                    }
                    aceIndexSeen++;
                }
            }
            if (aceIndexSeen < lowAces && lowAces > 0) {
                firstShown = 1;
            } else {
                firstShown = 11;
            }
        } else {
            firstShown = firstCard.getRank().value();
        }

        String pattern = lang.t(I18n.Msg.DEALER_HOLE_HIDDEN);
        String firstWithValue = firstName + " (" + firstShown + ")";
        return new MessageFormat(pattern, lang.locale()).format(new Object[]{firstWithValue});
    }
}
