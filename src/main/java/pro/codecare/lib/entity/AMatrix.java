package pro.codecare.lib.entity;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Objects;
import java.util.Random;

/**
 *
 */
public abstract class AMatrix implements Serializable {
  private static final long serialVersionUID = 1L;

  protected static String _ERR_MSG_1 = "You have to pass sizes of matrix like positive values";
  // "Число столбцов первой матрицы и число строк второй матрицы должны совпадать";
  protected static String _ERR_MSG_2 = "The number of columns in the first matrix and the number of rows in the second matrix must coincide";

  protected Long id;
  // protected Integer rows;
  // protected Integer columns;
  protected int[][] values;

  public AMatrix(int rowCount, int columnCount) {
    if ((rowCount <= 0) | (columnCount <= 0)) {
      throw new NegativeArraySizeException(_ERR_MSG_1);
    }
    this.id = System.currentTimeMillis();
    // this.rows = 0;
    // this.columns = 0;
    InitializeMatrix(rowCount, columnCount, false);
  }

  // either not used
  public AMatrix() {
    this.id = System.currentTimeMillis();
  }

  /**
   * Initialized and filled matrix by random integer numbers
   */
  private void InitializeMatrix(int rowsCount, int columnsCount, boolean clear) {
    this.values = new int[rowsCount][columnsCount];
    Random random = new Random();

    for (int row = 0; row < values.length; ++row)
      for (int col = 0; col < values[row].length; ++col)
        if (clear)
          this.values[row][col] = 0;
        else
          this.values[row][col] = 1 + random.nextInt(99);
  }

  protected void ResizeMatrix(int rowsCount, int columnsCount) {
    if (this.values != null) {
      this.values = null;
    }
    // this.values = new int[rowsCount][columnsCount];
    InitializeMatrix(rowsCount, columnsCount, true);
  }

  /**
   * Default implementation of algorithm for calculate of two matrix
   * Have to be used for checking results of other implementations
   */
  public void Multiplication(AMatrix firstLeft, AMatrix secondRight) {
    if (firstLeft.getColumns() != secondRight.getRows()) {
      throw new ArrayIndexOutOfBoundsException(_ERR_MSG_2);
    }

    int rowCount = firstLeft.getRows();
    int colCount = secondRight.getColumns();
    int amountCount = secondRight.getRows();

    this.ResizeMatrix(rowCount, colCount);

    for (int row = 0; row < rowCount; ++row) {
      for (int col = 0; col < colCount; ++col) {
        int amount = 0;

        for (int i = 0; i < amountCount; ++i)
          amount += firstLeft.getValue(row, i) * secondRight.getValue(i, col);

        this.setValue(row, col, amount);
      }
    }
  }

  public abstract void MultiplicationConcurrent(AMatrix firstLeft, AMatrix secondRight, int type);

  /**
   * Вывод содержимого матрицы в поток вывода
   * Производится выравнивание значений для лучшего восприятия.
   *
   * @param  stream       Потоковый объект, представляющий собой файл для записи
   * @throws IOException
   */
  public void Output(Writer stream) throws IOException {
    boolean hasNegative = false;  // Признак наличия в матрице отрицательных чисел.
    int maxValue = 0;      // Максимальное по модулю число в матрице.

    // Цикл по строкам матрицы.
    for (final int[] row : this.getValues()) {
      // Цикл по столбцам матрицы.
      for (final int element : row) {
        int temp = element;

        // Вычисляем максимальное по модулю число в матрице и проверяем на наличие отрицательных чисел.
        if (element < 0) {
          hasNegative = true;
          temp = -temp;
        }
        if (temp > maxValue)
          maxValue = temp;
      }
    }

    // Вычисление длины позиции под число.
    int len = Integer.toString(maxValue).length() + 1;  // Одно знакоместо под разделитель (пробел).
    if (hasNegative)
      ++len;  // Если есть отрицательные, добавляем знакоместо под минус.

    // Вывод элементов матрицы в файл.
    for (final int[] row : this.getValues()) {  // Цикл по строкам матрицы.

      // Цикл по столбцам матрицы
      for (final int element : row)
        // Построение строки в формате вывода
        stream.write(String.format("%"+len+"d", element));

      stream.write("\n");  // Разделяем строки матрицы переводом строки.
    }
  }

  public void setValues(int[][] values) {
    this.values = values;
  }

  public int[][] getValues() {
    return this.values != null
            ? this.values
            : new int[0][0];
  }

  public Integer getRows() {
    // Число строк вычисляется исходя из матрицы объекта
    return (this.values != null)
      ? Integer.valueOf(this.values.length)
      : 0;
  }

  public Integer getColumns() {
    // Число стобцов вычисляется исходя из матрицы объекта
    return (this.values != null)
      ? Integer.valueOf(this.values[0].length)
      : 0;
  }

  public int getValue(int rowPosition, int columnPosition) {
    return this.values != null
            ? this.values[rowPosition][columnPosition]
            : 0;
  }

  public void setValue(int rowPosition, int columnPosition, int value) {
    if (this.values != null) {
      this.values[rowPosition][columnPosition] = value;
    }
  }

  public Long getId() {
    return id;
  }

  @Override
  public String toString() {
    return this.values != null
            ? "[" + String.valueOf(this.getRows()) + "]x[" + String.valueOf(this.getColumns()) + "]"
            : "Undefined"; // String.valueOf(this.id)
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 97 * hash + Objects.hashCode(this.id);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final AMatrix other = (AMatrix) obj;
    return Objects.equals(this.id, other.id);
  }
}