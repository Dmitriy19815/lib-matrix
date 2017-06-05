/*
 * Copyright 2017 Key Bridge. All rights reserved.
 * Use is subject to license terms.
 *
 * Software Code is protected by Copyrights. Author hereby reserves all rights
 * in and to Copyrights and no license is granted under Copyrights in this
 * Software License Agreement.
 *
 * Key Bridge generally licenses Copyrights for commercialization pursuant to
 * the terms of either a Standard Software Source Code License Agreement or a
 * Standard Product License Agreement. A copy of either Agreement can be
 * obtained upon request from: info@keybridgewireless.com
 */
package pro.codecare.lib;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Unit cases tests to show how Matrix utility logic works
 *
 * @author Dmytro Farafonov
 */
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
      fileWriter.write("\tDefault constuctor matrix:\n");
      matrix.Output(fileWriter);
      System.out.println("Default matrix "+matrix.toString()+" is flushed to file stream");
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

}