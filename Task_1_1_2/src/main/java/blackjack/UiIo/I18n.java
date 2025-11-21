package blackjack.UiIo;

import blackjack.models.Card;
import blackjack.models.Rank;
import blackjack.models.Suit;
import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public final class I18n {

    private I18n() {
    }

    public enum Msg {
        // общие/меню
        GREETING, ASK_ENTER, THANKS_BYE, PLAY_AGAIN_Q,
        PROMPT_10, INPUT_PROMPT, SCORE_TITLE,

        // ход/действия
        YOUR_TURN,

        // руки
        PLAYER, DEALER_NAME, SUM, DEALER_HOLE_HIDDEN,

        // итоги
        RESULT,
        OUTCOME_PLAYER_BLACKJACK, OUTCOME_DEALER_BLACKJACK, OUTCOME_BOTH_BLACKJACK,
        OUTCOME_PLAYER_BUST, OUTCOME_DEALER_BUST,
        OUTCOME_PLAYER_WIN, OUTCOME_DEALER_WIN, OUTCOME_PUSH,

        // масти и номиналы
        SUIT_SPADES, SUIT_HEARTS, SUIT_DIAMONDS, SUIT_CLUBS,
        RANK_2, RANK_3, RANK_4, RANK_5, RANK_6, RANK_7, RANK_8, RANK_9, RANK_10,
        RANK_JACK, RANK_QUEEN, RANK_KING, RANK_ACE
    }

    public enum Lang {
        RU("Русский", new Locale("ru"), buildRu()),
        EN("English", Locale.ENGLISH, buildEn());

        private final String displayName;
        private final Locale locale;
        private final Map<Msg, String> dict;

        Lang(String displayName, Locale locale, Map<Msg, String> dict) {
            this.displayName = displayName;
            this.locale = locale;
            this.dict = dict;
        }

        public String displayName() {
            return displayName;
        }

        public String t(Msg key, Object... args) {
            String pattern = dict.getOrDefault(key, key.name());
            MessageFormat mf = new MessageFormat(pattern, locale);
            if (args == null || args.length == 0) {
                return pattern;
            } else {
                return mf.format(args);
            }
        }

        public static Lang fromOptionOrDefault(int option) {
            return switch (option) {
                case 0 -> RU;
                case 1 -> EN;
                default -> EN;
            };
        }

        public String suit(Suit s) {
            return switch (s) {
                case SPADES   -> t(Msg.SUIT_SPADES);
                case HEARTS   -> t(Msg.SUIT_HEARTS);
                case DIAMONDS -> t(Msg.SUIT_DIAMONDS);
                case CLUBS    -> t(Msg.SUIT_CLUBS);
            };
        }

        public String rank(Rank r) {
            return switch (r) {
                case TWO   -> t(Msg.RANK_2);
                case THREE -> t(Msg.RANK_3);
                case FOUR  -> t(Msg.RANK_4);
                case FIVE  -> t(Msg.RANK_5);
                case SIX   -> t(Msg.RANK_6);
                case SEVEN -> t(Msg.RANK_7);
                case EIGHT -> t(Msg.RANK_8);
                case NINE  -> t(Msg.RANK_9);
                case TEN   -> t(Msg.RANK_10);
                case JACK  -> t(Msg.RANK_JACK);
                case QUEEN -> t(Msg.RANK_QUEEN);
                case KING  -> t(Msg.RANK_KING);
                case ACE   -> t(Msg.RANK_ACE);
            };
        }

        public String card(Card c) {
            return rank(c.getRank()) + " " + suit(c.getSuit());
        }

        // ===== RU =====
        private static Map<Msg, String> buildRu() {
            EnumMap<Msg, String> m = new EnumMap<>(Msg.class);

            m.put(Msg.SUIT_SPADES, "Пики");
            m.put(Msg.SUIT_HEARTS, "Черви");
            m.put(Msg.SUIT_DIAMONDS, "Бубны");
            m.put(Msg.SUIT_CLUBS, "Крести");

            m.put(Msg.RANK_2, "2");
            m.put(Msg.RANK_3, "3");
            m.put(Msg.RANK_4, "4");
            m.put(Msg.RANK_5, "5");
            m.put(Msg.RANK_6, "6");
            m.put(Msg.RANK_7, "7");
            m.put(Msg.RANK_8, "8");
            m.put(Msg.RANK_9, "9");
            m.put(Msg.RANK_10, "10");
            m.put(Msg.RANK_JACK, "Валет");
            m.put(Msg.RANK_QUEEN, "Дама");
            m.put(Msg.RANK_KING, "Король");
            m.put(Msg.RANK_ACE, "Туз");

            m.put(Msg.GREETING, """
                Приветствуем Вас в игре Блэкджек!""");
            m.put(Msg.ASK_ENTER, """
                Желаете ли вы войти в игру?
                1 - Начать игру
                0 - Выйти из игры""");
            m.put(Msg.THANKS_BYE, "Спасибо за игру! Пока!");
            m.put(Msg.PLAY_AGAIN_Q, """
                Сыграем ещё?
                1 - Начать игру
                0 - Выйти из игры""");
            m.put(Msg.PROMPT_10, "Ожидается 1 или 0. Повторите ввод: ");
            m.put(Msg.INPUT_PROMPT, "> ");
            m.put(Msg.YOUR_TURN, "Ваш ход: 1 - Взять карту, 0 - Остановиться");
            m.put(Msg.PLAYER, "Игрок");
            m.put(Msg.DEALER_NAME, "Дилер");
            m.put(Msg.SUM, "сумма");
            m.put(Msg.DEALER_HOLE_HIDDEN, "[{0}, ??]");
            m.put(Msg.RESULT, "Итог: {0}");
            m.put(Msg.OUTCOME_PLAYER_BLACKJACK, "Блэкджек у игрока");
            m.put(Msg.OUTCOME_DEALER_BLACKJACK, "Блэкджек у дилера");
            m.put(Msg.OUTCOME_BOTH_BLACKJACK, "У обоих блэкджек (ничья)");
            m.put(Msg.OUTCOME_PLAYER_BUST, "Перебор у игрока");
            m.put(Msg.OUTCOME_DEALER_BUST, "Перебор у дилера");
            m.put(Msg.OUTCOME_PLAYER_WIN, "Игрок победил");
            m.put(Msg.OUTCOME_DEALER_WIN, "Дилер победил");
            m.put(Msg.OUTCOME_PUSH, "Ничья");
            m.put(Msg.SCORE_TITLE, "ОБЩИЙ СЧЁТ:");
            return Map.copyOf(m);
        }

        // ===== EN =====
        private static Map<Msg, String> buildEn() {
            EnumMap<Msg, String> m = new EnumMap<>(Msg.class);

            m.put(Msg.SUIT_SPADES, "Spades");
            m.put(Msg.SUIT_HEARTS, "Hearts");
            m.put(Msg.SUIT_DIAMONDS, "Diamonds");
            m.put(Msg.SUIT_CLUBS, "Clubs");

            m.put(Msg.RANK_2, "2");
            m.put(Msg.RANK_3, "3");
            m.put(Msg.RANK_4, "4");
            m.put(Msg.RANK_5, "5");
            m.put(Msg.RANK_6, "6");
            m.put(Msg.RANK_7, "7");
            m.put(Msg.RANK_8, "8");
            m.put(Msg.RANK_9, "9");
            m.put(Msg.RANK_10, "10");
            m.put(Msg.RANK_JACK, "Jack");
            m.put(Msg.RANK_QUEEN, "Queen");
            m.put(Msg.RANK_KING, "King");
            m.put(Msg.RANK_ACE, "Ace");

            m.put(Msg.GREETING, """
                Welcome to Blackjack!""");
            m.put(Msg.ASK_ENTER, """
                Do you want to enter the game?
                1 - Start
                0 - Exit""");
            m.put(Msg.THANKS_BYE, "Thanks for playing! Bye!");
            m.put(Msg.PLAY_AGAIN_Q, """
                Play another round?
                1 - Start
                0 - Exit""");
            m.put(Msg.PROMPT_10, "Expected 1 or 0. Try again: ");
            m.put(Msg.INPUT_PROMPT, "> ");
            m.put(Msg.YOUR_TURN, "Your move: 1 - Hit, 0 - Stand");
            m.put(Msg.PLAYER, "Player");
            m.put(Msg.DEALER_NAME, "Dealer");
            m.put(Msg.SUM, "total");
            m.put(Msg.DEALER_HOLE_HIDDEN, "[{0}, ??]");
            m.put(Msg.RESULT, "Result: {0}");
            m.put(Msg.OUTCOME_PLAYER_BLACKJACK, "Player blackjack");
            m.put(Msg.OUTCOME_DEALER_BLACKJACK, "Dealer blackjack");
            m.put(Msg.OUTCOME_BOTH_BLACKJACK, "Double blackjack (push)");
            m.put(Msg.OUTCOME_PLAYER_BUST, "Player bust");
            m.put(Msg.OUTCOME_DEALER_BUST, "Dealer bust");
            m.put(Msg.OUTCOME_PLAYER_WIN, "Player wins");
            m.put(Msg.OUTCOME_DEALER_WIN, "Dealer wins");
            m.put(Msg.OUTCOME_PUSH, "Push");
            m.put(Msg.SCORE_TITLE, "SCORE:");
            return Map.copyOf(m);
        }

        public java.util.Locale locale() {
            return locale;
        }
    }

    public static String localizeOutcome(blackjack.engine.Game.Outcome o, Lang lang) {
        return switch (o) {
            case PLAYER_BLACKJACK -> lang.t(Msg.OUTCOME_PLAYER_BLACKJACK);
            case DEALER_BLACKJACK -> lang.t(Msg.OUTCOME_DEALER_BLACKJACK);
            case BOTH_BLACKJACK -> lang.t(Msg.OUTCOME_BOTH_BLACKJACK);
            case PLAYER_BUST -> lang.t(Msg.OUTCOME_PLAYER_BUST);
            case DEALER_BUST -> lang.t(Msg.OUTCOME_DEALER_BUST);
            case PLAYER_WIN -> lang.t(Msg.OUTCOME_PLAYER_WIN);
            case DEALER_WIN -> lang.t(Msg.OUTCOME_DEALER_WIN);
            case PUSH -> lang.t(Msg.OUTCOME_PUSH);
        };
    }

}
