/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FilesManagerTest {

  @Test
  public void testFM() {
    FilesManager fm = new FilesManager();
    fm.addTimestamps(1528169988000L, 1528169989000L, 1528169983000L, 1528169985000L);
    assertTrue(fm.isAdjacent(1528169988000L, 1528169989000L));
    assertTrue(fm.isAdjacent(1528169983000L, 1528169985000L));
    assertFalse(fm.isAdjacent(1528169983000L, 1528169988000L));
  }
}
