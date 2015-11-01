#!/bin/sh
# This script displays memory information

FILE=memTmp.$$

#adb root
sleep 1
adb shell cat /proc/meminfo > $FILE

Buffers=`grep -w "Buffers" $FILE | grep -e "[0-9]*" -o`
Cached=`grep -w "Cached" $FILE | grep -e "[0-9]*" -o`
MemFree=`grep -w "MemFree" $FILE | grep -e "[0-9]*" -o`
MemTotal=`grep -w "MemTotal" $FILE | grep -e "[0-9]*" -o`

MEMUSED=`expr $MemTotal - $MemFree - $Cached - $Buffers `
MEMUSED="`expr $MEMUSED / 1024 `"
MEMTOTAL=$MemTotal
MEMTOTAL="`expr $MEMTOTAL / 1024 `"
MEMFREE=`expr $MEMTOTAL - $MEMUSED `

#clear
#echo "RAM:"
#echo "  Used: $MEMUSED MB"
#echo $MEMFREE |grep -v adbd
echo $MEMFREE |tail -n 1
#echo "  Total: $MEMTOTAL MB"

rm $FILE

