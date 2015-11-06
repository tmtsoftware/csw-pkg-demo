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

// Start the config service annex, which stores large/binary files
"configserviceannex".run

// use an alternative config file
val config = "../../csw/cs/src/test/resources/test.conf"

// Start the config service, creating temporary main and local repositories (TODO: add -config option)
// (The -delete and -init options tell it to delete and create the local and main Git repos, so we start with a clean repo)
s"cs --delete --config $config".run 

// Create the two container config files in the config service (TODO: add -config option)
s"csclient create test/container1.conf --config $config -i ../../csw-pkg-demo/container1/src/main/resources/container1.conf".!
s"csclient create test/container2.conf --config $config -i ../../csw-pkg-demo/container2/src/main/resources/container2.conf".!

// Since the files are not found locally, they will be fetched from the config service
s"container1s test/container1.conf -c $config".run
s"container2s test/container2.conf -c $config".run

