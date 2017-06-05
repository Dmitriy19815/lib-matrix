/**
 * Unit cases tests to show how is Matrix utilities works.
 *
 * @author Dmytro Farafonov
 */
package pro.codecare.lib;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;

public class EntityTest {

  public EntityTest() {

  }

  @Test
  public void testConstructorDefault() {
    Matrix matrix = new Matrix();
    Assert.assertNotNull(matrix);
    System.out.println("Default size matrix is "+matrix.toString());
  }

  @Test
  public void testConstructor() {
    Matrix matrix = new Matrix(9,9);
    Assert.assertNotNull(matrix);
    System.out.println("Defined size 9x9 matrix is "+matrix.toString());
  }

  @Test
  public void testCalculatorOne() {
    Matrix matrix = new Matrix();
    Assert.assertNotNull(matrix);
    matrix.Multiplication(new Matrix(7, 10), new Matrix(10, 8));
    Assert.assertEquals((int) matrix.getRows(), 7);
    Assert.assertEquals((int) matrix.getColumns(), 8);
    System.out.println("Default (with no concurrent) calculator for matrix "+matrix.toString()+" is works");
  }

  @Test
  public void testCalculatorTwo() {
    Matrix matrix = new Matrix();
    Assert.assertNotNull(matrix);
    matrix.MultiplicationConcurrent(new Matrix(550, 100), new Matrix(100, 500), 0);
    Assert.assertEquals((int) matrix.getRows(), 550);
    Assert.assertEquals((int) matrix.getColumns(), 500);
    System.out.println("Multithread sync calculator for matrix "+matrix.toString()+" is works");
  }

  @Test
  public void testCalculatorThree() {
    Matrix matrix = new Matrix();
    Assert.assertNotNull(matrix);
    matrix.MultiplicationConcurrent(new Matrix(550, 100), new Matrix(100, 500), 1);
    Assert.assertEquals((int) matrix.getRows(), 550);
    Assert.assertEquals((int) matrix.getColumns(), 500);
    System.out.println("Multithread async calculator matrix "+matrix.toString()+" is works");
  }

  @Test
  public void testOutput() {
    Matrix matrix = new Matrix();
    Assert.assertNotNull(matrix);

    try (final FileWriter fileWriter = new FileWriter("testOutput "+matrix.toString()+".log", false)) {
      fileWriter.write("\tDefault constructor matrix:\n");
      matrix.Output(fileWriter);
      System.out.println("Default matrix "+matrix.toString()+" is flushed to file stream");
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

}
