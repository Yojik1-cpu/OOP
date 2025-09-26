package blackjack.engine;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Колода из 52 карт.
 * Поддерживает тасовку и выдачу карт по одной.
 */
public class Deck {
    private final int[] deckArr;
    private int cnt = 0;

    //создание колоды
    public Deck() {
        deckArr = new int[52];
        for (int i = 0; i < 52; i++) {
            deckArr[i] = i;
        }
        shuffle();
    }

    // алгоритм Фишера-Йейтса для тасовки
    public void shuffle() {
        for (int i = deckArr.length - 1; i > 0; i--) {
            int j = ThreadLocalRandom.current().nextInt(i + 1);
            int tmp = deckArr[i];
            deckArr[i] = deckArr[j];
            deckArr[j] = tmp;
        }
    }

    //взять карту из колоды
    public int drawCard() {
        return deckArr[cnt++];
    }

    //сброс колоды
    public void reset() {
        for (int i = 0; i < 52; i++) {
            deckArr[i] = i;
        }
        shuffle();

        cnt = 0;
    }
}

