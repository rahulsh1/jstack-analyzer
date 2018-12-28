#!/usr/bin/env bash
# Copyright (c) 2018, Rahul Sh. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

input_dir=$1
tmp_file=/tmp/test.jstk

script_path=$(dirname "$0")
fg_out=$input_dir/flamegraph.svg
fg_rev_out=$input_dir/flamegraph_icicles.svg

# concat the file
function concat_file {
    :> $tmp_file
    for i in `ls $input_dir/*.jstack`
    do
      cat $i >> $tmp_file
    done
}

# Generate flamegraphs
function generate {
    cat $tmp_file | $script_path/stackcollapse-jstack.pl | $script_path/flamegraph.pl --color=green > $fg_out
    cat $tmp_file | $script_path/stackcollapse-jstack.pl | $script_path/flamegraph.pl --reverse --invert --color=green > $fg_rev_out
    echo "Generated Flamegraphs..."
    echo $fg_out
    echo $fg_rev_out
}

echo "Processing jstacks in $input_dir"
concat_file
generate
rm $tmp_file
echo "Generated tree forms..."
echo "$input_dir/thread-tree.txt"
echo "$input_dir/thread-tree.htm"


