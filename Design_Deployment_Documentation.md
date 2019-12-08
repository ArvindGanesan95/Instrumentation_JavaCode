# DESIGN & DEPLOYMENT DOCUMENT FOR SCALA INSTRUMENTER

##**TEAM**##
**Arvind Ganesan** <br>
**Abhijeet Mohanty**

## Table of Contents
  - Required Tools and Frameworks
  - Dependencies Used
  - Working of Instrumenter
  - Working of JDI Debugger
  - Files and Folders Used
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


## Working of JDI Debugger
 -   The .class files generated in target/scala-2.13/classes folder will be used
 by the debugger. After instrumenting, the lines where logging is used are found and these details
 are passed to the debugger. The debugger returns the runtime values for every variable in the scope
 at the breakpoint
 - The results are stored in a map of the form: Variable->RuntimeValue


## Files and Folders Used
 - The folder code-to-instrument is required. The two files Sample1.java and Sample2.java are to be placed here.
 - Next, the sample files are placed in com.instrumentation package
 - During compilation, .class files are generated in "target/scala-2.13/classes" path
 - The .class files generated here is used by JDI debugger



## Getting Variable Values
 - tools jar is present in JDK lib folder, which contains JDI components
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
        -   ClassFilesPath="target/scala-2.13/classes" - Defines location where .class files are generated and referred by JDI

## Unit Tests for Codebase
 - There are 3 unit tests


## Unit Tests for Instrumenter
    - There are 3 unit tests



## Results
-   **Using IntelliJ**
    - Run the main function of object InstrumentationDriver.scala in IntelliJ to instrument, generate scope table.
    - Sample output is shown below<br>
   ``` 
    13:51:22.821 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting method invocation with name : parseInt within parent 60
    13:51:22.821 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting variable declaration statement within parent : int d=Integer.parseInt(args[3]);
    
    13:51:22.821 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting method invocation with name : parseInt within parent 60
    13:51:22.821 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting variable declaration statement within parent : int e=Integer.parseInt(args[4]);
    
    13:51:22.821 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting method invocation with name : parseInt within parent 60
    13:51:22.822 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting a for statement
    13:51:22.822 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting method invocation with name : add within parent 8
    13:51:22.822 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting variable declaration statement within parent : int m=0;
    
    13:51:22.823 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting a while statement
    13:51:22.823 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting method invocation with name : add within parent 8
    13:51:22.824 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting method declaration with name : add
    13:51:22.824 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting a return statement
    13:51:23.121 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting method invocation with name : getLogger within parent 23
    13:51:23.121 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting method declaration with name : main
    13:51:23.121 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting variable declaration statement within parent : int n1=0, n2=1, n3, i, count=10;
    
    13:51:23.122 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting method invocation with name : print within parent 8
    13:51:23.122 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting a for statement
    13:51:23.122 [main] INFO com.instrumentation.visitor.CustomASTVisitor - Instrumenting method invocation with name : print within parent 8
    Class is class Sample1
    Local Variables =
    	args = instance of java.lang.String[5] (id=441)
    	a = 1
    Class is class Sample1
    Local Variables =
    	b = 2
    	a = 1
    	args = instance of java.lang.String[5] (id=443)
    Class is class Sample1
    Local Variables =
    	c = 3
    	b = 2
    	a = 1
    	args = instance of java.lang.String[5] (id=443)
    Class is class Sample1
    Local Variables =
    	c = 3
    	b = 2
    	a = 1
    	args = instance of java.lang.String[5] (id=443)
    	d = 4

```

   - The results of debugger and AST are combined to produce
       the final output of the format: variable identifier,line number,scope,runtime value
       
       VARIABLE SCOPE DETAILS FOR SECOND PROGRAM
       ListBuffer(VariableScopeDetails(n1,public static void main(String args[]){
         int n1=0, n2=1, n3, i, count=10;
         System.out.print(n1 + " " + n2);
         for (i=2; i < count; ++i) {
           n3=n1 + n2;
           System.out.print(" " + n3);
           n1=n2;
           n2=n3;
         }
       }
       ,10,0))
       
       Here n1 is the variable and 10 is its line number in the source code
       and 0 is its runtime value