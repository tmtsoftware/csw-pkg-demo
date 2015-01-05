container2x
==========

This project is not built with the other subprojects.
It is an example of how you could use the sbt launcher to dynamically
create and start a container based only on a few config files.
You can start the container with the script container2x.sh.

Files
-----

* build.properties - specifies the sbt version to use

* build.sbt - needed in order to specify the dependencies

* container2x.conf - describes the container, lists the HCDs and assemblies to start in the container

* container2x.props - input file to the [sbt launcher](http://www.scala-sbt.org/0.13.5/docs/Launcher/Configuration.html)

* container2x.sh - start script that runs the container based on the config files

Repositories and Cache
----------------------

Note that the dependencies come from an sbt/ivy repository (by default under ~/.ivy2).

Also, the dependencies are downloaded and cached in a local, application specific directory only once the first time the script
is run. The parent directory is configured here in the container2x.props file as `${user.home}/.csw/boot`.

Unfortunately, this project also depends on itself, so you need to run `sbt publish-local` once before starting.

Updates (TODO)
--------------

Since the jar file dependencies for sbt launcher based application are cached only the first time the script is run,
updating the application requires first deleting the cache directory. A script option could be added for that.

Running
-------

To run:

* `sbt publish-local`  (first time only, or after a change)

* `bash ./container2x.sh`




