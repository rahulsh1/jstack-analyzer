/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack;

import net.jstack.util.ThreadDump;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestThreadDump {

  @Test
  public void testAllFiles() throws IOException {
    Files.walk(getResourcesPath()).filter(Files::isRegularFile).filter(e -> e.toString().endsWith("jstack")).forEach((Path e) -> {
      try {
        validateStack(e.getFileName().toString(), 0, false);
      } catch (Exception e1) {
        System.out.println("Failed to parse file: " + e.toString());
        fail("Got" + e1);
      }
    });
  }

  @Test
  public void testParseFullThreadDump() throws Exception {
    validateStack("20180707202828.17588.jstack", 38, false);
    validateStack("20180707202835.17588.jstack", 38, false);
  }

  @Test
  public void testThreadFilter() {
    List<String> input = Arrays.asList("one", "two", "sun.nio.ch.EPollArrayWrapper.epollWait");
    assertTrue(ThreadDump.threadStackFilter().test(input));

    input = Arrays.asList("one", "two", "sun.nio.ch.ServerSocketChannelImpl.accept0");
    assertTrue(ThreadDump.threadStackFilter().test(input));

    input = Arrays.asList("one", "two", "java.net.PlainSocketImpl.socketAccept");
    assertTrue(ThreadDump.threadStackFilter().test(input));

    input = Arrays.asList("one", "two", "sun.nio.ch.EPollArrayWrapper.socketRead0");
    assertTrue(ThreadDump.threadStackFilter().test(input));

    input = Arrays.asList("one", "two", "java.net.AbstractPlainSocketImpl.accept");
    assertFalse(ThreadDump.threadStackFilter().test(input));

  }

  public void validateStack(String file, int sampleCount, boolean dump) throws Exception {
    ThreadDump td = new ThreadDump(getResourcesPath().resolve(file));

    if (sampleCount != 0) {
      assertTrue(td.isFullThreadDump());
      assertEquals(sampleCount, td.getThreadData().size());
    }
    if (dump) {
      System.out.println("Timestamp: " + new Date(td.getTimestamp()));
      //td.getThreadData().forEach(System.out::println);
    }
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
