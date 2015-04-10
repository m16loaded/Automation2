#!/bin/bash

set -x



if [ ! -d /home/$USER/AutoScripts/Screenshots ]; then
	 mkdir /home/$USER/AutoScripts/Screenshots
fi


TIME=$(date "+%T")
DATE=$(date "+%F")





adb pull /data/containers/priv/data/media/0/DCIM/100ANDRO 
mv screen.png /home/$USER/AutoScripts/Screenshots/$TIME.$DATE.png
exit
