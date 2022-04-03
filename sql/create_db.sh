#!/bin/bash

# Deze script is medemogelijk gemaakt door: Vim!

# Here you can set the username, database name and password
username="peter"
database="blueshell"
password="patat"

echo $password

# Run the .sql files in sequential order ensuring forein keys constrained are fullfilled.
mysql -u $username --password=$password $database < V1_Init.sql
mysql -u $username --password=$password $database < V1_update.sql
mysql -u $username --password=$password $database < V20220403_events.sql

