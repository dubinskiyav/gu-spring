SET JAVA_HOME="D:\java\jdk-11"
call "C:\Program Files (x86)\Java\apache-maven-3.6.3\bin\mvn.cmd" clean package
copy ".\target\gu-spring-0.0.1-SNAPSHOT.jar" ".\target\gu-spring.jar"
rem Скопировать в .\target\gu-spring.jar \\build.domain.gelicon.biz\distributive\GeliconUtilitiesCrimea\
