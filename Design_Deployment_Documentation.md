# DESIGN & DEPLOYMENT DOCUMENT FOR SCALA INSTRUMENTER

##**TEAM**##
**Arvind Ganesan** <br>
**Abhijeet Mohanty**

## Table of Contents
  - Required Tools and Frameworks
  - Dependencies Used
  - Working of Instrumenter
  - Getting variable values
  - Setting up Configuration library
  - Unit Tests for Codebase
  - Unit Tests for Instrumenter
  - Results
  
### Required Tools and Frameworks

 - **IDE:** IntelliJ Ultimate 2019.2.3
 - **Scala Version:** 2.13.0
 - **Java Version:** Java 8 SDK

## Dependencies Used
    
  - Name : Slf4j - Wrapper API for logging
 - Name: logback - Logging library
 - Name: Eclipse JDT - Create, Process AST for Java source code
 - Name: JUnit - Unit Testing library
 - Name: Typesafe Config - Configuration library
       
   
## Working of Instrumenter
    - There are two Java source files used in this project: Sample1.java and       Sample2.java
    - Files Location: **code-to-instrument folder**
    - Steps include:
        - Load the file using Compilation Unit
        - Create a Visitor to visit each node in the Abstract Syntax Tree (AST)
        - In the class that extends ASTVisitor, define methods to visit nodes of specific type: For example, WhileStatement type is used to visit all the while block declared in the source code. 
        - In the project, the following types are visited:
            - MethodDeclaration
            - MethodInvocation
            - VariableDeclarationStatement
            - ForStatement
            - WhileStatement
            - DoStatement
            - ReturnStatement
    - Using ASTRewrite type, "Logging.info" line is added the source code for each of the visitors defined above
    - The modified AST is again converted to file and written to the disk


## Getting Variable Values
 - sd-api jar is present in JDK bin folder, which contains JDI components
 - JDI components are used to provide functionalities to launch JVM instance, load a java program and perform operations such as setting up a breakpoint
 - The instrumented class is compiled to product .class files. For each of the .class file, JDI debugger is launched with the line number for which breakpoint has to be applied. 
 - The line numbers are nothing but the line numbers where "Logging.info" instrumentation was applied to the java source code
 - JDI debugger returns the runtime values of the variables that are alive at the breakpoint. 
 - The data is stored in a Map(Variable->Value)

## Setting up Configuration library
   - After adding TypeSafe dependency, an application.conf file 
     is created inside the required module under resources folder.
   - The following values are defined in the configuration file:
        -   File1="Sample1.java" - File name
        -   File2="Sample2.java" - File Name
        -   SourceFileRootPath="code-to-instrument" - Folder that contains Java code
        -   InstrumentedFilesRootPath="src\\main\\java" - Folder to put instrumented code
        -   ClassFilesPath="target/scala-2.12/classes" - Defines location where .class files are generated and referred by JDI

## Unit Tests for Codebase
 - There are 3 unit tests


## Unit Tests for Instrumenter
    - There are 3 unit tests



## Results
-   **Using IntelliJ**
    - Run the following commands in gradle window in IntelliJ for the whole project: clean, build.
    - This shows the results of the compilation in output window in IntelliJ. After that executing 'run' task in gradle window in IntelliJ runs the code and shows appropriate logs

