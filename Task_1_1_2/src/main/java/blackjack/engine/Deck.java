package blackjack.engine;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Колода из 52 карт.
 * Поддерживает тасовку и выдачу карт по одной.
 */
public class Deck {
    private final int[] deckArr;
    private int cnt = 0;

    /**
     * Создаёт новую колоду из 52 карт и выполняет начальную тасовку.
     */
    public Deck() {
        deckArr = new int[52];
        for (int i = 0; i < 52; i++) {
            deckArr[i] = i;
        }
        shuffle();
    }

    /**
     * Перемешивает колоду с использованием алгоритма Фишера-Йейтса.
     */
    public void shuffle() {
        for (int i = deckArr.length - 1; i > 0; i--) {
            int j = ThreadLocalRandom.current().nextInt(i + 1);
            int tmp = deckArr[i];
            deckArr[i] = deckArr[j];
            deckArr[j] = tmp;
        }
    }

    /**
     * Берёт следующую карту из колоды.
     */
    public int drawCard() {
        return deckArr[cnt++];
    }

    /**
     * Сбрасывает колоду: возвращает все 52 карты,
     * перемешивает и обнуляет счётчик.
     */
    public void reset() {
        for (int i = 0; i < 52; i++) {
            deckArr[i] = i;
        }
        shuffle();
        cnt = 0;
    }
}

