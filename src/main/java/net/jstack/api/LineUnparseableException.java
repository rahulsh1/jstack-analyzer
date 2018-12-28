/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack.api;

public class LineUnparseableException extends Exception {

  public LineUnparseableException() {
    super();
  }

  public LineUnparseableException(String message) {
    super(message);
  }

  public LineUnparseableException(String message, Throwable cause) {
    super(message, cause);
  }

}
