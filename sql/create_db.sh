#!/bin/bash

# Deze script is medemogelijk gemaakt door: Vim!

# Here you can set the username, database name and password
username="peter"
database="blueshell"
password="patat"

echo $password

# Run the .sql files in sequential order ensuring forein keys constrained are fullfilled.
mysql -u $username --password=root$password $database < V1_Init.sql
mysql -u $username --password=root$password $database < V1_update.sql
mysql -u $username --password=root$password $database < V2_events.sql
mysql -u $username --password=root$password $database < V3_fucksubscriptions.sql
mysql -u $username --password=root$password $database < V4_addcolumns_user_reset_stuff.sql
mysql -u $username --password=root$password $database < V5_addcolumns_user_moreinfo.sql
mysql -u $username --password=root$password $database < V6_users_photoconsent_default.sql
mysql -u $username --password=root$password $database < V7_users_other_defaults.sql
mysql -u $username --password=root$password $database < V8_authorities_dropcolumn_username.sql
mysql -u $username --password=root$password $database < V9_event_signup_delete_trigger.sql
mysql -u $username --password=root$password $database < V10_fix_column_naming.sql
mysql -u $username --password=root$password $database < V11_users_signature_fields.sql


