export OHTU_KRYPTO="salainen"
export CONF_API="http://localhost:4569/testconfigurations"
~/Documents/gradle-2.7/bin/gradle clean
~/Documents/gradle-2.7/bin/gradle build
java -jar ~/Documents/Courses/ohtu/viikko8/ProductService/build/libs/ProductService-1.0.jar

