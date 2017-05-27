#!/bin/bash

sudo systemctl stop spark-fw.service
sudo systemctl stop ssh-fw.service
sudo systemctl disable spark-fw.service
sudo systemctl disable ssh-fw.service
sudo unlink /lib/systemd/system/spark-fw.service
sudo unlink /lib/systemd/system/ssh-fw.service
unlink ${HOME}/.ssh/config
