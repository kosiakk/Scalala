/*
 * Distributed as part of Scalala, a linear algebra library.
 * 
 * Copyright (C) 2008- Daniel Ramage
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110 USA 
 */
package scalala;
package tensor;

import collection.{MergeableSet, IntSpanSet, ProductSet};
import tensor.operators.TensorShapes._;
import tensor.operators.TensorSelfOp;

/**
 * A standard numerical Tensor2 defined over (0,0) inclusive to 
 * (rows,cols) exclusive.
 * 
 * @author dramage
 */
trait Matrix extends Tensor2[Int,Int] with TensorSelfOp[(Int,Int),Matrix,Shape2] {

  /** The number of rows in this matrix. */
  def rows : Int;
  
  /** The number of columns in this matrix. */
  def cols : Int;
  
  @inline final protected def check(row : Int, col : Int) = {
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
      throw new IndexOutOfBoundsException(
        "Matrix out of bounds: "+(row,col)+" size was "+size)
    }
  }
  
  /** Returns (rows,cols). */
  @inline final def dimensions = (rows, cols);
  
  /** Returns the number of entries */
  @inline final override def size = domain.size;

  private val _domain = ProductSet(IntSpanSet(0, rows), IntSpanSet(0, cols));
  final override def domain = _domain;
  
  override def transpose : Matrix = new {
    val inner : Matrix = Matrix.this;
  } with Matrix.Transpose;
  
  override def getRow(row : Int) = new Vector {
    override def size = cols;
    override def apply(i : Int) = Matrix.this.apply(row,i);
    override def update(i : Int, value : Double) = Matrix.this.update(row,i,value);
    override def activeDomain = Matrix.this.activeDomainInRow(row);
    def like = Matrix.this.vectorLike(this);
    def vectorLike(size:Int) = Matrix.this.vectorLike(size);
    def matrixLike(rows:Int,cols:Int) = Matrix.this.matrixLike(rows,cols);
  }
  
  override def getCol(col : Int) = new Vector {
    override def size = rows;
    override def apply(i : Int) = Matrix.this.apply(i,col);
    override def update(i : Int, value : Double) = Matrix.this.update(i,col,value);
    override def activeDomain = Matrix.this.activeDomainInCol(col);
    def like = Matrix.this.vectorLike(this);
    def vectorLike(size:Int) = Matrix.this.vectorLike(size);
    def matrixLike(rows:Int,cols:Int) = Matrix.this.matrixLike(rows,cols);
  }

  /**
  * Creates a vector "like" this matrix, with the dimensionality of the provided vector
  */
  def vectorLike(v: Vector): Vector = vectorLike(v.size);

  /**
  * Creates a vector "like" this matrix, with the dimensionality provided 
  */
  def vectorLike(sz: Int): Vector;

  /**
  * Creates a matrix "like" this matrix, with the dimensionality provided
  */
  def matrixLike(rows: Int, cols: Int): Matrix;
  
  /*
  def apply(select : (Int => (Int,Int))) : Vector = {
    select(-1) match {
      case (-1,-1)  => throw new IllegalArgumentException("Index of out range");
      case (-1,col) => getCol(col);
      case (row,-1) => getRow(row);
      case _        => throw new IllegalArgumentException("Invalid index selector");
    }
  }
  */
  
}

object Matrix {
  trait Transpose extends Matrix {
    type I1 = Int;
    type I2 = Int;
    
    val inner : Matrix;
    
    override def rows = inner.cols;
    override def cols = inner.rows;
    
    override def apply(row : I2, col : I1) =
      inner.apply(col, row);
      
    override def update(row : I2, col : I1, value : Double) =
      inner.update(col, row, value);
    
    override def copy =
      inner.copy.asInstanceOf[Matrix].transpose;

    def like = inner.like.transpose;
    def matrixLike(rows: Int, cols: Int) = inner.matrixLike(rows,cols);
    def vectorLike(size: Int) = inner.vectorLike(size);
    
    override def activeDomain : MergeableSet[(I2,I1)] = {
      new MergeableSet[(I2,I1)] {
        override def size = inner.activeDomain.size;
        override def iterator = inner.activeDomain.iterator.map(tup => (tup._2,tup._1));
        override def contains(i : (I2,I1)) = inner.activeDomain.contains((i._2,i._1));
      };
    }
    
    override def transpose = inner;
    
    override def getRow(col : I2) =
      inner.getCol(col);
    
    override def getCol(row : I1) =
      inner.getRow(row);
    
    override def activeDomainInRow(col : I2) =
      inner.activeDomainInCol(col);
    
    override def activeDomainInCol(row : I1) =
      inner.activeDomainInRow(row);
  }
}
