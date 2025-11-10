#!/usr/bin/bash

./cleanup.sh

echo "Reuploading data"
source ./.venv/bin/activate \
    && pip install -r ./etc/requirements \
    && python ./etc/init.py

echo "Reupload complete"

