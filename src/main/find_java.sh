#!/bin/bash

# Output file
OUTPUT_FILE="java_code.txt"

# Clear the output file if it already exists
> "$OUTPUT_FILE"

# Find all files ending with 'Controller.java' or 'Service.java' and write their contents to the output file
find . -type f \( -wholename '*/security/*.java' \) | while read -r file; do
    echo "==== Contents of: $file ====" >> "$OUTPUT_FILE"
    cat "$file" >> "$OUTPUT_FILE"
    echo -e "\n\n" >> "$OUTPUT_FILE" # Add spacing between files
done

echo "All Controller.java and Service.java contents have been written to $OUTPUT_FILE"
