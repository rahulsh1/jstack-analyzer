/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack.api;

import net.jstack.api.LineUnparseableException;
import net.jstack.api.StackLine;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StackLineTest {

  @Test
  public void testLine() throws LineUnparseableException {
    StackLine sL = new StackLine("    at java.lang.Thread.run(Thread.java:748)");
    assertEquals("java.lang.Thread.run", sL.getMethod());
    assertEquals("Thread.java", sL.getFile());
    assertEquals("748", sL.getLineNumber());
  }

  @Test
  public void testLine2() throws LineUnparseableException {
    StackLine sL = new StackLine("\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)");
    assertEquals("java.util.concurrent.ThreadPoolExecutor$Worker.run", sL.getMethod());
    assertEquals("ThreadPoolExecutor.java", sL.getFile());
    assertEquals("624", sL.getLineNumber());
  }

  @Test
  public void testLine3() throws LineUnparseableException {
    StackLine sL = new StackLine("    \tat java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)");
    assertEquals("java.util.concurrent.SynchronousQueue.poll", sL.getMethod());
    assertEquals("SynchronousQueue.java", sL.getFile());
    assertEquals("941", sL.getLineNumber());
  }

  @Test
  public void testLine4() throws LineUnparseableException {
    StackLine sL = new StackLine("    \tat sun.misc.Unsafe.park(Native Method)");
    assertEquals("sun.misc.Unsafe.park", sL.getMethod());
    assertEquals("Native Method", sL.getFile());
    assertEquals("", sL.getLineNumber());
  }

  @Test(expected = LineUnparseableException.class)
  public void testLine5() throws LineUnparseableException {
    new StackLine("- parking to wait for  <0x000000067633d1c0> (a java.util.concurrent.SynchronousQueue$TransferStack)");
  }
}
