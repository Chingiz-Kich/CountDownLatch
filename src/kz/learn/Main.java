package kz.learn;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    /**
     * CountDownLatch - потокобезопасный класс, поэтому нам вообще не надо задумываться о синх потоков
     * Для этого и создан пакет java.util.concurrent
     */

    public static void main(String[] args) throws InterruptedException {
        /**
         * CountDownLatch - (перевод) защелка обратного отсчета
         * При создании мы должны указать в аргументе count
         * count - количество итерации которые мы должны отсчитать назад, прежде чем защелка отопрется
         */
        CountDownLatch countDownLatch = new CountDownLatch(3);

        /**
         * ExecutorService - для создания пул потоков, это и есть пул потоков
         */
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            /**
             * Передаем задание каждому потоку (их у нас 3)
             * Каждому потоку при создании Processor будем передавать id
             * Все потоки будут с разными айдишками
             */
            executorService.submit(new Processor(i, countDownLatch));
        }

        /**
         * executorService.shutdown() - даем знать нашему ExecutorService, чтобы прекратить сабмит новых заданий
         * Мы назначили все задания
         * Этим 3 потокам необходимо взяться за выполнение задании
         */
        executorService.shutdown();

        for (int i = 0; i < 3; i++) {
            /**
             * countDown() - метод отсчитывает назад заданный нами в конструкторе count
             */
            countDownLatch.countDown();
        }
    }
}

class Processor implements Runnable {

    private final int id;
    private final CountDownLatch countDownLatch;

    public Processor(int id, CountDownLatch countDownLatch) {
        this.id = id;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /**
         * Теперь три потока будут ждать пока countDownLatch станет 0
         * Как только countDownLatch будет равен 0
         * countDownLatch.await() - откроет защелку
         * И все три потока одновременно продолжат свое выполнение
         */
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /**
         * Как только защелка откроется все три потока выведут сообщение
         * И мы увидим, что все три разных потока продолжили свое выполнение
         */
        System.out.println("Current thread with id: " + id + " proceeded.");
    }
}
