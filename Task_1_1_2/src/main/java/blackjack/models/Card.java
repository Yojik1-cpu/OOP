package blackjack.models;

public class Card {
    private final Suit suit;
    private final Rank rank;

    // id = 0..51
    public Card(int id) {
        int suitIndex = id / 13;
        int rankIndex = id % 13;
        this.suit = Suit.values()[suitIndex];
        this.rank = Rank.values()[rankIndex];
    }

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank + " " + suit;
    }
}
