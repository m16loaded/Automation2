#!/bin/bash
#Copy this script from git/automation to /home/topq/AutoScripts
set -x


IGNORESERIAL="08726194998cf3ad"


for SERIAL in $(adb devices | grep -v $IGNORESERIAL | cut -f 1);
do
adb -s $SERIAL root
adb -s $SERIAL push /home/topq/Downloads/nanomark2.apk /data/containers/priv/data/media/0/TEMP
done
