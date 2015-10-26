#!/bin/bash
#Copy this script from git/automation to /home/topq/AutoScripts
set -x





IGNORESERIAL="08726194998cf3ad"
 
for SERIAL in $(adb devices | grep -v $IGNORESERIAL | cut -f 1);
do
#adb -s $SERIAL root
#adb -s $SERIAL remount
#adb -s $SERIAL push com.xdialer.apk /system/app
#adb -s ZX1G424D8J shell time -p cell next | grep -v sys|grep -v adbd|grep -v OK|grep -v user| awk -F'real     ' '{print $2}'
#adb -s ZX1G428DZP shell time -p cell next | grep -v sys|grep -v adbd|grep -v OK|grep -v user| awk -F'real     ' '{print $2}'
adb -s $SERIAL shell time -p cell next | grep -v sys|grep -v adbd|grep -v OK|grep -v user| awk -F'real     ' '{print $2}'
done




exit
