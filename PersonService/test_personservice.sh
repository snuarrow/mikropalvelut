export MONGO_KEY="tests"
export OHTU_KRYPTO="salainen"
export CONF_API="http://localhost:4569/testconfigurations"
export ENCRYPTION_KEY="foobar"
~/Documents/gradle-2.7/bin/gradle clean
~/Documents/gradle-2.7/bin/gradle build
java -jar ~/Documents/Courses/ohtu/viikko8/PersonService/build/libs/PersonService-1.0.jar

