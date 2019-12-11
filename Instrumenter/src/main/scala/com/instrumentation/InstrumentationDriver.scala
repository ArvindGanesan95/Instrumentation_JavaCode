package com.instrumentation

import java.io.{File, FileInputStream, PrintWriter}
import java.net.{URL, URLClassLoader}
import java.nio.file.{Files, Paths}
import java.util
import java.util.Timer
import org.slf4j.Logger
import org.slf4j.LoggerFactory


import com.instrumentation.visitor.CustomASTVisitor
import com.sun.jdi.{LocalVariable, Value}
import com.typesafe.config.ConfigFactory
import debugger.Debugger
import org.apache.commons.io.IOUtils
import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import org.eclipse.jface.text.Document
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


object configObject {
  val config = ConfigFactory.load()
}

object InstrumentationDriver extends LazyLogging {

  var customASTVisitor: CustomASTVisitor = null

  def main(args: Array[String]): Unit = {


    logger.info("Starting the instrumentor")
    // Get the list of libraries used in the programs current classpath
    val list = System.getProperty("java.class.path").split(";")
    var isLogBackFound = false
    var isSLF4JFound = false
    var isLogBackCoreFound = false
    var filePaths: String = ""

    // Check if the following libraries are available. Its required for the current program
    // as well as for instrumenting the java codes
    list.foreach((line: String) => {
      if (line.contains("slf4j-api-1.7.26.jar")) {
        filePaths += line + ";"
        isSLF4JFound = true
      }
      if (line.contains("logback-classic-1.2.3.jar")) {
        filePaths += line + ";"
        isLogBackFound = true
      }

      if (line.contains("logback-core-1.2.3.jar")) {
        filePaths += line + ";"
        isLogBackCoreFound = true
      }
    })
    println("here")
    if (!(isLogBackFound && isSLF4JFound && isLogBackCoreFound)) {
      // Libraries are not found in the project class path, so quit the program.
      println("here")
      return
    }


    filePaths = filePaths.replace("\\", "\\\\")
    val cwd = new File("").getAbsolutePath
    val sourceFilesPrefix = configObject.config.getString("SourceFileRootPath")

    val file1 = configObject.config.getString("File1")
    val file2 = configObject.config.getString("File2")

    // Block to instrument File and Write the instrumented file to disk
    var fileName: String = file1

    var filePath = if (System.getProperty("os.name").contains("Mac")) {
      Paths.get("./", sourceFilesPrefix, fileName)
    } else {
      Paths.get("../", sourceFilesPrefix, fileName)
    }
    var sourceString = IOUtils
      .toString(new FileInputStream(new File(filePath.toString)), "UTF-8")

    var document = runInstrumentation(sourceString)
    var refinedDocument: String = stripPackageName(document.get())

    var NewFilePath = Paths.get(cwd,  File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator, fileName).toString
    var pw = new PrintWriter(new File(NewFilePath))
    pw.write(refinedDocument)
    pw.close

    // Holds list of [variable name, line number, scope and expression] from java source code
    val variablesSet1 = customASTVisitor.getVariableDeclarationContextList


    //Block to instrument file 2 and write to disk
     var fileName2=file2
    filePath = if (System.getProperty("os.name").contains("Mac")) {
      Paths.get("./", sourceFilesPrefix, fileName2)
    } else {
      Paths.get("../", sourceFilesPrefix, fileName2)
    }
    //filePath = Paths.get("././", sourceFilesPrefix, fileName2)
    sourceString = IOUtils
      .toString(new FileInputStream(new File(filePath.toString)), "UTF-8")

    document = runInstrumentation(sourceString)
    refinedDocument = stripPackageName(document.get())

    // Write the instrumented file to disk
    var NewFilePath2 = Paths.get(cwd,  File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator, fileName2).toString
    pw = new PrintWriter(new File(NewFilePath2))
    pw.write(refinedDocument)
    pw.close

    // Holds list of [variable name, line number, scope and expression] from java source code
    val variablesSet2 = customASTVisitor.getVariableDeclarationContextList


    var scopeTable1: Map[String, String] = Map()
    var scopeTable2: Map[String, String] = Map()

    // Get the Line Numbers for the assignment statements using AST
    val result = readLineByLine(NewFilePath)
    // Get the Line Numbers for the assignment statements using AST
    val result2 = readLineByLine(NewFilePath2)

    val classPathParams = filePaths+ Paths.get(cwd,"target","scala-2.13","classes").toString

    import java.nio.file.StandardCopyOption.REPLACE_EXISTING
    var pathObject=Paths.get(cwd,  File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator, "com.instrumentation.Sample1.class").toAbsolutePath


      if (!result.isEmpty) {

        val file = new File(Paths.get(cwd,"target","scala-2.13","classes").toUri)
        val url: URL = file.toURI.toURL

        val urls = Array(url)

        val cl: ClassLoader = new URLClassLoader(urls)

        val cls = cl.loadClass(fileName.split("\\.")(0))

        val params = " 1 2 3 4 5"
        //println("PARAMS "+cls.getClass+" "+y+" "+params+" "+classPathParams )
        var debugger = new Debugger(cls, params, classPathParams)
        result.map(x => x.map(y => {

          debugger.changeValues(y)
          var res: util.Map[LocalVariable, Value] = null
          try {

            res = debugger.getDetails
          } catch {
            case e: Exception => println(e.getMessage)
          }

          // JDI returns runtime values for a given breakpoint.
          if (res != null) {
            if (res.size() > 0) {

              val iterator = res.entrySet().iterator()
              while (iterator.hasNext) {
                val Iteratorkey = iterator.next()
                val key = Iteratorkey.getKey
                val value = Iteratorkey.getValue
                val keyName = key.name()
                val VariableLineNumber = Integer.parseInt(key.toString.split(":")(1)) - 1
                try {
                  scopeTable1 += (keyName -> value.toString)
                }
                catch {
                  case e: Exception => println("Debugger returned entries with exception")
                }
              }
            }
          }


        }))

      }



      if (!result2.isEmpty) {
        val classPathParams = filePaths+ Paths.get(cwd,"target","scala-2.13","classes").toString
        // Spawn the instrumented code , go to the line number, get the value and insert it into the hash table
        // Generate .class file for the instrumented java code. This is an input to JDI Debugger
//        val processBuilder: Process = new ProcessBuilder("javac", "-cp", classPathParams, "\"" + NewFilePath2 + "\"").start()
//        val process = processBuilder.waitFor()
//        import java.io.BufferedReader
//        import java.io.InputStreamReader
//        val output = new BufferedReader(new InputStreamReader(processBuilder.getInputStream))
//        val value = output.readLine
//        println(value)
//        import java.io.BufferedReader
//        import java.io.InputStreamReader
//        val error = new BufferedReader(new InputStreamReader(processBuilder.getErrorStream))
//        val errorString = error.readLine
//        println(errorString)

        val file = new File(Paths.get("target","scala-2.13","classes").toUri)
        val url: URL = file.toURI.toURL
        val urls = Array(url)
        val cl: ClassLoader = new URLClassLoader(urls)

        // Get the class name
        val cls = cl.loadClass(fileName2.split("\\.")(0))


        val params = " 10"

        // For every line number where logging was given, put a breakpoint using jdi and get the variable runtime value
        result2.map(x => x.map(y => {
          var debugger = new Debugger(cls, params, classPathParams)
          debugger.changeValues(y)
          debugger.getDetails
          var res: util.Map[LocalVariable, Value] = null
          try {
            debugger.getDetails
            // val resultArray=ArrayBuffer[]
            res = debugger.getDetails
          } catch {
            case e: Exception => println(e.getMessage)
          }


          if (res != null && res.size() > 0) {

            val iterator = res.entrySet().iterator()
            while (iterator.hasNext) {
              val Iteratorkey = iterator.next()
              val key = Iteratorkey.getKey
              val value = Iteratorkey.getValue
              val keyName = key.name()
              val VariableLineNumber = Integer.parseInt(key.toString.split(":")(1)) - 1
              try {
                scopeTable2 += (keyName -> value.toString)
              } catch {
                case e: Exception => println("Debugger returned entries with exception")
              }
            }
          }
        }
        ))

      }




    // Class to hold variable details
    case class VariableScopeDetails(variableName: String,
                                    scopeNode: ASTNode,
                                    variableDeclarationLocation: Int,
                                    runTimeValue: String
                                   )

    val finalScopeList1 = new mutable.ListBuffer[VariableScopeDetails]()

    // For every variable identified in AST, get its runtime value from JDI result
    variablesSet1 foreach (x => {
      val variableName = x.variableName

      if (scopeTable1.contains(variableName)) {
        val runTimeValue = scopeTable1(variableName)
       finalScopeList1.addOne(new VariableScopeDetails(x.variableName,
          x.scopeNode, x.variableDeclarationLocation, runTimeValue))
      }
    })

    val finalScopeList2 = new mutable.ListBuffer[VariableScopeDetails]()

    variablesSet2 foreach (x => {
      val variableName = x.variableName

      if (scopeTable2.contains(variableName)) {
        val runTimeValue = scopeTable2(variableName)
        finalScopeList2.addOne(new VariableScopeDetails(x.variableName,
          x.scopeNode, x.variableDeclarationLocation, runTimeValue))
      }
    })

    println("VARIABLE SCOPE DETAILS FOR FIRST PROGRAM")
    println(finalScopeList1)
    println("VARIABLE SCOPE DETAILS FOR SECOND PROGRAM")
    println(finalScopeList2)


  }


