# See all keys
KEYS *
#curl http://localhost:8080/api/salons on terminal
# See all salon list cache keys
KEYS salons*

# Read a specific cached value (your JSON)
GET "salons::null-null"

# Check how many seconds until a key expires
TTL "salons::null-null"

# Manually delete one cache key
DEL "salons::null-null"

# Wipe everything (same as FLUSHALL)
FLUSHDB

# Check memory usage of one key
MEMORY USAGE "salons::null-null"

# Count total keys
DBSIZE