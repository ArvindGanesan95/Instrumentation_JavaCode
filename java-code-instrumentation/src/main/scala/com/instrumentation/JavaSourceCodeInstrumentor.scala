package com.instrumentation

import java.io.{File, FileInputStream}

import org.apache.commons.io.IOUtils
import org.eclipse.jdt.core.dom.{AST, ASTParser, CompilationUnit}

object JavaSourceCodeInstrumentor {

  def main(args: Array[String]): Unit = {

    val sourceString = IOUtils
      .toString(new FileInputStream(new File("./java-code-instrumentation/src/main/scala/com/instrumentation/code/Application.java")), "UTF-8")

    val compilationUnit = parse(sourceString.toCharArray)
  }

  private def parse(sourceString: Array[Char]) = {

    val astParser = ASTParser.newParser(AST.JLS8)

    astParser.setKind(ASTParser.K_COMPILATION_UNIT)
    astParser.setSource(sourceString)
    astParser.setResolveBindings(true)
    astParser.createAST(null).asInstanceOf[CompilationUnit]
  }

}
