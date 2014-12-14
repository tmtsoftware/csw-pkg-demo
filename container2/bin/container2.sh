#!/bin/sh

name=container2
launcher=/opt/local/share/sbt/sbt-launch.jar
config=$name.conf

java -Dsbt.boot.properties=$name.props -jar $launcher $config
