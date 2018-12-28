/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Keeps a list of all file timestamps to decide if file timestamps are adjacent.
 * This is done to account for missing jstack files.
 */
public class FilesManager {

  private List<Long> fileTimestamps;

  public FilesManager() {
    fileTimestamps = new ArrayList<>();
  }

  public void addTimestamp(long timestamp) {
    fileTimestamps.add(timestamp);
    Collections.sort(fileTimestamps);
  }

  public void addTimestamps(Long ... tss) {
    fileTimestamps.addAll(Arrays.asList(tss));
    Collections.sort(fileTimestamps);
  }

  public boolean isAdjacent(long t1, long t2) {
    int i1 = fileTimestamps.indexOf(t1);
    int i2 = fileTimestamps.indexOf(t2);
    return i1 != i2 && Math.abs(i2 - i1) == 1;
  }

}
