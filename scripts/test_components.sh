#!/bin/sh
exec scala "$0" "$@"
!#

// Demonstrates starting the test components without containers along with the location service.
// This script should be run from the csw install/bin directory, or with csw/install/bin in the shell path.

import scala.sys.process._

// Start the ZMQ based hardware simulation
"mtserver2 filter".run
"mtserver2 disperser".run
"mtserver2 pos".run
"mtserver2 one".run

// Start the location service
"loc".run

// Start Assembly-1
"assembly1".run

// Start the test HCDs
"hcd2 HCD-2A tmt.mobie.blue.filter".run
"hcd2 HCD-2B tmt.mobie.blue.disperser".run
"hcd2 HCD-2C tmt.tel.base.pos".run
"hcd2 HCD-2D tmt.tel.ao.pos.one".run

