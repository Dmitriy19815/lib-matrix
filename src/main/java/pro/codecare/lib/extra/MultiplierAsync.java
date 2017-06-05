package pro.codecare.lib.extra;

import pro.codecare.lib.Matrix;

import java.util.concurrent.Callable;

/**
 *
 */
public class MultiplierAsync implements Callable {
  private int[][] first;
  private int[][] second;
  private int[][] result;
  private int firstIndex;
  private int lastIndex;
  private int amountCount;

  public MultiplierAsync(Matrix sourceLeft, Matrix sourceRight, Matrix destination, int startBound, int endBound) {
    this.first = sourceLeft.getValues();
    this.second = sourceRight.getValues();
    this.result = destination.getValues();
    this.amountCount = sourceRight.getRows();
    this.firstIndex = startBound;
    this.lastIndex = endBound;
  }

  @Override
  public Object call() throws Exception {
    System.out.println("Calculating cells from " + firstIndex + " to " + lastIndex + " in callable task...");
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
    return Integer.valueOf(lastIndex - firstIndex);
  }
}