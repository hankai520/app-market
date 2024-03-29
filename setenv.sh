#! /bin/sh
#
# Set environment variables
#

TCP_LISTEN=0.0.0.0
TCP_PORT=8000

PRGDIR=`dirname "$PRG"`
APP_HOME=$PRGDIR/data

# specify absolute path of "java" command here or keep default to use java that configured
# in environment variables
JAVA_EXEC=java

# More arguments passed to spring boot or jvm
#EXTRA_ARGS=""
EXTRA_ARGS="--proxy.name=apps.sparksoft.com.cn --proxy.port=443 --proxy.scheme=https"