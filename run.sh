# load the dB. Put DROP statements in bitcoin.sql if necessary
psql -d CS421 < bitcoin.sql

# run UI prompt
java bin/bitcoin/BitcoinPrompt