#!/bin/sh

name=container1x
launcher=/opt/local/share/sbt/sbt-launch.jar

java -Dsbt.boot.properties=$name.props -jar $launcher $name.conf
