#!/bin/bash

sudo ln -s ${PWD}/spark-fw.service /lib/systemd/system/
sudo ln -s ${PWD}/ssh-fw.service /lib/systemd/system/
ln -s ${PWD}/ssh_config ${HOME}/.ssh/config
sudo systemctl enable spark-fw.service
sudo systemctl enable ssh-fw.service
sudo systemctl start spark-fw.service
sudo systemctl start ssh-fw.service
