/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack.util;

import net.jstack.api.ThreadData;

import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Utility class for parsing stack.
 */
public final class StackParserUtils {

  private static final DateFormat DATE_FORMAT_FILE = new SimpleDateFormat("yyyyMMddHHmmss");

  /**
   * generate thread info token for table view.
   * @param line the thread info.
   * @return thread tokens.
   */
  public static ThreadData parseThreadLineOne(String line) {
    ThreadData threadData = null;
    if (line.indexOf('"') >= 0) {
      threadData = new ThreadData(line.substring(1, line.lastIndexOf('"') == 0 ? line.length() - 1 : line.lastIndexOf('"')));
      threadData.setTid(getPart(line, "tid="));
      threadData.setNid(getPart(line, "nid="));
      threadData.setOsPriority(getIntPart(line, "os_prio=", false));
      if (line.indexOf("prio") > 0) {
        threadData.setDaemon(line.indexOf("daemon") > 0);
        threadData.setPriority(getIntPart(line, "prio=", false));
      }
      if (line.indexOf("#") > 0) {
        threadData.setThreadNumber(getIntPart(line, "#", true));
      }
    }
    return threadData;
  }

  public static void parseThreadLineTwo(String line, ThreadData threadData) {
    if (line.contains("java.lang.Thread.State")) {
      threadData.setState(fromState(getPart(line, "java.lang.Thread.State: ")));
    }
  }

  public static long parseFileTimestamp(Path file) {
    long timestamp = 0;
    try {
      String filename = file.getFileName().toString();
      int idx = filename.indexOf('.');
      if (idx > 0) {
        String dt = filename.substring(0, filename.indexOf('.'));
        timestamp = DATE_FORMAT_FILE.parse(dt).getTime();
      }
    } catch (ParseException e) {
    }
    return timestamp;
  }

  private static String getPart(String line, String startToken) {
    int startIdx = line.indexOf(startToken) + startToken.length();
    int endIdx = line.indexOf(' ', startIdx);
    if (endIdx == -1) {
      endIdx = line.length();
    }
    return line.substring(startIdx, endIdx);
  }

  private static Integer getIntPart(String line, String startToken, boolean isLast) {
    int startIdx = (isLast ? line.lastIndexOf(startToken) : line.indexOf(startToken)) + startToken.length();
    int endIdx = line.indexOf(" ", startIdx);
    String val = line.substring(startIdx, endIdx);
    if (val.length() > 0) {
      try {
        return Integer.parseInt(val);
      } catch (NumberFormatException ne) {
        System.out.println("In line " + line + " startToken: " + startToken);
        throw ne;
      }
    } else {
      System.out.println("Searching for " + startToken + " in " + line);
      return 0;
    }
  }

  private static Thread.State fromState(String state) {
    for (Thread.State e : Thread.State.values()) {
      if (e.name().equalsIgnoreCase(state)) {
        return e;
      }
    }
    System.out.println("Unable to decode thread state: " + state);
    return Thread.State.TERMINATED;
  }
}
