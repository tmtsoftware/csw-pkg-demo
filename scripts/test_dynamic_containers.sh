#!/bin/sh
exec scala "$0" "$@"
!#

// Demonstrates dynamically generating the containers using scalas scripts with embedded dependency declarations.
// This script should be run from this dir (csw/install/bin).

import scala.sys.process._

// Start the ZMQ based hardware simulation
"mtserver2 filter".run
"mtserver2 disperser".run
"mtserver2 pos".run
"mtserver2 one".run

// Start the location service
"loc".run

// Start the containers, using the given config files
"container1s ../../csw-pkg-demo/container1/src/main/resources/container1.conf".run
"container2s ../../csw-pkg-demo/container2/src/main/resources/container2.conf".run
