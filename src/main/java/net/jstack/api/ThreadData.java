/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack.api;

import java.util.List;

/**
 * Represents info for a single thread in a thread dump along with its stack (@link StackLine).
 */
public class ThreadData {

  private String name;
  private Thread.State state;
  private int osPriority;
  private String tid;
  private String nid;
  // Optional params
  private boolean isDaemon;
  private int priority;
  private Integer threadNumber;

  private List<StackLine> stackData;

  public ThreadData(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Thread.State getState() {
    return state;
  }

  public void setState(Thread.State state) {
    this.state = state;
  }

  public int getOsPriority() {
    return osPriority;
  }

  public void setOsPriority(int osPriority) {
    this.osPriority = osPriority;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public String getTid() {
    return tid;
  }

  public void setTid(String tid) {
    this.tid = tid;
  }

  public String getNid() {
    return nid;
  }

  public void setNid(String nid) {
    this.nid = nid;
  }

  public Integer getThreadNumber() {
    return threadNumber;
  }

  public void setThreadNumber(Integer threadNumber) {
    this.threadNumber = threadNumber;
  }

  public boolean isDaemon() {
    return isDaemon;
  }

  public void setDaemon(boolean daemon) {
    isDaemon = daemon;
  }

  public List<StackLine> getStackData() {
    return stackData;
  }

  public void setStackData(List<StackLine> stackData) {
    this.stackData = stackData;
  }

  public boolean hasStacks() {
    return stackData != null && !stackData.isEmpty();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ThreadData {");
    sb.append("name='").append(name).append('\'');
    sb.append(", state=").append(state);
    sb.append(", osPriority=").append(osPriority);
    sb.append(", priority=").append(priority);
    sb.append(", tid='").append(tid).append('\'');
    sb.append(", nid='").append(nid).append('\'');
    sb.append(", isDaemon=").append(isDaemon);
    sb.append(", threadNumber=").append(threadNumber);
    if (hasStacks()) {
      sb.append(", \nstackData=\n");
      for (StackLine line : stackData) {
        sb.append("    ").append(line).append('\n');
      }
    }
    sb.append('}');
    sb.append('\n');
    return sb.toString();
  }
}
