#!/bin/bash
#Copy this file to /home/topq/AutoScripts/Screenshot.sh  in case your username is different than topq
set -x



if [ ! -d /home/$USER/AutoScripts/Screenshots ]; then
	 mkdir /home/$USER/AutoScripts/Screenshots
fi

IGNORESERIAL="08726194998cf3ad"
TIME=$(date "+%T")
DATE=$(date "+%F")





for SERIAL in $(adb devices | grep -v $IGNORESERIAL | cut -f 1);
do
adb -s $SERIAL root
adb -s $SERIAL pull /data/containers/priv/data/media/0/DCIM/100ANDRO
done
mv screen.png /home/$USER/AutoScripts/Screenshots/$TIME.$DATE.png
exit
