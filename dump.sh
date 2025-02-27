docker exec -i blueshell-api-db-1 mysqldump -u root -p'BLUESHELL2024!' blueshell > ./db_dump.sql
sed -i 's/utf8mb4_0900_ai_ci/utf8mb4_unicode_ci/g' db_dump.sql
