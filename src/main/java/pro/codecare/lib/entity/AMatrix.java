package pro.codecare.lib.entity;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Objects;
import java.util.Random;

/**
 * Abstract parent class with simple Matrix utilities for calculation of multiplication for two matrix.
 * Works properly for rectangular matrix.
 */
public abstract class AMatrix implements Serializable {
  private static final long serialVersionUID = 1L;
  protected static final int bound = 99;
  protected static String _ERR_MSG_1 = "You have to pass sizes of matrix like positive values";
  protected static String _ERR_MSG_2 = "The number of columns in the first matrix and the number of rows in the second matrix must coincide";
  protected Long id;
  protected int[][] values;

  /**
   * Constructor method of matrix object with with defined dimensions
   * Also fills the new matrix by random integer numbers
   *
   * @param rowCount
   * @param columnCount
   */
  public AMatrix(int rowCount, int columnCount) {
    if ((rowCount <= 0) | (columnCount <= 0)) {
      throw new NegativeArraySizeException(_ERR_MSG_1);
    }
    this.id = System.currentTimeMillis();
    InitializeMatrix(rowCount, columnCount, false);
  }

  // either not used
  public AMatrix() {
    this.id = System.currentTimeMillis();
  }

  /**
   * Makes initialize of matrix with defined dimensions
   * Also fills the new matrix by integer numbers depend on value of flag
   *
   * @param rowsCount
   * @param columnsCount
   * @param clear
   */
  private void InitializeMatrix(int rowsCount, int columnsCount, boolean clear) {
    this.values = new int[rowsCount][columnsCount];
    Random random = new Random();
    for (int row = 0; row < values.length; ++row)
      for (int col = 0; col < values[row].length; ++col)
        if (clear)
          this.values[row][col] = 0;
        else
          this.values[row][col] = 1 + random.nextInt(bound);
  }

  /**
   * Makes resize of matrix with defined dimensions
   * Also fills the new matrix by zero integer numbers
   *
   * @param rowsCount
   * @param columnsCount
   */
  protected void ResizeMatrix(int rowsCount, int columnsCount) {
    if (this.values != null)
      this.values = null;
    InitializeMatrix(rowsCount, columnsCount, true);
  }

  /**
   * Default implementation of algorithm for calculate of two matrix.
   * Have to be used for checking results of other implementations
   *
   * @param firstLeft     First source matrix object
   * @param secondRight   Second source matrix object
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

  /**
   * Abstract method for the concurrent implementation in inherits
   *
   * @param firstLeft     First source matrix object
   * @param secondRight   Second source matrix object
   */
  public abstract void MultiplicationConcurrent(AMatrix firstLeft, AMatrix secondRight, int type);

  /**
   * Output of matrix content to the output stream;
   * Aligns the values for better perception.
   *
   * @param  stream       Object (file etc.) for output
   * @throws IOException
   */
  public void Output(Writer stream) throws IOException {
    boolean hasNegative = false;
    int maxValue = 0;

    // Checking values before output
    for (final int[] row : this.getValues()) {
      for (final int item : row) {
        int temp = item;

        if (item < 0) {
          hasNegative = true;
          temp = -temp;
        }
        if (temp > maxValue)
          maxValue = temp;
      }
    }

    // Prepare to output
    int len = Integer.toString(maxValue).length() + 1;
    if (hasNegative)
      ++len;

    // Output matrix to stream
    for (final int[] row : this.getValues()) {
      for (final int item : row)
        // builds pretty format output String for matrix element
        stream.write(String.format("%"+len+"d", item));

      // add empty line separator
      stream.write("\n");
    }
  }

  public void setValues(int[][] values) {
    this.values = values;
  }

  /**
   * The actual matrix values in 2D-array format
   *
   * @return Actual matrix values 2D-array or empty array
   *          in case of empty matrix object
   */
  public int[][] getValues() {
    return this.values != null
            ? this.values
            : new int[0][0];
  }

  /**
   * The number of rows is calculated from the actual matrix of the object
   *
   * @return Actual number or zero number in case of empty matrix object
   */
  public Integer getRows() {
    return (this.values != null)
      ? Integer.valueOf(this.values.length)
      : 0;
  }

  /**
   * The number of columns is calculated from the actual matrix of the object
   *
   * @return Actual number or zero number in case of empty matrix object
   */
  public Integer getColumns() {
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