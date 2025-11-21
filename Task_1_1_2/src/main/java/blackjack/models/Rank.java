package blackjack.models;

public enum Rank {
    TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7),
    EIGHT(8), NINE(9), TEN(10),
    JACK(10), QUEEN(10), KING(10),
    ACE(11);

    private final int baseValue;

    Rank(int baseValue) { this.baseValue = baseValue; }

    public int value() { return baseValue; }

    public boolean isAce() { return this == ACE; }
}
