package pro.codecare.lib;

import pro.codecare.lib.entity.AMatrix;
import pro.codecare.lib.extra.*;

import java.util.Random;
import java.util.concurrent.*;

/**
 *
 */
public class Matrix extends AMatrix {

  public Matrix() {
    this(1 + (new Random()).nextInt(99), 1 + (new Random()).nextInt(99));
  }

  public Matrix(int rows, int columns) {
    super(rows, columns);
  }

  // Calculate performs in the threads
  public void MultiplicationConcurrent(AMatrix firstLeft, AMatrix secondRight, int type) {
    switch (type) {
      case 0:
        MultiplySync((Matrix) firstLeft, (Matrix) secondRight);
        break;
      case 1:
        MultiplyAsync((Matrix) firstLeft, (Matrix) secondRight);
        break;
      default:
        System.out.println("Undefined action");
    }
  }

  private void MultiplyAsync(Matrix firstLeft, Matrix secondRight) {
    if (firstLeft.getColumns() != secondRight.getRows()) {
      throw new ArrayIndexOutOfBoundsException(_ERR_MSG_2);
    }

    final int threadCount = 2 * Runtime.getRuntime().availableProcessors();
    ExecutorService executorPool = Executors.newFixedThreadPool(2 * Runtime.getRuntime().availableProcessors() + 1);

    int rowCount = firstLeft.getRows();
    int colCount = secondRight.getColumns();
    this.ResizeMatrix(rowCount, colCount);

    // Число вычисляемых ячеек на единичную задачу
    int cellsForThread = (rowCount * colCount) / threadCount;
    // Индекс первой вычисляемой ячейки
    int firstIndex = 0;

    // Массив потоков
    Future[] calculatorFutures = new Future[threadCount];

    // Создание и запуск потоков
    for (int threadIndex = threadCount - 1; threadIndex >= 0; --threadIndex) {

      int lastIndex = firstIndex + cellsForThread;  // Индекс последней вычисляемой ячейки.

      if (threadIndex == 0) {
      /* Один из потоков должен будет вычислить не только свой блок ячеек,
        но и остаток, если число ячеек не делится нацело на число потоков. */
        lastIndex = rowCount * colCount;
      }

      MultiplierAsync task = new MultiplierAsync(firstLeft, secondRight, this, firstIndex, lastIndex);

      calculatorFutures[threadIndex] = executorPool.submit(task);
      System.out.println("Task #" + threadIndex + " is submitted for calculating");
      firstIndex = lastIndex;
    }

    try {
      // Ожидание завершения потоков
      for (Future task : calculatorFutures) {

        while (!task.isDone()) {
          // System.out.println("Cycle task #" + ft.getClass().getSimpleName() + " is not completed yet....");
          Thread.sleep(50); // sleep for 50 millisecond before checking again
        }
        int result = (Integer) task.get();
        System.out.println("Task #" + task.getClass().getSimpleName() + " is calculated "+result+" values");
      }
    }
    catch (ExecutionException | InterruptedException e) {
      System.err.println(e.toString());
    }

    try {
      executorPool.shutdown();
      executorPool.awaitTermination(1, TimeUnit.SECONDS);
    }
    catch (InterruptedException e) {
      System.err.println("Tasks interrupted");
    }
    finally {
      if (!executorPool.isTerminated()) {
        System.err.println("Canceling non-finished tasks");
      }
      executorPool.shutdownNow();
      System.out.println("All tasks are finished");
    }
  }

  private void MultiplySync(Matrix firstLeft, Matrix secondRight) {
    if (firstLeft.getColumns() != secondRight.getRows()) {
      throw new ArrayIndexOutOfBoundsException(_ERR_MSG_2);
    }

    final int threadCount = 2 * Runtime.getRuntime().availableProcessors();

    int rowCount = firstLeft.getRows();
    int colCount = secondRight.getColumns();
    this.ResizeMatrix(rowCount, colCount);

    // Число вычисляемых ячеек на поток
    int cellsForThread = (rowCount * colCount) / threadCount;
    // Индекс первой вычисляемой ячейки
    int firstIndex = 0;

    // Массив потоков
    Thread[] calculatorRunnables = new Thread[threadCount];

    // Создание и запуск потоков
    for (int threadIndex = threadCount - 1; threadIndex >= 0; --threadIndex) {

      int lastIndex = firstIndex + cellsForThread;  // Индекс последней вычисляемой ячейки.

      if (threadIndex == 0) {
      /* Один из потоков должен будет вычислить не только свой блок ячеек,
        но и остаток, если число ячеек не делится нацело на число потоков. */
        lastIndex = rowCount * colCount;
      }
      // Thread thread1 = new Thread(task1);

      calculatorRunnables[threadIndex] =
          new Thread(new MultiplierSync(firstLeft, secondRight, this, firstIndex, lastIndex));

      calculatorRunnables[threadIndex].start();
      System.out.println("Runnable thread #" + threadIndex + " is started for calculating");
      firstIndex = lastIndex;
    }

    try {
      // Ожидание завершения потоков
      for (Thread threadIn : calculatorRunnables)
        threadIn.join(50);
    }
    catch (InterruptedException e) {
      System.err.println(e.toString());
    }
    System.out.println("All threads are finished");
  }

}