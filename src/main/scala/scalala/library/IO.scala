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
package library;

import java.io.File;

import tensor.Matrix;
import tensor.dense.DenseMatrix;

/**
 * Input and output routines.
 * 
 * @author dramage
 */
trait IO extends Library {
  
  def dlmread(file : String) : Matrix = {
    def lines = scala.io.Source.fromFile(new File(file)).getLines();

    val numCols = lines.next.trim.split("\\s+").length;
    val numRows = lines.map(_ => 1).reduceLeft(_+_);
    
    val m = new DenseMatrix(numRows, numCols);
    
    var i = 0;
    for (line <- lines) {
      var j = 0;
      for (entry <- line.trim.split("\\s+").map(_.toDouble)) {
        m(i,j) = entry;
        j += 1;
      }
      i += 1;
    }
    
    return m;
  }
}

/**
 * An object with access to the IO trait members.
 * 
 * @author dramage
 */
object IO extends IO { }
