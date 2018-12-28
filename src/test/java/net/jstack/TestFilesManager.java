/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack;

import net.jstack.util.FilesManager;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestFilesManager {

  @Test
  public void testFM() {
    FilesManager fm = new FilesManager();
    fm.addTimestamps(1528169988000L, 1528169989000L, 1528169983000L, 1528169985000L);
    assertTrue(fm.isAdjacent(1528169988000L, 1528169989000L));
    assertTrue(fm.isAdjacent(1528169983000L, 1528169985000L));
    assertFalse(fm.isAdjacent(1528169983000L, 1528169988000L));
  }

  private Path getResourcesPath() {
    ClassLoader classLoader = getClass().getClassLoader();
    URL inputUrl = classLoader.getResource("20180604203941-74824");
    if (inputUrl != null) {
      String inputResourceLocation = inputUrl.getFile();
      return Paths.get(inputResourceLocation);
    }
    return null;
  }
}
