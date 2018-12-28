#!/usr/bin/env bash
# Copyright (c) 2018, Rahul S. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

#
# Analyzes jstack files in the given directory.
# Generates html and flamegraphs from the given jstacks
# @author rahulsh1
#
if [ $# -ne 1 ]; then
  echo "$0 <jstack_dir>"
  exit 1
fi

script_path=$(dirname "$0")

# generates the tree form
$script_path/mvnw install -DskipTests -Prun -DjstackDir=$1

# generates the flamegraphs
$script_path/src/main/scripts/gen-flamegraph.sh $1
