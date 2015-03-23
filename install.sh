#!/bin/sh
#
# Creates a single install directory from all the csw stage directories.

dir=../install

test -d $dir || mkdir -p $dir/{bin,lib,conf}
#for i in hcd2 assembly1 container2 container1; do (cd $i; sbt publish-local stage); done
sbt publish-local stage
for i in bin lib ; do cp -f */target/universal/stage/$i/* $dir/$i/; done
rm -f $dir/bin/*.log.* $dir/bin/*.bat
(cd hardware/src/main/c; make; cp mtserver2 ../../../../../install/bin/)

chmod ugo+x scripts/*
cp scripts/* $dir/bin/



