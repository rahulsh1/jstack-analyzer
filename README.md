# jstack-analyzer
Analyzes Hotspot/OpenJDK Jstacks and generates a HTML and flamegraphs views for inspecting the expensive paths.

[![Build Status](https://travis-ci.org/rahulsh1/jstack-analyzer.svg?branch=master)](https://travis-ci.org/rahulsh1/jstack-analyzer)

## Collecting JStacks

    $ ./collectjstack.sh <process_name/id>
    e.g.
    $ ./collectjstack.sh mylauncher 1
    Found process mylauncher with PID: 71498
    Collecting jstacks in dir: jstack_12272018_2337
      20181227233708.71498.jstack
      20181227233709.71498.jstack
      20181227233710.71498.jstack
      20181227233711.71498.jstack
      20181227233712.71498.jstack
     ^C

## To Analyse, run

    $ ./jstack-analyze.sh <path_to_jstacks>
    e.g.
    $ ./jstack-analyze.sh ./sample

    Processing jstacks in ./sample
    Generated Flamegraphs...
    ./sample/flamegraph.svg
    ./sample/flamegraph_icicles.svg
    Generated tree forms...
    ./sample/thread-tree.txt
    ./sample/thread-tree.htm


### Flamegraph

Taken from Brendan Gregg's awesome [flamegraphs](https://github.com/brendangregg/FlameGraph)

# License
BSD