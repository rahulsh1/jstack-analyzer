/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack;

import net.jstack.util.FilesManager;
import net.jstack.util.ThreadTrie;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ThreadManagerTest {

  @Test
  public void testTreeOutput() throws IOException {
    ThreadManager threadManager = new ThreadManager(new FilesManager());
    threadManager.processThreadStacks(getResourcesPath());

    Map<String, ThreadTrie> map = threadManager.getRoot().getChildren();
    assertEquals(1, map.size());

    assertEquals("Tree output differs",
      new String(Files.readAllBytes(getResourcesPath().resolve("thread_tree.txt"))),
      threadManager.toString());
  }

  private Path getResourcesPath() {
    ClassLoader classLoader = getClass().getClassLoader();
    URL inputUrl = classLoader.getResource("sample");
    if (inputUrl != null) {
      String inputResourceLocation = inputUrl.getFile();
      return Paths.get(inputResourceLocation);
    }
    return null;
  }
}
