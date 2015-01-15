#!/bin/sh
exec scala "$0" "$@"
!#

// Demonstrates starting the test containers along with the location service.
// This script should be run from the csw install directory, or with csw/install/bin in the shell path.

import scala.sys.process._

"mtserver2 filter".run
"mtserver2 disperser".run
"mtserver2 pos".run
"mtserver2 one".run

"loc".run
"container1".run
"container2".run

