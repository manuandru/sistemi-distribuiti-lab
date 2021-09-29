# do NOT alter this file

EXPECTED_JARS=expected_jars.txt
ACTUAL_JARS=actual_jars.txt
DIFF_FILE=diff.txt

echo "./gradle/wrapper/gradle-wrapper.jar" > $EXPECTED_JARS
find . -type f -name "*.jar" > $ACTUAL_JARS

diff $EXPECTED_JARS $ACTUAL_JARS > $DIFF_FILE

if [ -s $DIFF_FILE ]; then
    echo "No .jar file should be tracked apart from:" 1>&2
    cat $EXPECTED_JARS 1>&2
    rm *.txt
    exit 1
else
    rm *.txt
    echo "No .jar dependency found (this is ok!)"
    exit 0
fi