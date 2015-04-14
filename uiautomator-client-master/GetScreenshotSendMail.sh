#!/bin/bash
#Copy this script from git/automation to /home/topq/AutoScripts
set -x



if [ ! -d /home/$USER/AutoScripts/ScreenshotsSend ]; then
	 mkdir /home/$USER/AutoScripts/ScreenshotsSend
fi

IGNORESERIAL="08726194998cf3ad"
TIME=$(date "+%T")
DATE=$(date "+%F")
NAME=$TIME.$DATE.png
EMAILS="ibilkevich@cellrox.com"




#adb -s 06416c4f23b784a2 pull /data/containers/priv/data/media/0/DCIM/100ANDRO 
for SERIAL in $(adb devices | grep -v $IGNORESERIAL | cut -f 1);
do
adb -s $SERIAL root
adb -s $SERIAL pull /data/containers/priv/data/media/0/DCIM/100ANDRO
done
mv screen.png /home/$USER/AutoScripts/ScreenshotsSend/$NAME

/home/topq/AutoScripts/sendEmail -f cellroxqa@gmail.com -t $EMAILS -u "[Screenshots] $DATE " -m "Screenshot taken on $TIME " -a /home/$USER/AutoScripts/ScreenshotsSend/$NAME -s smtp.gmail.com -xu cellroxqa -xp qa12rocks

exit
