package blackjack.UiIo;

import blackjack.engine.Game;
import blackjack.models.Card;
import blackjack.models.Rank;
import blackjack.models.Suit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class I18nSmokeTest {

    @Test
    void endToEnd_LocalizationBasics() {
        I18n.Lang ru = I18n.Lang.RU;
        I18n.Lang en = I18n.Lang.EN;

        assertEquals("Пики", ru.suit(Suit.SPADES));
        assertEquals("Черви", ru.suit(Suit.HEARTS));
        assertEquals("Бубны", ru.suit(Suit.DIAMONDS));
        assertEquals("Крести", ru.suit(Suit.CLUBS));
        assertEquals("2", ru.rank(Rank.TWO));
        assertEquals("10", ru.rank(Rank.TEN));
        assertEquals("Валет", ru.rank(Rank.JACK));
        assertEquals("Дама", ru.rank(Rank.QUEEN));
        assertEquals("Король", ru.rank(Rank.KING));
        assertEquals("Туз", ru.rank(Rank.ACE));

        assertEquals("Spades", en.suit(Suit.SPADES));
        assertEquals("Hearts", en.suit(Suit.HEARTS));
        assertEquals("Diamonds", en.suit(Suit.DIAMONDS));
        assertEquals("Clubs", en.suit(Suit.CLUBS));
        assertEquals("2", en.rank(Rank.TWO));
        assertEquals("10", en.rank(Rank.TEN));
        assertEquals("Jack", en.rank(Rank.JACK));
        assertEquals("Queen", en.rank(Rank.QUEEN));
        assertEquals("King", en.rank(Rank.KING));
        assertEquals("Ace", en.rank(Rank.ACE));

        Card aceSpades = new Card(Suit.SPADES, Rank.ACE);
        assertEquals("Туз Пики", ru.card(aceSpades));
        assertEquals("Ace Spades", en.card(aceSpades));

        String holeRu = ru.t(I18n.Msg.DEALER_HOLE_HIDDEN).replace("{0}", ru.card(aceSpades));
        String holeEn = en.t(I18n.Msg.DEALER_HOLE_HIDDEN).replace("{0}", en.card(aceSpades));
        assertEquals("[Туз Пики, ??]", holeRu);
        assertEquals("[Ace Spades, ??]", holeEn);

        assertEquals("Итог: Игрок победил", ru.t(I18n.Msg.RESULT, "Игрок победил"));
        assertEquals("Result: Player wins", en.t(I18n.Msg.RESULT, "Player wins"));

        assertEquals("Игрок победил", I18n.localizeOutcome(Game.Outcome.PLAYER_WIN, ru));
        assertEquals("Дилер победил", I18n.localizeOutcome(Game.Outcome.DEALER_WIN, ru));
        assertEquals("Ничья", I18n.localizeOutcome(Game.Outcome.PUSH, ru));
        assertEquals("У обоих блэкджек (ничья)", I18n.localizeOutcome(Game.Outcome.BOTH_BLACKJACK, ru));

        assertEquals("Player wins", I18n.localizeOutcome(Game.Outcome.PLAYER_WIN, en));
        assertEquals("Dealer wins", I18n.localizeOutcome(Game.Outcome.DEALER_WIN, en));
        assertEquals("Push", I18n.localizeOutcome(Game.Outcome.PUSH, en));
        assertEquals("Double blackjack (push)", I18n.localizeOutcome(Game.Outcome.BOTH_BLACKJACK, en));

        assertEquals(I18n.Lang.RU, I18n.Lang.fromOptionOrDefault(0));
        assertEquals(I18n.Lang.EN, I18n.Lang.fromOptionOrDefault(1));
        assertEquals(I18n.Lang.EN, I18n.Lang.fromOptionOrDefault(42));
    }
}
