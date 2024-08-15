#!/bin/bash

TARGET_FILES=$1

# Loop through each file in the current directory
for file in $TARGET_FILES; do
    # Sort and remove duplicates, then save back to the same file
    sort "$file" | uniq > "${file}.tmp"
    mv "${file}.tmp" "$file"
done
