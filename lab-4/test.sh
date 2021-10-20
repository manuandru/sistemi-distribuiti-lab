set -e

alias client='gradle -q client:run'
alias server='gradle -q server:run'

function cleanup() {
    rm *.txt
}

function test_client_alone_fails() {
  echo "Test: launching the client alone should fail"
  (client &> /dev/null) && (echo "The client did not fail" && exit 1)
  echo "ok"
}

function test_server_alone_succeeds() {
  echo "Test: launching the server alone should succeed"
  OUTPUT=server-output.txt
  EXPECTED=server-expected-output.txt
  echo "Listening on port 10000" > $EXPECTED
  echo "Goodbye!" >> $EXPECTED
  server &> $OUTPUT &
  while [ $(cat $OUTPUT | wc -l) -lt 1 ]; do
    sleep 1
  done
  kill $!
  diff -q $EXPECTED $OUTPUT || exit 1
  echo "ok"
}

function test_two_servers_cannot_start_on_same_port() {
  echo "Test: when launching two servers on the same port, the second should fail"
  OUTPUT1=server1-output.txt
  server -Pport=10001 &> $OUTPUT1 < /dev/random &
  SERVER1=$!
  while [ $(cat $OUTPUT1 | wc -l) -lt 1 ]; do
    sleep 1
  done
  server -Pport=10001 && (echo "The second server did not fail" && kill $SERVER1 && exit 1)
  kill $SERVER1
  echo "ok"
}

gradle build

test_client_alone_fails
test_server_alone_succeeds
test_two_servers_cannot_start_on_same_port

cleanup

echo All tests succeeded