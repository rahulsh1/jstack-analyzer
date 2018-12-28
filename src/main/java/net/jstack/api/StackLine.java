/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack.api;

/**
 * Represents a stack line.
 * Currently we ignore lines not starting with "at". e.g:
 * - locked <0x0000000681ae1180> (a java.lang.Object)
 */
public class StackLine {

  private String file;
  private String lineNumber;
  private String method;

  public StackLine(String line) throws LineUnparseableException {
    parse(line);
  }

  private void parse(String line) throws LineUnparseableException {
    String ln = line.trim();
    if (ln.startsWith("at")) {
      int bIdx = ln.indexOf('(');
      if (bIdx > 0) {
        method = ln.substring(3, bIdx);
        int mIdx = ln.indexOf(':', bIdx);
        if (mIdx < 0) {
          file = ln.substring(bIdx + 1, ln.indexOf(')', mIdx));
          lineNumber = "";
        } else {
          file = ln.substring(bIdx + 1, mIdx);
          lineNumber = ln.substring(mIdx + 1, ln.indexOf(')', mIdx));
        }
      } else {
        throw new LineUnparseableException(line);
      }
    } else {
      throw new LineUnparseableException(line);
    }
  }

  public String getFile() {
    return file;
  }

  public String getLineNumber() {
    return lineNumber;
  }

  public String getMethod() {
    return method;
  }

  public String getKey() {
    return file + ":" + lineNumber + ":" + method;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("");
    sb.append(String.format("%40s:%-5s - %s", file, lineNumber, method));
    return sb.toString();
  }
}
