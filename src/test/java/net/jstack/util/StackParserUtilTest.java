/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack.util;

import net.jstack.api.ThreadData;
import org.junit.Test;

import static org.junit.Assert.*;

public class StackParserUtilTest {

  @Test
  public void testLineOneTwo() {
    ThreadData td = StackParserUtils.parseThreadLineOne("\"Jetty HTTP Service\" #46 z=c prio=5 os_prio=1 tid=0x00007fb74079fbe0 nid=0x124cf runnable [0x00007fb6fb2eb000]");
    assertNotNull(td);

    assertEquals(td.getName(), "Jetty HTTP Service");
    assertFalse(td.isDaemon());
    assertEquals(5, td.getPriority());
    assertEquals(1, td.getOsPriority());
    assertEquals("0x00007fb74079fbe0", td.getTid());
    assertEquals("0x124cf", td.getNid());

    StackParserUtils.parseThreadLineTwo("   java.lang.Thread.State: RUNNABLE", td);
    assertEquals(Thread.State.RUNNABLE, td.getState());
    StackParserUtils.parseThreadLineTwo("   java.lang.Thread.State: WAITING (on object monitor)", td);
    assertEquals(Thread.State.WAITING, td.getState());
    System.out.println(td);
  }
}
