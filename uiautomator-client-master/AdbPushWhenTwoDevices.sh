#!/bin/bash
#Copy this script from git/automation to /home/topq/AutoScripts
set -x


IGNORESERIAL="08726194998cf3ad"


for SERIAL in $(adb devices | grep -v $IGNORESERIAL | cut -f 1);
do
adb -s $SERIAL root
adb -s $SERIAL push /home/topq/git/automation/uiautomator-client-master/nanomark2.apk /personas/priv/data/app
adb -s $SERIAL push /home/topq/git/automation/uiautomator-client-master/nanomark2.apk /personas/corp/data/app
adb -s $SERIAL push /home/topq/git/automation/uiautomator-client-master/com.quicinc.vellamo2.apk /personas/priv/data/app
adb -s $SERIAL push /home/topq/git/automation/uiautomator-client-master/com.android.chrome.apk /personas/priv/data/app
adb -s $SERIAL push /home/topq/git/automation/uiautomator-client-master/org.zwanoo.android.speedtest.apk /personas/priv/data/app
adb -s $SERIAL push /home/topq/git/automation/uiautomator-client-master/cxRilPolicy.apk /system/app
done
