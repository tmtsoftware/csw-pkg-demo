CSW Command Service Package Demo
================================

This project contains standalone applications for testing the command service, 
Container, Assembly and HCD components and is based on
the document "OSW TN009 - TMT CSW PACKAGING SOFTWARE DESIGN DOCUMENT".

ZeroMQ Native Lib Dependency
----------------------------

In this demo the hardware layer accessed by the HCDs is simulated by a
C/ZeroMQ based process, which needs to be built separately using `make`.
The hardware simulation server is called mtserver2 and the sources are under the "hardware" directory.
The Akka ZeroMQ support currently requires ZeroMQ version 2.2.
The Scala code picks up the shared library automatically if it is installed in /usr/lib or /usr/local/lib.
The required library name on the Mac is libzmq.1.dylib.

Subprojects
----------

* assembly1 - an example assembly
* hcd2 - an example HCD
* container1 - an example container for assembly1
* container2 - an example container for a number of instances of hcd2

Note that assembly1 and hcd2 are both _components_ and can also be run standalone using generated scripts
(under target/universal/stage/bin).

A container can include multiple components. The container1 example wraps the assembly1 component while the
container2 demo wraps multiple hcd2 instances.

The scripts directory also contains the scripts container1s and container2s to demonstrate how to
dynamically configure and run containers.

Sbt Build
---------

To compile, run ./install.sh to create an install directory (../install) containing all the necessary scripts and jar files.

Note: See <a href="https://github.com/tmtsoftware/csw-extjs">csw-extjs</a> for how to setup the ExtJS
based web UI used below. You need to install and run some "sencha" commands once to prepare the web app, otherwise
the generated CSS file will not be found and the web app will not display properly.

Run the demo
------------

To run the demo, there are a number of alternative scripts provided (installed under ../install/bin):

* test_containers.sh - runs the hardware simulation code, the location service and the two containers

* test_components.sh - does the same as above, but runs the assembly and HCDs in standalone mode, without a container

* test_dynamic_containers.sh - does the same as above, but creates the containers on the fly using a
  [scalas](http://www.scala-sbt.org/0.13/docs/Scripts.html)
  script and config files for the containers

* test_containers_with_config_service.sh - does the same as above, but in this case the containers get the
  configurations from the config service, which is started in the script

Note that the 1.0-M2 version of akka-http and akka-streams produces a lot of error log messages
that can be ignored. These should go away in a future version.

Test with the web app
---------------------

* open http://localhost:8089 in a browser for the Ext JS version and select the development
(JavaScript source) or production (compiled, minified) version. Note that you need to
compile the ExtJS code at least once to get the required CSS file generated.
See <a href="https://github.com/tmtsoftware/csw-extjs">csw-extjs</a> for instructions.

Select values in the form and press Submit. The status of the command is shown below the button and updated
while the command is running.

TODO: Add the ability to pause and restart the queue, pause, cancel or abort a command, etc.

The following diagram shows the relationships of the various containers, assemblies and HCDs in this demo:

![PkgTest diagram](doc/PkgTest.jpg)

When the user fills out the web form and presses Submit, a JSON config is sent to the Spray/REST HTTP server
of the Assembly1 command service. It forwards different parts of the config to different HCDs, which run in
a different container and JVM, but are registered as components with Assembly1, so that it forwards parts of
configs that match the paths they are registered with.

The HCDs both talk to the C/ZeroMQ based hardware simulation code and then return a command status to the
original submitter (Assembly1).
