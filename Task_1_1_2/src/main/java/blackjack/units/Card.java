package blackjack.units;

/**
 * Карта стандартной колоды (52 карты).
 * Хранит масть и ранг.
 */
public class Card {
    private final String suit;
    private final String rank;

    private static final String[] SUITS = {
            "Пики", "Черви", "Бубны", "Крести"
    };

    private static final String[] RANKS = {
            "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "Валет", "Дама", "Король", "Туз"
    };

    // конструктор из id = 0...51
    public Card(int id) {
        int suitIndex = id / 13;
        int rankIndex = id % 13;

        this.suit = SUITS[suitIndex];
        this.rank = RANKS[rankIndex];
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    // переопределение
    @Override
    public String toString() {
        return rank + " " + suit;
    }
}
