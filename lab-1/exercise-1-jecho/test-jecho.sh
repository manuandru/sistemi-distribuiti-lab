# do NOT alter this file

TEST_FILE=test.txt
DIFF_FILE=diff.txt

EXPECTED_NORMAL=expected_normal.txt
EXPECTED_UPPERCASE=expected_uppercase.txt
EXPECTED_LOWERCASE=expected_lowercase.txt

ACTUAL_NORMAL=actual_normal.txt
ACTUAL_UPPERCASE=actual_uppercase.txt
ACTUAL_LOWERCASE=actual_lowercase.txt

echo lowercase > $TEST_FILE
echo UPPERCASE >> $TEST_FILE
echo MixedCase >> $TEST_FILE

cp $TEST_FILE $EXPECTED_NORMAL

echo LOWERCASE > $EXPECTED_UPPERCASE
echo UPPERCASE >> $EXPECTED_UPPERCASE
echo MIXEDCASE >> $EXPECTED_UPPERCASE

echo lowercase > $EXPECTED_LOWERCASE
echo uppercase >> $EXPECTED_LOWERCASE
echo mixedcase >> $EXPECTED_LOWERCASE

cat $TEST_FILE | ./gradlew --console=plain -q run > $ACTUAL_NORMAL
cat $TEST_FILE | ./gradlew --console=plain -q run -Pmode=uppercase > $ACTUAL_UPPERCASE
cat $TEST_FILE | ./gradlew --console=plain -q run -Pmode=lowercase > $ACTUAL_LOWERCASE

diff $EXPECTED_NORMAL $ACTUAL_NORMAL > $DIFF_FILE
diff $EXPECTED_UPPERCASE $ACTUAL_UPPERCASE >> $DIFF_FILE
diff $EXPECTED_LOWERCASE $ACTUAL_LOWERCASE >> $DIFF_FILE

if [ -s $DIFF_FILE ]; then
    echo "Unexpected behaviour for JEcho" 1>&2
    cat $DIFF_FILE 1>&2
    rm *.txt
    exit 1
else
    rm *.txt
    echo "JEcho implementation and Gradlefication are ok"
    exit 0
fi