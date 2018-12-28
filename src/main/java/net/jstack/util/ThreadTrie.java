/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack.util;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public final class ThreadTrie {

  /**
   * Comparator to sort based on start timestamp.
   */
  public static class TimeSlot implements Comparable<TimeSlot> {
    long start;
    long end;

    TimeSlot(long st) {
      start = st;
    }

    @Override
    public int compareTo(TimeSlot o) {
      return Long.compare(start, o.start);
    }

    public long getStart() {
      return start;
    }

    public long getEnd() {
      return end;
    }

    public void setEnd(long end) {
      this.end = end;
    }
  }

  private int samples;
  private SortedSet<TimeSlot> timeSlots;
  // Holds the Class.classMethod  e.g. java.lang.Thread.start0
  private String classMethod;
  // Holds data such in form <File:line> e.g. ForkJoinPool.java:868
  private String fileLineNum;
  private Map<String, ThreadTrie> children;

  public ThreadTrie() {
    children = new HashMap<>(0, 1);
    timeSlots = new TreeSet<>();
  }

  public Map<String, ThreadTrie> getChildren() {
    return children;
  }

  public void setFileData(String value, String value2) {
    this.classMethod = value;
    this.fileLineNum = value2;
  }

  public void setTimestamp(long timestamp) {
    samples++;
    timeSlots.add(new TimeSlot(timestamp));
  }

  public String getClassMethod() {
    return classMethod;
  }

  public String getfileLineNum() {
    return fileLineNum;
  }

  public SortedSet<TimeSlot> getTimeSlots() {
    return timeSlots;
  }

  public int getSamples() {
    return samples;
  }

  public void computeOneLevelCount() {
    int currCount[] = new int[1];
    children.forEach((k, v) -> {
      currCount[0] += v.samples;
    });
    samples = currCount[0];
  }

  public String stringForm() {
    StringBuilder sb = new StringBuilder();
    toString(sb, this, 1);
    return sb.toString();
  }

  public void toString(StringBuilder sb, ThreadTrie node, int numIndent) {
    if (node == null) {
      return;
    }
    for (int i = 0; i < numIndent * 2 - 3; i++) {
      sb.append("|-");
    }
    sb.append(shortenPackage(node.getClassMethod())).append(":").append(getLineNumber(node.getfileLineNum())).append(" - ")
        .append(node.computeTotalTime()).append(" sec(# ").append(node.getSamples()).append(')').append('\n');

    node.getChildren().forEach((k, v) -> {
      toString(sb, v, numIndent+1);
    });
  }

  public void toHtml(StringBuilder sb, ThreadTrie node, int numIndent, int totalSamples) {
    if (node != null) {
      sb.append("<li>\n");
      sb.append("<div>").append(" [").append(numIndent).append("] ")
          .append(getPercent(node.getSamples(), totalSamples))
          .append("% ").append(node.getSamples()).append("</div>\n");
      if (node.getfileLineNum().contains("Native Method")) {
        sb.append("<span class=\"red\">");
      } else {
        sb.append("<span class=\"green\">");
      }
      sb.append(shortenPackage(node.getClassMethod())).append("</span>\n");
      if (node.getChildren().size() > 0) {
        sb.append("<ul>\n");
      }
      node.getChildren().forEach((k, v) -> {
        toHtml(sb, v, numIndent+1, totalSamples);
      });
      if (node.getChildren().size() > 0) {
        sb.append("</ul>\n");
      }
      sb.append("</li>\n");
    }
  }

  private String getPercent(int samples, int total) {
    return String.format("%.2f", samples * 100.0/total );
  }

  private String getLineNumber(String data) {
    int idx = data.indexOf(":");
    if (idx > 0) {
      return ((idx + 1) < data.length()) ? data.substring(idx+1) : data;
    } else {
      return data;
    }
  }

  /**
   * Trims long package name to single letter names.
   *
   * @param data full package name
   * @return trimmed name
   */
  private String shortenPackage(String data) {
    String val[] = data.split("\\.");
    StringBuilder sb = new StringBuilder();
    if (val.length > 2) {
      int i = 0;
      for (i = 0; i < val.length - 2; i++) {
        sb.append(val[i].charAt(0)).append('.');
      }
      sb.append(val[i++]).append('.');
      sb.append(val[i]);
    } else {
      return data;
    }
    return sb.toString();
  }

  private int computeTotalTime() {
    int arr[] = new int[1];
    timeSlots.forEach(ts -> {
      arr[0] += (ts.end - ts.start)/1000L + 1;
    });
    return arr[0];
  }

}
