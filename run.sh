#!/bin/sh

echo "[Altair 8080 emulator]"
echo 
echo "After the emulation starts, use Ctrl+K to quit"
echo
echo "Press any key to continue..."

read
clear

stty cbreak -echo -icrnl -onlcr -istrip intr ^K

java -jar target/altair8800-1.0-SNAPSHOT.jar

stty sane
echo
