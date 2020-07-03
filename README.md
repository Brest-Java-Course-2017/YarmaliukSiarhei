## Detective Agency
#### Sample web-app project using java8 as backend and pure js as frontend part.

Domain is the Detective Agency.

Environment:
1. JDK 8+
2. Maven
3. Git

#### How to start REST service:
1. Clone project: ```git clone https://github.com/Brest-Java-Course-2017/YarmaliukSiarhei.git destination_folder```
2. Go to: ```cd destination_folder/rest-app```
3. Run ```mvn clean install```
4. Start app using container servlets, also you can use Jetty plugin:
    * Start service with Jetty plugin. 
    >Go to rest-app folder and run command: ```mvn jetty:run```
    * Start service with Apache Tomcat.
    >Run Apache Tomcat, and deploy web archive to server.

REST API: http://localhost:8088/api/v1.

###### This project under the Apache License 2.0.
