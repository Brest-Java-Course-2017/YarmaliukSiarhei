## Detective Agency
#### Project for EPAM Java course 2017

This is simple Detective Agency web application.
You can manage investigations, employees and relationships between them.

Environment requirements:
1. JDK 8+
2. Maven
3. Git
4. Optional: Servlet container

#### How to start REST service:
1. Clone project: ```git clone https://github.com/Brest-Java-Course-2017/YarmaliukSiarhei.git destination_folder```
2. Go to: ```cd destination_folder/rest-app```
3. Package and install rest app: ```mvn clean install```
After it war file will be available on next path:
destination_folder/rest-app/target/rest-app-1.0-SNAPSHOT.war
4. Then you can start service using some container servlets or using Jetty plugin:
    * Start service with Jetty plugin. 
    >Go to rest-app folder and run command: ```mvn jetty:run```
    * Start service with Apache Tomcat.
    >Run Apache Tomcat,
    than go to http://localhost:8080/manager/html page.
    In 'WAR file to deploy' section choose war file (rest-app-1.0-SNAPSHOT.war will be in rest-app/target folder) and than push 'Deploy' button.

After this REST service will be available in url http://localhost:8088/api/v1.

#### How to start js-client on Apache Tomcat:
1. Copy js-client folder to Apache Tomcat $CATALINA_HOME/webapps directory: ```cp destination_folder/js-client ${CATALINA_HOME}/webapps/```
2. Go to webapps folder and rename js-client folder to project_name: ```mv js-client DetectiveAgency```
3. Start Apache Tomcat

After this js-client will be available in url http://localhost:8080/DetectiveAgency
>Note: instead ${CATALINA_HOME}, you can use path to your apache-tomcat directory.                          
>Note: before you will start js-client you need running REST service.

#### Test REST service 
##### With next commands you can test REST service:
###### For employee's entity

1. get employees: ```curl -v localhost:8088/api/v1/employees?limit=5\&offset=0```
2. get employee with id=1: ```curl -v localhost:8088/api/v1/employees/1```
3. get involved employees in investigation with id = 1:                      
```curl -v localhost:8088/api/v1/employees/investigation/1?limit=5\&offset=0```
4. add new employee: 
``` curl -X POST -H 'Content-Type: application/json' -d '{"employeeId":null,"name":"Eric Dickman","age":"1990-11-26","startWorkingDate":"2005-12-16"}' -v localhost:8088/api/v1/employees ```
5. add investigations to employee in which he participated:                   
``` curl -X POST -H 'Content-Type: application/json' -d '[1,2,3]' -v localhost:8088/api/v1/employees/1/investigations ```
6. update employee: 
``` curl -X PUT -H 'Content-Type:application/json' -d '{"employeeId":2,"name":"Eric Dickman","age":"1990-11-26","startWorkingDate":"2005-12-16"}' -v localhost:8088/api/v1/employees ```
7. update employee participated in investigations:                                                    
``` curl -X PUT -H 'Content-Type:application/json' -d '[1,2,3]' localhost:8088/api/v1/employees/1/investigations ```
8. delete employee with id=1: ``` curl -X DELETE -v localhost:8088/api/v1/employees/1 ```
9. get employees' rating: ``` curl -v localhost:8088/api/v1/employees/rating?limit=5\&offset=0 ```

###### For investigation's entity
10. get investigations: ``` curl -v localhost:8088/api/v1/investigations?limit=5\&offset=0 ```
11. get filtered investigations by time period: 
``` curl -v localhost:8088/api/v1/investigations/filter?startDate=2017-11-26T00:05:08Z\&endDate=2017-11-            26T00:05:07%2B02:15\&limit=5\&offset=0 ```
12. get investigation with id=1: ``` curl -v localhost:8088/api/v1/investigations/1 ```
13. get investigations in which involved employee with id = 1:               
``` curl -v localhost:8088/api/v1/investigations/employee/1?limit=5\&offset=0 ```
14. add new investigation: 
``` curl -X POST -H 'Content-Type: application/json' -d '{"investigationId":null,"number":null,"title":"Toy thief","description":"Someone stole a rabbit toy.","startInvestigationDate":"2017-05-26T02:00:15+03:00","endInvestigationDate":null}' -v localhost:8088/api/v1/investigations ```
15. add involved staff to investigation: ``` curl -X POST -H 'Content-Type: application/json' -d '[1,3]' -v localhost:8088/api/v1/investigations/1/staff ```
16. update investigation:                               
``` curl -X PUT -H 'Content-Type: application/json'
 -d '{"investigationId":1,"number":null,"title":"Some title","description":"Some description",
 "startInvestigationDate":"2011-05-26T15:56:45+03:00","endInvestigationDate":"2013-02-29T20:01:23Z"}',
 "involvedStaff":[{"employeeId":2,"name":"Some name","age":"1965-05-16","startWorkingDate":"1980-04-16"}]
 -v localhost:8088/api/v1/investigations
```
17. update involved staff in investigation:                                                                    
``` curl -X PUT -H 'Content-Type: application/json' -d '[1,4]' -v localhost:8088/api/v1/investigations/1/staff ```
18. delete investigation with id=1: ``` curl -X DELETE -v localhost:8088/api/v1/investigations/1 ```

For add/update investigation and employee, you can add data about involved staff or participated investigations.
Example, for employee we can pass next data to server:
{"employeeId":null,"name":"Eric Dickman","age":"1990-11-26","startWorkingDate":"2005-12-16",**"participatedInvestigations":[]**}
Instead "participatedInvestigations":[] you can use "participatedInvestigations":null or omit this expression,
server will interpret it as same.

You can specify which participated investigations will be included in employee:
instead "participatedInvestigations":[] use 
"participatedInvestigations":[{"investigationId":someId,"number":null/778,"title":"Some title","description":"Some description",
"startInvestigationDate":"1996-05-26T23:56:01Z","endInvestigationDate":"2005-06-15T15:45:59Z"},...]"
In this version of application server just take investigation's id, other field not need for server.
But important which format for these fields you will use.

Same behavior you can see when you will pass investigation for add or update.
Expressions "involvedStaff":null, "involvedStaff":[] or omit it - same.
Example for investigation entity:
"involvedStaff":[{"employeeId":someId,"name":"Some Derrek","age":"1996-05-16","startWorkingDate":"1965-05-26"},
{"employeeId":someOtherId,"name":"Some other Derrek","age":"1996-05-16","startWorkingDate":"1965-05-26"},...]"

>Note: server will not change existed investigations or employees described in participatedInvestigations, involvedStaff expressions.

###### This project under the Apache License 2.0.
