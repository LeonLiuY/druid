#!/bin/bash
TDIR=`mktemp -d`

echo "installing in temp dir "$TDIR
trap "{ cd - ; rm -rf $TDIR; exit 255; }" SIGINT

cd $TDIR
JAVA_VER=$(java -version 2>&1 | sed 's/java version "\(.*\)\.\(.*\)\..*"/\1\2/; 1q')
if [ "$JAVA_VER" -ge 18 ]; then
    echo "ok, java is 1.8 or newer"
else
    echo "require java 1.8 or newer"
    exit 1
fi

git clone https://github.com/liuyang1204/druid.git
cd druid
mvn clean assembly:assembly
rm -rf ~/.druid
mkdir ~/.druid
cp target/druid.jar ~/.druid
echo "druid='java -jar $HOME/.druid/druid.jar'" >> ~/.profile && source ~/.profile

rm -rf $TDIR

exit 0
