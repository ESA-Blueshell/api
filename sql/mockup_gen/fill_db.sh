#!/bin/bash

# Deze script is medemogelijk gemaakt door: Vim!

# Here you can set the username, database name and password 
username="mauk"
database="blueshell"
password="123"

echo $password

# Run the .sql files in sequential order ensuring forein keys constrained are fullfilled.
mysql -u $username --password=$password $database < users.sql
mysql -u $username --password=$password $database < registrations.sql
mysql -u $username --password=$password $database < pictures.sql
mysql -u $username --password=$password $database < committees.sql
mysql -u $username --password=$password $database < events.sql
mysql -u $username --password=$password $database < event_feedback.sql
mysql -u $username --password=$password $database < event_signups.sql
mysql -u $username --password=$password $database < billables.sql
mysql -u $username --password=$password $database < sponsors.sql

