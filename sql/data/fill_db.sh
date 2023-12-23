#!/bin/bash

# Deze script is medemogelijk gemaakt door: Vim!

# Here you can set the username, database name and password
username="peter"
database="blueshell"
password="patat"

echo $password

# Run the .sql files in sequential order ensuring forein keys constrained are fullfilled.
mysql -u $username --password=$password $database < newsarticles.sql
mysql -u $username --password=$password $database < committees.sql
mysql -u $username --password=$password $database < events.sql

