#! /bin/bash

# Modify touchpad
synclient FingerLow=5 FingerHigh=5 VertScrollDelta=10 HorizScrollDelta=50 VertTwoFingerScroll=0 HorizTwoFingerScroll=0 CircularScrolling=1

# Mount Google Drive
rclone mount gdrive: gdrive
