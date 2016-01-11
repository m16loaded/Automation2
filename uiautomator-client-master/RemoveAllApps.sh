#!/bin/bash
#Copy this script from git/automation to /home/topq/AutoScripts
set -x


IGNORESERIAL="08726194998cf3ad"


for SERIAL in $(adb devices | grep -v $IGNORESERIAL | cut -f 1);
do
adb -s $SERIAL root
adb -s $SERIAL shell rm -rf /personas/p1/data/app/nanomark2* /personas/p2/data/app/nanomark2* /personas/p1/data/app/com.quicinc.vellamo2* /personas/p1/data/app/com.android.chrome* /personas/p1/data/app/org.zwanoo.android.speedtest* /personas/p1/data/app/imoblife.androidsensorbox*

done
#
