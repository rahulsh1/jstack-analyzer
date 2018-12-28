/*
 * Copyright (c) 2018, Rahul S. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found in the LICENSE file.
 */
package net.jstack;

import net.jstack.util.ThreadTrie;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class ThreadDumpFormatter {

  public enum OutputType {
    STRING {
      @Override
      public MetricFormatter getFormatter(ThreadTrie threadTrie) {
        return new TextFormatter(threadTrie);
      }
    },
    HTML {
      @Override
      public MetricFormatter getFormatter(ThreadTrie threadTrie) {
        return new HTMLFormatter(threadTrie);
      }
    };

    public abstract MetricFormatter getFormatter(ThreadTrie threadTrie);
  }

  /**
   * Interface to convert data to various formats.
   */
  interface MetricFormatter {
    String getOutput();
  }

  private static class HTMLFormatter implements MetricFormatter {

    private final ThreadTrie threadTrie;

    HTMLFormatter(ThreadTrie tt) {
      this.threadTrie = tt;
    }

    @Override
    public String getOutput() {
      StringBuilder sb = new StringBuilder();
      try {
        Path resourcePath = getResourcesPath();
        if (resourcePath != null) {
          // add header
          sb.append(new String(Files.readAllBytes(resourcePath.resolve("head.htm")), StandardCharsets.UTF_8));

          // add body
          sb.append("<ul class=\"tree\">");
          threadTrie.toHtml(sb, threadTrie, 1, threadTrie.getSamples());
          // closing ul is part of footer
          // add footer
          sb.append(new String(Files.readAllBytes(resourcePath.resolve("footer.htm")), StandardCharsets.UTF_8));
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      return sb.toString();
    }

    private Path getResourcesPath() {
      ClassLoader classLoader = getClass().getClassLoader();
      URL inputUrl = classLoader.getResource("html");
      if (inputUrl != null) {
        String inputResourceLocation = inputUrl.getFile();
        return Paths.get(inputResourceLocation);
      }
      return null;
    }
  }

  private static class TextFormatter implements MetricFormatter {

    private final ThreadTrie threadTrie;

    TextFormatter(ThreadTrie tt) {
      this.threadTrie = tt;
    }

    @Override
    public String getOutput() {
      return threadTrie.stringForm();
    }
  }

}
