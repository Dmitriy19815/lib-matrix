package pro.codecare.lib.extra;

import pro.codecare.lib.Matrix;

/**
 * Class for concurrent implementation.
 * Used in the method of synchronous implementation of the calculation algorithm
 */
public class MultiplierSync implements Runnable {
  private int[][] first;
  private int[][] second;
  private int[][] result;
  private int firstIndex;
  private int lastIndex;
  private int amountCount;

  public MultiplierSync(Matrix sourceLeft, Matrix sourceRight, Matrix destination, int startBound, int endBound) {
    this.first = sourceLeft.getValues();
    this.second = sourceRight.getValues();
    this.result = destination.getValues();
    this.amountCount = sourceRight.getRows();
    this.firstIndex = startBound;
    this.lastIndex = endBound;
  }

  /**
   * The executed body of thread.
   */
  @Override
  public void run() {
    try {
      if (!Thread.currentThread().isInterrupted()) {
        System.out.println("Calculating cells from " + firstIndex + " to " + lastIndex + " in thread...");
        // Число столбцов результирующей матрицы
        int columnsCount = second[0].length;

        for (int index = firstIndex; index < lastIndex; ++index) {
          int row = index / columnsCount;
          int column = index % columnsCount;
          int amount = 0;

          for (int i = 0; i < amountCount; ++i)
            amount += first[row][i] * second[i][column];

          result[row][column] = amount;
        }

        System.out.println("Calculating cells from " + firstIndex + " to " + lastIndex + " finished");
      }
    } catch (Exception e) {
      System.err.println(e.toString());
      Thread.currentThread().interrupt();
    }
  }
}