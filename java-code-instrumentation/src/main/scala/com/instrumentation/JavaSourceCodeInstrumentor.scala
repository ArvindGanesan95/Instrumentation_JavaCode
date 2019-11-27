package com.instrumentation

import java.io.{File, FileInputStream}

import com.instrumentation.visitor.CustomASTVisitor
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.io.IOUtils
import org.eclipse.jdt.core.dom.{AST, ASTParser, CompilationUnit}

object JavaSourceCodeInstrumentor extends LazyLogging {

  def main(args: Array[String]): Unit = {

    val sourceString = IOUtils
      .toString(new FileInputStream(new File("./java-code-instrumentation/src/main/scala/com/instrumentation/code/Application.java")), "UTF-8")

    val compilationUnit = parse(sourceString.toCharArray)
    val customASTVisitor = new CustomASTVisitor

    compilationUnit.accept(customASTVisitor)

    customASTVisitor.getMethodInvocationList.foreach {
      x =>
        logger.info("Method invocation : {}", x.getName.getIdentifier)
    }
  }

  private def parse(sourceString: Array[Char]) = {

    val astParser = ASTParser.newParser(AST.JLS8)

    astParser.setKind(ASTParser.K_COMPILATION_UNIT)
    astParser.setSource(sourceString)
    astParser.setResolveBindings(true)
    astParser.createAST(null).asInstanceOf[CompilationUnit]
  }

}
