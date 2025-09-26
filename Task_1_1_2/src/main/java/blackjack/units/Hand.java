package blackjack.units;

import java.util.ArrayList;
import java.util.List;

/**
 * Рука игрока или дилера.
 * Хранит карты и вычисляет значение руки.
 */
public class Hand {
    private final List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        cards.add(card);
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getValue() {
        int sum = 0;
        int aces = 0;

        for (Card card : cards) {
            String rank = card.getRank();
            if (rank.equals("Туз")) {
                sum += 11;
                aces++;
            } else if (rank.equals("Валет") || rank.equals("Дама") || rank.equals("Король")) {
                sum += 10;
            } else {
                sum += Integer.parseInt(rank);
            }
        }

        while (sum > 21 && aces > 0) {
            sum -= 10;
            aces--;
        }

        return sum;
    }

    public boolean isBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    public boolean isBust() {
        return getValue() > 21;
    }

    public void clear() {
        cards.clear();
    }


}
