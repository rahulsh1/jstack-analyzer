/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack.util;

import net.jstack.api.LineUnparseableException;
import net.jstack.api.StackLine;
import net.jstack.api.ThreadData;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

public class ThreadDump {

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final DateFormat DATE_FORMAT_FILE = new SimpleDateFormat("yyyyMMddHHmmss");
  private final Path file;
  private boolean isFullThreadDump;
  private List<ThreadData> threadDataList;
  private long timestamp;

  public ThreadDump(Path pathToFile) throws Exception {
    file = pathToFile;
    threadDataList = new ArrayList<>();
    processDump();
  }

  public boolean isFullThreadDump() {
    return isFullThreadDump;
  }

  public List<ThreadData> getThreadData() {
    return threadDataList;
  }

  public long getTimestamp() {
    return timestamp;
  }

  private void processDump() throws Exception {
    int lineCount = 0;
    List<String> threadBuffer = new ArrayList<>();
    for (String line : Files.readAllLines(file)) {
      lineCount++;
      if (lineCount < 3) {
        if (lineCount == 1) {
          parseTimestamp(line);
        }
        if (line.startsWith("Full thread dump")) {
          isFullThreadDump = true;
        }
      } else {
        if (line.trim().length() == 0) {
          process(threadBuffer);
          threadBuffer.clear();
        } else {
          threadBuffer.add(line);
        }
      }
    }
  }

  /**
   * Process the thread buffer.
   * For Now, ignore 1 liner thread lines
   *
   * @param threadBuffer
   */
  private void process(List<String> threadBuffer) {
    if (threadBuffer.size() > 1) {
      ThreadData td = StackParserUtils.parseThreadLineOne(threadBuffer.get(0));
      threadDataList.add(td);
      StackParserUtils.parseThreadLineTwo(threadBuffer.get(1), td);

      if (!threadStackFilter().test(threadBuffer)) {
        List<StackLine> tStack = new ArrayList<>();
        for (int i = 2; i < threadBuffer.size(); i++) {
          try {
            tStack.add(0, new StackLine(threadBuffer.get(i)));
          } catch (LineUnparseableException ex) {
          }
        }
        td.setStackData(tStack);
      }
    }
  }

  /**
   * Parse first line 2018-06-21 00:44:38
   * @param line
   */
  private void parseTimestamp(String line) throws ParseException {
    try {
      Date date = DATE_FORMAT.parse(line.trim());
      timestamp = date.getTime();
    } catch (ParseException e) {
      String filename = file.getFileName().toString();
      String dt = filename.substring(0, filename.indexOf('.'));
      timestamp = DATE_FORMAT_FILE.parse(dt).getTime();
    }
  }

  /**
   * Add filtering conditions here that we want to ignore
   * e.g: if stack contains epollWait - ignore it
   * @return true for cases that should be ignored.
   */
  public static Predicate<List<String>> threadStackFilter() {
    return p -> {
      if (p.size() < 3) return false;
      String line = p.get(2);
      return (line.contains("epollWait") ||
          line.contains("ServerSocketChannelImpl.accept0") ||
          line.contains("socketAccept") ||
          line.contains("socketRead0"));
    };
  }
}
