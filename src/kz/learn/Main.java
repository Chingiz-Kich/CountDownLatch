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
             */
            executorService.submit(new Processor(countDownLatch));
        }

        /**
         * executorService.shutdown() - даем знать нашему ExecutorService, чтобы прекратить сабмит новых заданий
         * Мы назначили все задания
         * Этим 3 потокам необходимо взяться за выполнение задании
         */
        executorService.shutdown();

        /**
         * Таким образом у нас в программе появляется 4 потока
         * 3 потока - отсчитывают у CountDownLatch
         * поток Main - будет ждать пока защелка откроется
         * countDownLatch.await() - сauses the current thread to wait until the latch has counted down to zero, unless the thread is interrupted.
         */
        countDownLatch.await();

        /**
         * После того как защелка откроется, выводим сообщение
         */
        System.out.println("Latch has been opened, main thread is proceeding");
    }
}

class Processor implements Runnable {

    private CountDownLatch countDownLatch;

    public Processor(CountDownLatch countDownLatch) {
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
         * countDown() - метод отсчитывает назад заданный нами в конструкторе count
         */
        countDownLatch.countDown();
    }
}
