## General

Simple implementation of service for fetching posts via REST API and storing them in files.

There is still some stuff to improve like:

* retrying fetch operation e.g. (Resilience4j)
* better error handling e.g. for file saving
* tests organization (Unit/Integration)

## Usage

Application can be run via executable jar (requires output directory path as argument)

java -jar target/post-service-1.0-SNAPSHOT-jar-with-dependencies.jar <span style="color:red">C:\Users\user-name\Desktop\output</span>

Example

```
    .\mvnw clean install  
    java -jar target/post-service-1.0-SNAPSHOT-jar-with-dependencies.jar C:\Users\user-name\Desktop\output
```