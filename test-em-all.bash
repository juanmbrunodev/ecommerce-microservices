#!/usr/bin/env bash
#
# Sample usage:
#
#   HOST=localhost PORT=7000 ./test-em-all.bash

# Declare variables for testing if not set
: ${HOST=localhost}
: ${PORT=8080}
: ${PROD_ID_REVS_RECS=1}
: ${PROD_ID_NOT_FOUND=13}
: ${PROD_ID_NO_RECS=113}
: ${PROD_ID_NO_REVS=213}

function assertCurl() {

  local expectedHttpCode=$1
  # Example of below local function variable 'assertCurl 200 "curl http://$HOST:$PORT/product-composite/1 -s"'
  # This line defines a local variable curlCmd which contains the curl command string passed as the second positional
  # parameter to the function with an additional option -w "%{http_code}" to output the HTTP status code of the curl
  # command.
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval $curlCmd)
  # This line extracts the last three characters of the result string and stores them in the httpCode variable.
  # In the context of a curl command, these three characters should correspond to the HTTP response code, such as "200" for a successful response.
  local httpCode="${result:(-3)}"
  # This line sets the RESPONSE variable to an empty string, then checks if the length of the result string is greater than three.
  # If so, it sets the RESPONSE variable to the result string with the last three characters removed. The syntax ${var%suffix}
  # is a parameter expansion that removes the specified suffix from the end of the variable $var. In this case,
  # the ??? suffix represents the HTTP response code that was already extracted in the previous line.
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]
  then
    if [ "$httpCode" = "200" ]
    then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
  else
      echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
      echo  "- Failing command: $curlCmd"
      echo  "- Response Body: $RESPONSE"
      exit 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]
  then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}

# This is a bash function called testUrl that takes a single argument (the URL to be tested) and returns either 0 (success)
# or 1 (failure) depending on whether the URL is reachable or not.
#
function testUrl() {
  url=$@
  # This line uses the $url variable as the command to execute, followed by several options:
      #
      #-k allows insecure SSL connections.
      #-s silent mode.
      #-f fail silently on server errors (e.g., HTTP 404 Not Found).
      #-o /dev/null redirects output to /dev/null, a special file that discards all output.
  if $url -ks -f -o /dev/null
  then
    return 0
  else
    return 1
  fi;
}

# This is a bash function called waitForService that takes a single argument (the URL to be tested) and waits until the
# URL is reachable by repeatedly calling the testUrl function until it returns a success status code.
function waitForService() {
  url=$@
  echo -n "Wait for: $url... "
  n=0
  until testUrl $url
  do
    n=$((n + 1))
    if [[ $n == 100 ]]
    then
      echo " Give up"
      exit 1
    else
      sleep 3
      echo -n ", retry #$n "
    fi
  done
  echo "DONE, continues..."
}

set -e

echo "Starting Tests:" `date`

echo "HOST=${HOST}"
echo "PORT=${PORT}"

# The * characters in the if statement are wildcards that match any sequence of characters (including an empty sequence).
# In the Docker Compose command docker-compose down --remove-orphans, the --remove-orphans flag is used to remove any
# Docker containers that were created by docker-compose but are not defined in the current docker-compose.yml configuration file.
# These are referred to as "orphans" because they no longer have a parent (i.e., the docker-compose.yml configuration) and are therefore considered "orphaned".
#
# By default, the docker-compose down command will only stop and remove the containers that are defined in the current docker-compose.yml file
if [[ $@ == *"start"* ]]
then
  echo "Restarting the test environment..."
  echo "$ docker-compose down --remove-orphans"
  docker-compose down --remove-orphans
  echo "$ docker-compose up -d"
  docker-compose up -d
fi

# Wait for Product Composite (orchestrator) service to be available once docker compose is UP for good.
waitForService curl http://$HOST:$PORT/product-composite/$PROD_ID_REVS_RECS

echo -e '\nVerify that a normal request works, expect three recommendations and three reviews'
assertCurl 200 "curl http://$HOST:$PORT/product-composite/$PROD_ID_REVS_RECS -s"
assertEqual $PROD_ID_REVS_RECS $(echo $RESPONSE | jq .productId)
assertEqual 3 $(echo $RESPONSE | jq ".recommendations | length")
assertEqual 3 $(echo $RESPONSE | jq ".reviews | length")

echo -e "\n Verify that a 404 (Not Found) error is returned for a non-existing productId ($PROD_ID_NOT_FOUND)"
assertCurl 404 "curl http://$HOST:$PORT/product-composite/$PROD_ID_NOT_FOUND -s"
assertEqual "No product found for productId: $PROD_ID_NOT_FOUND" "$(echo $RESPONSE | jq -r .message)"

echo -e "\nVerify that no recommendations are returned for productId $PROD_ID_NO_RECS"
assertCurl 200 "curl http://$HOST:$PORT/product-composite/$PROD_ID_NO_RECS -s"
assertEqual $PROD_ID_NO_RECS $(echo $RESPONSE | jq .productId)
assertEqual 0 $(echo $RESPONSE | jq ".recommendations | length")
assertEqual 3 $(echo $RESPONSE | jq ".reviews | length")

echo -e "\nVerify that no reviews are returned for productId $PROD_ID_NO_REVS"
assertCurl 200 "curl http://$HOST:$PORT/product-composite/$PROD_ID_NO_REVS -s"
assertEqual $PROD_ID_NO_REVS $(echo $RESPONSE | jq .productId)
assertEqual 3 $(echo $RESPONSE | jq ".recommendations | length")
assertEqual 0 $(echo $RESPONSE | jq ".reviews | length")

echo -e "\nVerify that a 422 (Unprocessable Entity) error is returned for a productId that is out of range (-1)"
assertCurl 422 "curl http://$HOST:$PORT/product-composite/-1 -s"
assertEqual "\"Invalid productId: -1\"" "$(echo $RESPONSE | jq .message)"

echo -e "\nVerify that a 400 (Bad Request) error error is returned for a productId that is not a number, i.e. invalid format"
assertCurl 400 "curl http://$HOST:$PORT/product-composite/invalidProductId -s"
assertEqual "\"Type mismatch.\"" "$(echo $RESPONSE | jq .message)"

if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
    echo "$ docker-compose down"
    docker-compose down
fi

echo "End, all tests OK:" `date`