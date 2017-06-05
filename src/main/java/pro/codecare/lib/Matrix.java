package pro.codecare.lib;

import pro.codecare.lib.entity.AMatrix;
import pro.codecare.lib.extra.*;

import java.util.Random;
import java.util.concurrent.*;

/**
 * Facade class for Matrix utilities for calculation of multiplication for two matrix.
 * Implements the methods for the concurrent matrix calculations in asynchronous and synchronous thread concepts.
 */
public class Matrix extends AMatrix {

  /**
   * Facade for default constructor of matrix object without dimensions
   */
  public Matrix() {
    this(1 + (new Random()).nextInt(bound), 1 + (new Random()).nextInt(bound));
  }

  /**
   * Facade for constructor of matrix object with with defined dimensions
   *
   * @param rows
   * @param columns
   */
  public Matrix(int rows, int columns) {
    super(rows, columns);
  }

  /**
   * Implementation of method for the concurrent matrix calculations.
   *
   * @param firstLeft     First source matrix object
   * @param secondRight   Second source matrix object
   */
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

  /**
   * The asynchronous thread concept of the matrix calculation's implementation
   *
   * @param firstLeft     First source matrix object
   * @param secondRight   Second source matrix object
   */
  private void MultiplyAsync(Matrix firstLeft, Matrix secondRight) {
    if (firstLeft.getColumns() != secondRight.getRows()) {
      throw new ArrayIndexOutOfBoundsException(_ERR_MSG_2);
    }

    final int threadCount = 2 * Runtime.getRuntime().availableProcessors();
    ExecutorService executorPool = Executors.newFixedThreadPool(2 * Runtime.getRuntime().availableProcessors() + 1);

    int rowCount = firstLeft.getRows();
    int colCount = secondRight.getColumns();
    this.ResizeMatrix(rowCount, colCount);

    int cellsForThread = (rowCount * colCount) / threadCount;
    int firstIndex = 0;

    Future[] calculatorFutures = new Future[threadCount];

    for (int threadIndex = threadCount - 1; threadIndex >= 0; --threadIndex) {

      int lastIndex = firstIndex + cellsForThread;  // Индекс последней вычисляемой ячейки.

      if (threadIndex == 0) {
        /**
         *  One of the threads will have to calculate not only its block of cells,
         *  But the remainder, if the number of cells is not divided by the number of threads.
         */
        lastIndex = rowCount * colCount;
      }

      MultiplierAsync task = new MultiplierAsync(firstLeft, secondRight, this, firstIndex, lastIndex);

      calculatorFutures[threadIndex] = executorPool.submit(task);
      System.out.println("Task #" + threadIndex + " is submitted for calculating");
      firstIndex = lastIndex;
    }

    try {
      for (Future task : calculatorFutures) {

        while (!task.isDone()) {
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

  /**
   * The synchronous thread concept of the matrix calculation's implementation
   *
   * @param firstLeft     First source matrix object
   * @param secondRight   Second source matrix object
   */
  private void MultiplySync(Matrix firstLeft, Matrix secondRight) {
    if (firstLeft.getColumns() != secondRight.getRows()) {
      throw new ArrayIndexOutOfBoundsException(_ERR_MSG_2);
    }

    final int threadCount = 2 * Runtime.getRuntime().availableProcessors();

    int rowCount = firstLeft.getRows();
    int colCount = secondRight.getColumns();
    this.ResizeMatrix(rowCount, colCount);

    int cellsForThread = (rowCount * colCount) / threadCount;
    int firstIndex = 0;

    Thread[] calculatorRunnables = new Thread[threadCount];

    for (int threadIndex = threadCount - 1; threadIndex >= 0; --threadIndex) {
      int lastIndex = firstIndex + cellsForThread;

      if (threadIndex == 0) {
      /**
       *  One of the threads will have to calculate not only its block of cells,
       *  But the remainder, if the number of cells is not divided by the number of threads.
       */
        lastIndex = rowCount * colCount;
      }
      calculatorRunnables[threadIndex] =
          new Thread(new MultiplierSync(firstLeft, secondRight, this, firstIndex, lastIndex));
      calculatorRunnables[threadIndex].start();
      System.out.println("Runnable thread #" + threadIndex + " is started for calculating");
      firstIndex = lastIndex;
    }

    try {
      for (Thread threadIn : calculatorRunnables)
        threadIn.join(50);
    }
    catch (InterruptedException e) {
      System.err.println(e.toString());
    }
    System.out.println("All threads are finished");
  }

}