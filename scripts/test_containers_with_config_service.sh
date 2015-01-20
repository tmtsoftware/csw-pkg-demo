#!/bin/sh
exec scala "$0" "$@"
!#

// Demonstrates starting the test containers using config files retrieved from the config service.
// This script should be run from this dir (csw/install/bin).
// (Note: "ls".run runs ls in the background, while "ls".! runs ls and waits. )

import scala.sys.process._

// Start the ZMQ based hardware simulation
"mtserver2 filter".run
"mtserver2 disperser".run
"mtserver2 pos".run
"mtserver2 one".run

// Start the location service
"loc".run

// Start the config service annex, which stores large/binary files
"configserviceannex".run

// Start the config service, creating temporary main and local repositories (TODO: add -config option)
"cs -delete -init".run 

// Create the two container config files in the config service (TODO: add -config option)
"csClient create test/container1.conf ../../csw-pkg-demo/container1/src/main/resources/container1.conf".!
"csClient create test/container2.conf ../../csw-pkg-demo/container2/src/main/resources/container2.conf".!

// Since the files are not found locally, they will be fetched from the config service
"container1s test/container1.conf".run
"container2s test/container2.conf".run