  // Function to strip the package name and return the code
  def stripPackageName(str: String): String = {

    logger.info("Stripping package name from source code")

    val stream = str.split("\r?\n")
    var substring: String = ""
    stream.foldLeft(0)((i, x) => {
      if (x.asInstanceOf[String].contains("package ")) {
        substring = stream.slice(i + 1, stream.length + 1).mkString("")
      }
      i + 1
    })
    if (substring.equals("")) str
    else substring
  }

  //Function to read the code line by line and get the line numbers where logging is used
  def readLineByLine(filePath: String): Option[ArrayBuffer[Int]] = {


    logger.info("Reading the code line by line to count no. of loggers in the code")


    val contentBuilder = new ArrayBuffer[Int]()

    try {
      val stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8).toArray
      var index = 0
      stream.foldLeft(0)((i, x) => {
        if (x.asInstanceOf[String].contains("logger.info(\"Logging a variable declaration and its value")) {
          contentBuilder += (i + 1)
        }

        i + 1
      })

      Some(contentBuilder)
    } catch {
      case e: Exception => None
    }
  }

  // Initialize AST parser and return a compilation unit for further processing
  def parse(sourceString: Array[Char]): CompilationUnit = {

    logger.info("Parsing the source string")

    val astParser = ASTParser.newParser(AST.JLS8)

    astParser.setKind(ASTParser.K_COMPILATION_UNIT)
    astParser.setSource(sourceString)
    astParser.setResolveBindings(true)
    astParser.setEnvironment(null, null, null, true)
    astParser.setUnitName("sample")
    astParser.createAST(null).asInstanceOf[CompilationUnit]
  }

  // Function to create ast, instrument it and return the ast document
  def runInstrumentation(sourceString: String): Document = {

    logger.info("Running the instrumentation")


    val compilationUnit = parse(sourceString.toCharArray)
    customASTVisitor = new CustomASTVisitor(compilationUnit, ASTRewrite.create(compilationUnit.getAST), true)

    val document = new Document(sourceString)

    compilationUnit.accept(customASTVisitor)
    val edits = customASTVisitor.getASTRewrite.rewriteAST(document, null)
    edits.apply(document)
    document
  }

}

case class InstrumentationDriver()
