/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack;

import net.jstack.util.FilesManager;
import net.jstack.util.StackParserUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main entry point.
 */
public class Main {

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("java Main <full_path_to_jstack_dir>");
      System.exit(1);
    }

    Path inputPath = Paths.get(args[0]);
    final ThreadManager threadManager = new ThreadManager(new FilesManager());
    try {
      threadManager.processThreadStacks(inputPath);
      // HTML form
      Path destHtmlPath = inputPath.resolve("thread-tree.htm");
      Files.write(destHtmlPath, threadManager.getFormatter(ThreadDumpFormatter.OutputType.HTML).getOutput().getBytes());
      System.out.println("Wrote to " + destHtmlPath);

      // Text form
      Path destStrPath = inputPath.resolve("thread-tree.txt");
      Files.write(destStrPath, threadManager.getFormatter(ThreadDumpFormatter.OutputType.STRING).getOutput().getBytes());
      System.out.println("Wrote to " + destStrPath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
