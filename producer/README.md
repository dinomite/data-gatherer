Pull sensors via rtl_433 and produce them to HTTP

# Prerequisites
rtl_433 command

# Startup script
Install at /etc/systemd/system/data-producer.service

Then

    sudo systemctl --system daemon-reload
    sudo systemctl enable data-producer.service

Manage with:

    sudo systemctl [start/stop] data-producer.service
