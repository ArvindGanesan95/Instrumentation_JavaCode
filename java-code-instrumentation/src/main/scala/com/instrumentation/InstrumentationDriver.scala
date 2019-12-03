package com.instrumentation

import java.io.{File, FileInputStream}

import com.instrumentation.visitor.CustomASTVisitor
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.io.IOUtils
import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import org.eclipse.jface.text.Document

object InstrumentationDriver extends LazyLogging {

  def main(args: Array[String]): Unit = {

    val sourceString = IOUtils
      .toString(new FileInputStream(new File("./java-code-instrumentation/src/main/scala/com/instrumentation/code/Application.java")), "UTF-8")

    val compilationUnit = parse(sourceString.toCharArray)
    val customASTVisitor = new CustomASTVisitor(compilationUnit, ASTRewrite.create(compilationUnit.getAST), true)
    val document = new Document(sourceString)

    compilationUnit.accept(customASTVisitor)
    val edits = customASTVisitor.getASTRewrite.rewriteAST(document, null)
    edits.apply(document)

    val instrumentedCompilationUnit = parse(document.get.toCharArray)
  }

  private def parse(sourceString: Array[Char]) = {

    val astParser = ASTParser.newParser(AST.JLS8)

    astParser.setKind(ASTParser.K_COMPILATION_UNIT)
    astParser.setSource(sourceString)
    astParser.setResolveBindings(true)
    astParser.createAST(null).asInstanceOf[CompilationUnit]
  }

}
