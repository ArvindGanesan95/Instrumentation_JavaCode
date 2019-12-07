package com.instrumentation

import java.io.{File, FileInputStream, PrintWriter}
import java.nio.file.Paths

import com.instrumentation.visitor.CustomASTVisitor
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import debugger.Debugger
import org.apache.commons.io.IOUtils
import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import org.eclipse.jface.text.Document

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object configObject {
  val config = ConfigFactory.load()
}

object InstrumentationDriver extends LazyLogging {
  var customASTVisitor: CustomASTVisitor = null

  def main(args: Array[String]): Unit = {

    // Get the list of libraries used in the programs current classpath
    val list = System.getProperty("java.class.path").split(";")
    var isLogBackFound = false
    var isSLF4JFound = false
    var isLogBackCoreFound = false
    var filePaths: String = ""

    // Check if the following libraries are available. Its required for the current program
    // as well as for instrumenting the java codes
    list.foreach((line: String) => {
      if (line.contains("slf4j-api-1.7.25.jar")) {
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

    if (!(isLogBackFound && isSLF4JFound && isLogBackCoreFound)) {
      // Libraries are not found in the project class path, so quit the program.
      System.exit(1)
    }
    import scala.collection.JavaConversions._

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

    var NewFilePath = Paths.get(cwd, "java-code-instrumentation"+ File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator, fileName).toString
    var pw = new PrintWriter(new File(NewFilePath))
    pw.write(refinedDocument)
    pw.close

    // Holds list of [variable name, line number, scope and expression] from java source code
    val variablesSet1 = customASTVisitor.getVariableDeclarationContextList


    //Block to instrument file 2 and write to disk

    var fileName2 = file2
    filePath = Paths.get("././", sourceFilesPrefix, fileName2)
    sourceString = IOUtils
      .toString(new FileInputStream(new File(filePath.toString)), "UTF-8")

    document = runInstrumentation(sourceString)
    refinedDocument = stripPackageName(document.get())

    // Write the instrumented file to disk
    var NewFilePath2 = Paths.get(cwd, "java-code-instrumentation"+ File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator, fileName2).toString
    pw = new PrintWriter(new File(NewFilePath2))
    pw.write(refinedDocument)
    pw.close

    // Holds list of [variable name, line number, scope and expression] from java source code
    val variablesSet2 = customASTVisitor.getVariableDeclarationContextList


    var scopeTable1: Map[String, String] = Map()
    var scopeTable2: Map[String, String] = Map()

    // Get the Line Numbers for the assignment statements using AST
    val result = readLineByLine(NewFilePath)
    if (!result.isEmpty) {
      val classPathParams = filePaths + configObject.config.getString("ClassFilesPath")
      val process: Process = new ProcessBuilder("javac", "-cp", classPathParams, "\"" + NewFilePath + "\"").start
      val exitCode = process.waitFor
      // Spawn the instrumented code , go to the line number, get the value and insert it into the hash table
      Thread.sleep(5000)


      val cls = Class.forName(fileName.split("\\.")(0))
      result.map(x => x.map(y => {


        val params = " 1 2 3 4 5"
        var debugger = new Debugger(cls, y, params, classPathParams)
        var res = debugger.getDetails

        // JDI returns runtime values for a given breakpoint.
        if (res.size() > 0) {

          val iterator = res.entrySet().iterator()
          while (iterator.hasNext) {
            val Iteratorkey = iterator.next()
            val key = Iteratorkey.getKey
            val value = Iteratorkey.getValue
            val keyName = key.name()
            val VariableLineNumber = Integer.parseInt(key.toString.split(":")(1)) - 1
            scopeTable1 += (keyName -> value.toString)
          }
        }
      }
      ))

    }


    // Get the Line Numbers for the assignment statements using AST
    val result2 = readLineByLine(NewFilePath2)
    if (!result2.isEmpty) {
      val classPathParams = filePaths + configObject.config.getString("ClassFilesPath")
      // Spawn the instrumented code , go to the line number, get the value and insert it into the hash table
      // Generate .class file for the instrumented java code. This is an input to JDI Debugger
      val process: Process = new ProcessBuilder("javac", "-cp", classPathParams, "\"" + NewFilePath2 + "\"").start
      val exitCode = process.waitFor
      // Get the class name
      val cls: Class[_] = Class.forName(fileName2.split("\\.")(0))

      // For every line number where logging was given, put a breakpoint using jdi and get the variable runtime value
      result2.map(x => x.map(y => {
        val classPathParams = filePaths + configObject.config.getString("ClassFilesPath")
        val params = " 10"
        var debugger = new Debugger(cls, y, params, classPathParams)
        var res = debugger.getDetails

        if (res.size() > 0) {

          val iterator = res.entrySet().iterator()
          while (iterator.hasNext) {
            val Iteratorkey = iterator.next()
            val key = Iteratorkey.getKey
            val value = Iteratorkey.getValue
            val keyName = key.name()
            val VariableLineNumber = Integer.parseInt(key.toString.split(":")(1)) - 1
            scopeTable2 += (keyName -> value.toString)
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
        finalScopeList1.add(new VariableScopeDetails(x.variableName,
          x.scopeNode, x.variableDeclarationLocation, runTimeValue))
      }
    })

    val finalScopeList2 = new mutable.ListBuffer[VariableScopeDetails]()

    variablesSet2 foreach (x => {
      val variableName = x.variableName

      if (scopeTable2.contains(variableName)) {
        val runTimeValue = scopeTable2(variableName)
        finalScopeList2.add(new VariableScopeDetails(x.variableName,
          x.scopeNode, x.variableDeclarationLocation, runTimeValue))
      }
    })

    println("VARIABLE SCOPE DETAILS FOR FIRST PROGRAM")
    println(finalScopeList1)
    println("VARIABLE SCOPE DETAILS FOR SECOND PROGRAM")
    println(finalScopeList2)

    // Write the final results to the disk

  }

  import java.nio.charset.StandardCharsets
  import java.nio.file.{Files, Paths}

  // Function to strip the package name and return the code
  def stripPackageName(str: String): String = {
    val stream = str.split("\n")
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

    val compilationUnit = parse(sourceString.toCharArray)
    customASTVisitor = new CustomASTVisitor(compilationUnit, ASTRewrite.create(compilationUnit.getAST), true)

    val document = new Document(sourceString)

    compilationUnit.accept(customASTVisitor)
    val edits = customASTVisitor.getASTRewrite.rewriteAST(document, null)
    edits.apply(document)
    document
  }

}
