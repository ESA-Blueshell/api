#!/bin/bash

# Define the directory to list
DIRECTORY=${1:-.}

# Output file (optional)
OUTPUT_FILE="project_file_tree.txt"

# Generate the file tree
echo "Generating file tree for directory: $DIRECTORY"
tree -a "$DIRECTORY" > "$OUTPUT_FILE"

# Output result
echo "File tree saved to $OUTPUT_FILE"
