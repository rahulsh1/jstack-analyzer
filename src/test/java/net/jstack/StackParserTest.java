/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack;

import net.jstack.api.ThreadData;
import net.jstack.util.StackParserUtils;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class StackParserTest {

  @Test
  public void testLineOneTwo() {
    StackParserUtils sp = new StackParserUtils();
    ThreadData td = sp.parseThreadLineOne("\"Jetty HTTP Service\" #46 z=c prio=5 os_prio=1 tid=0x00007fb74079fbe0 nid=0x124cf runnable [0x00007fb6fb2eb000]");
    assertNotNull(td);

    assertEquals(td.getName(), "Jetty HTTP Service");
    assertFalse(td.isDaemon());
    assertEquals(5, td.getPriority());
    assertEquals(1, td.getOsPriority());
    assertEquals("0x00007fb74079fbe0", td.getTid());
    assertEquals("0x124cf", td.getNid());

    sp.parseThreadLineTwo("   java.lang.Thread.State: RUNNABLE", td);
    assertEquals(Thread.State.RUNNABLE, td.getState());
    sp.parseThreadLineTwo("   java.lang.Thread.State: WAITING (on object monitor)", td);
    assertEquals(Thread.State.WAITING, td.getState());
    System.out.println(td);
  }

  @Test
  public void testTimeStamp() {
    StackParserUtils sp = new StackParserUtils();
    long t1 = sp.parseFileTimestamp(Paths.get("20180614174303.jstack"));
    assertEquals(1529023383000L, t1);
    t1 = sp.parseFileTimestamp(Paths.get("20180614174029.jstack"));
    assertEquals(1529023229000L, t1);
    t1 = sp.parseFileTimestamp(Paths.get("20180630004303.jstack"));
    assertEquals(1530344583000L, t1);

    // Unparseable
    t1 = sp.parseFileTimestamp(Paths.get("ss180630004303.jstack"));
    assertEquals(0L, t1);
    t1 = sp.parseFileTimestamp(Paths.get(""));
    assertEquals(0L, t1);
  }
}
