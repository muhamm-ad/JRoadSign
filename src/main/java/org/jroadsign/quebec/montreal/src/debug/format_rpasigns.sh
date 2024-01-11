#!/bin/bash

TARGET_FILE="$1"

format_line() {
    local line="$1"

    # Add indentation for specific keywords and characters
    line=$(echo "$line" | sed -r 's/(\{)/ { /g')
    line=$(echo "$line" | sed -r 's/(\})/ \}/g')

    # Indent specific fields
    line=$(echo "$line" | sed -r 's/id/\n\tid/g')

    line=$(echo "$line" | sed -r 's/description/\n\tdescription/g')
    line=$(echo "$line" | sed -r 's/stringDescription/\n\t\tstringDescription/g')
    line=$(echo "$line" | sed -r 's/durationMinutesList/\n\t\tdurationMinutesList/g')

    line=$(echo "$line" | sed -r 's/dailyTimeRangeList/\n\t\tdailyTimeRangeList/g')
    line=$(echo "$line" | sed -r 's/DailyTimeRange/\n\t\t\t\tDailyTimeRange/g')

    line=$(echo "$line" | sed -r 's/weeklyDays/\n\t\tweeklyDays/g')

    line=$(echo "$line" | sed -r 's/annualMonthRangeList/\n\t\tannualMonthRangeList/g')
    line=$(echo "$line" | sed -r 's/AnnualMonthRange/\n\t\t\t\t\AnnualMonthRange/g')

    line=$(echo "$line" | sed -r 's/additionalMetaData/\n\t\tadditionalMetaData/g')
    line=$(echo "$line" | sed -r 's/code/\n\tcode/g')

    line=$(echo "$line" | sed -r "s/' \},/'\n\t\}/g")
    line=$(echo "$line" | sed -r "s/' \}/'\n\}/g")
    line=$(echo "$line" | sed -r 's/],/\n\t\t\t],/g')

    # Print the formatted line
    echo -e "$line"
}


if [ -z "$TARGET_FILE" ]; then
    echo "Usage: $0 <filename>"
    exit 1
fi

if [ ! -f "$TARGET_FILE" ]; then
    echo "Error: File not found."
    exit 1
fi

TEMP_FILE="${TARGET_FILE}.tmp"

if [ -f "$TEMP_FILE" ]; then
    rm "$TEMP_FILE"
fi

# REVIEW
while IFS= read -r line; do
    formatted_line=$(format_line "$line")
    echo "$formatted_line" >> "$TEMP_FILE"
done < "$TARGET_FILE"

mv "$TEMP_FILE" "$TARGET_FILE"
