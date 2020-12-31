COURSE PROJECT
==========

A Scala Instrumenter for Java Code
=====================================================

##**TEAM**## <br>
ARVIND GANESAN <br>
ABHIJEET MOHANTY 

**Table of Contents**

1. Importing the project

2. Libraries Used

3. Running the program

4. Outline of the project


**Importing the project**
- Open Intellij. Go to New->Project from existing sources and choose the project
- Build the project to resolve the dependencies

**Libraries Used**
- Eclipse JDT: To create, process AST for Java source code
- TypeSafe - To add configuration support 
- Logback - Logger used with Slf4j
- Slf4j - Wrapper for logger
- tools.jar :  Library for launching JVM instance. Its usually located in the 
- path: "C:\Program Files\Java\jdk1.8.0_131\lib\tools.jar". Sometimes, the project
  may not be able to find it and may report jdi modules not being found. So having this jar in the project is necessary

**Running the program**
- Run the command ***sbt clean compile test*** in the sbt shell to build and run the test cases.
- **To run the project in IntelliJ, first Go to Build->Rebuild Project**.
- **The project can only be run in IntelliJ by running the main function of the object InstrumentationDriver.scala.
Click on Green-Colored Play symbol left to main function of InstrumentationDriver.scala**
- Running via ***sbt run*** command was not able to resolve tools.jar and sbt was only able
to compile the codebase and run the test cases

**Outline of the project**
- The project takes 2 Java Files
- Instruments it by adding logging for various AST Nodes
- Generates new Source code
- Runs JDI to get runtime values of instrumented lines
- Shows consolidated output
