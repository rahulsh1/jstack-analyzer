/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack;

import net.jstack.api.StackLine;
import net.jstack.util.FilesManager;
import net.jstack.util.ThreadDump;
import net.jstack.util.ThreadTrie;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * Reads multiple stack files and populates the Trie for Threads.
 */
final class ThreadManager {

  private final ThreadTrie rootTrie;
  private final FilesManager filesManager;

  ThreadManager(FilesManager filesMgr) {
    rootTrie = new ThreadTrie();
    // Set the root class:method to "main"
    rootTrie.setFileData("main", "");
    filesManager = filesMgr;
  }

  void processThreadStacks(Path dirName) throws IOException {
    Files.walk(dirName).filter(Files::isRegularFile).filter(e -> e.toString().endsWith("jstack")).forEach((Path e) -> {
      try {
        ThreadDump td = new ThreadDump(e);
        filesManager.addTimestamp(td.getTimestamp());
        addToTrie(td);
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    });

    collapseTimeslots(rootTrie);
    rootTrie.computeOneLevelCount();
  }

  ThreadDumpFormatter.MetricFormatter getFormatter(ThreadDumpFormatter.OutputType type) {
    return type.getFormatter(rootTrie);
  }

  public String toString() {
    return rootTrie.stringForm();
  }

  ThreadTrie getRoot() {
    return rootTrie;
  }

  private void addToTrie(ThreadDump td) {
    td.getThreadData().stream().filter(e -> e.getState() == Thread.State.RUNNABLE && e.hasStacks()).forEach(th -> {
      ThreadTrie trieNode = rootTrie;
      for (StackLine sl : th.getStackData()) {
        trieNode = trieNode.getChildren().computeIfAbsent(sl.getKey(), k -> {
          ThreadTrie v = new ThreadTrie();
          v.setFileData(sl.getMethod(), sl.getFile() + ":" + sl.getLineNumber());
          return v;
        });
        trieNode.setTimestamp(td.getTimestamp());
      }
    });
  }

  private void collapseTimeslots(ThreadTrie trieNode) {
    SortedSet<ThreadTrie.TimeSlot> slots = trieNode.getTimeSlots();
    if (slots.size() > 0) {
      collapseSlot(slots);
    }
    trieNode.getChildren().forEach((k, v) -> {
      collapseTimeslots(v);
    });
  }

  private void collapseSlot(SortedSet<ThreadTrie.TimeSlot> slots) {
    Iterator<ThreadTrie.TimeSlot> iterator = slots.iterator();
    ThreadTrie.TimeSlot startSlot = null;
    ThreadTrie.TimeSlot currSlot;
    while (iterator.hasNext()) {
      currSlot = iterator.next();
      if (startSlot == null) {
        startSlot = currSlot;
      } else {
        long startTs = startSlot.getEnd() > 0 ? startSlot.getEnd() : startSlot.getStart();
        if (filesManager.isAdjacent(startTs, currSlot.getStart())) {
          startSlot.setEnd(currSlot.getStart());
          iterator.remove();
        } else {
          if (startSlot.getEnd() == 0) {
            startSlot.setEnd(startSlot.getStart());
          }
          startSlot = currSlot;
        }
      }
    }
    if (startSlot != null && startSlot.getEnd() == 0) {
      startSlot.setEnd(startSlot.getStart());
    }
  }

}
