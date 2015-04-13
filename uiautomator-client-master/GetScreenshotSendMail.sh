#!/bin/bash
#Copy this script from git/automation to /home/topq/AutoScripts
set -x



if [ ! -d /home/$USER/AutoScripts/ScreenshotsSend ]; then
	 mkdir /home/$USER/AutoScripts/ScreenshotsSend
fi


TIME=$(date "+%T")
DATE=$(date "+%F")
NAME=$TIME.$DATE.png
EMAILS="ibilkevich@cellrox.com"




adb pull /data/containers/priv/data/media/0/DCIM/100ANDRO 
mv screen.png /home/$USER/AutoScripts/ScreenshotsSend/$NAME

/home/topq/AutoScripts/sendEmail -f cellroxqa@gmail.com -t $EMAILS -u "[Screenshots] $DATE " -m "Screenshot taken on $TIME " -a /home/$USER/AutoScripts/ScreenshotsSend/$NAME -s smtp.gmail.com -xu cellroxqa -xp qa12rocks

exit
