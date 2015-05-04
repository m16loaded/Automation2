#!/bin/bash -x
set -x
#
# cellrox-update.sh <local file> - push local file to device and install
#
# example for push to non default dir:
# DLOAD_DIR=/sdcard cellrox-update.sh ota.zip
#
IGNORESERIAL="08726194998cf3ad"
UPDSRC="$1"
UPDSRC2="/home/topq/main_jenkins/workspace/Automation_Nightly_Hammerhead_Lollipop_SHAMU/artifacts/ota-nightly.zip"
SERIAL="ZX1G424LDD"

ADB=${ADB:-adb}
DLOAD_DIR=${DLOAD_DIR:-/data/media/ota}

function error()
{
	echo `basename $0` ": error: $1"
	exit 1
}

#for SERIAL in $(adb devices | grep -v $IGNORESERIAL | cut -f 1);  #new

#do #new
# test sadb/adb mixup
$ADB -s $SERIAL shell id | grep -v "not found" || error "connecting to device (ADB=$ADB)"
# test root privleges on device
$ADB -s $SERIAL shell id | grep "root" || ($ADB -s $SERIAL root && sleep 3)
$ADB -s $SERIAL shell id | grep "root" || error "insufficient privileges on device"

# mount /data and /cache just in case..
$ADB -s $SERIAL remount
$ADB -s $SERIAL root
$ADB -s $SERIAL shell mount /cache
$ADB -s $SERIAL shell mount /data

# test dest dir on device
$ADB -s $SERIAL shell "mkdir -p $DLOAD_DIR"

function usage()
{
	echo "Usage: $0 <update file>"
	exit 1
}

[ -n "$UPDSRC" ] || usage

if [ -f "$UPDSRC" ]; then
	local_md5sum=`md5sum "$UPDSRC"|(read s f;echo $s)`
	# make room for new image
	$ADB -s $SERIAL shell "rm $DLOAD_DIR/*"
	UPDTRG=$DLOAD_DIR/`basename "$UPDSRC"`
	$ADB -s $SERIAL push "$UPDSRC" "$UPDTRG" || \
		error "pushing $UPDSRC failed"
	remote_md5sum=`$ADB -s $SERIAL shell "md5sum $UPDTRG"|(read s f;echo $s)`
	[ "$local_md5sum" = "$remote_md5sum" ] || \
		error "verifying $UPDSRC failed"
else
	error "file not found $UPDSRC"
fi

$ADB -s $SERIAL reboot recovery || error "cannot reboot to recovery"
# 'adb wait-for-device' doesn't work with recovery, so poll
while true; do
	$ADB -s $SERIAL shell id && break;
	sleep 5
done
$ADB -s $SERIAL shell "export DISABLE_SIGNATURE_CHECKING=1; recovery --update_package=$UPDTRG" || \
	error "error installing $UPDTRG"
#done #new
