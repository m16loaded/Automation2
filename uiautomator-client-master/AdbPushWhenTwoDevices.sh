#!/bin/bash
#Copy this script from git/automation to /home/topq/AutoScripts
set -x


IGNORESERIAL="08726194998cf3ad"


for SERIAL in $(adb devices | grep -v $IGNORESERIAL | cut -f 1);
do
adb -s $SERIAL root
adb -s $SERIAL push /home/topq/git/automation/uiautomator-client-master/nanomark2.apk /data/containers/priv/data/media/0/TEMP
adb -s $SERIAL push /home/topq/git/automation/uiautomator-client-master/com.quicinc.vellamo2.apk /data/containers/priv/data/media/0/TEMP
adb -s $SERIAL push /home/topq/git/automation/uiautomator-client-master/com.android.chrome.apk /data/containers/priv/data/media/0/TEMP
adb -s $SERIAL push /home/topq/git/automation/uiautomator-client-master/org.zwanoo.android.speedtest.apk /data/containers/priv/data/media/0/TEMP
done
