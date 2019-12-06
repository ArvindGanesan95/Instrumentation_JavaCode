package com.instrumentation.visitor

import com.instrumentation.utils.VariableDeclarationContext
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.lang3.StringUtils
import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite

import scala.collection.JavaConverters._
import scala.collection.mutable

// Class to visit AST nodes based on node type
class CustomASTVisitor(compilationUnit: CompilationUnit, astRewrite: ASTRewrite, rewriteMode: Boolean) extends ASTVisitor with LazyLogging {

  // Each of the block of code inserts a logging statement using ASTRewrite node.
  override def visit(node: MethodDeclaration): Boolean = {

    if (rewriteMode) {

      logger.info("Instrumenting method declaration with name : {}", node.getName)


      val ast = node.getAST
      val listRewrite = astRewrite.getListRewrite(node.getBody, Block.STATEMENTS_PROPERTY)
      val methodInvocation = node.getAST.newMethodInvocation

      val qualifiedName = ast.newSimpleName("logger")
      methodInvocation.setExpression(qualifiedName)
      methodInvocation.setName(ast.newSimpleName("info"))

      val stringLiteral: StringLiteral = ast.newStringLiteral
      stringLiteral.setLiteralValue("Logging a method declaration with name :".concat(StringUtils.SPACE).concat(node.getName.toString).concat("()"))

      methodInvocation.arguments.asScala.asInstanceOf[mutable.Buffer[AnyRef]].+=(stringLiteral.asInstanceOf[StringLiteral])

      val statement = node.getAST.newExpressionStatement(methodInvocation)


      listRewrite.insertFirst(statement, null)
    }

    super.visit(node)
  }

  override def visit(node: MethodInvocation): Boolean = {


    if (rewriteMode) {
      logger.info("Instrumenting method invocation with name : {} within parent {}", node.getName, node.getParent.getParent.getNodeType)

      val ast = node.getAST
      val block = VisitorUtils.getParentBlock(node)

      if (node.getName.getIdentifier != "getLogger" && block.getNodeType != ASTNode.COMPILATION_UNIT && block != null) {

        val listRewrite = astRewrite.getListRewrite(block, Block.STATEMENTS_PROPERTY)
        val methodInvocation = node.getAST.newMethodInvocation

        val qualifiedName = ast.newSimpleName("logger")
        methodInvocation.setExpression(qualifiedName)
        methodInvocation.setName(ast.newSimpleName("info"))

        val stringLiteral: StringLiteral = ast.newStringLiteral
        stringLiteral.setLiteralValue("Logging a method invocation with name :".concat(StringUtils.SPACE).concat(node.getName.toString).concat("()"))

        methodInvocation.arguments.asScala.asInstanceOf[mutable.Buffer[AnyRef]].+=(stringLiteral.asInstanceOf[StringLiteral])

        val methodInvokedLine = compilationUnit.getLineNumber(node.getStartPosition)
        val blockStartLine = compilationUnit.getLineNumber(block.getStartPosition)

        val statement = node.getAST.newExpressionStatement(methodInvocation)


        listRewrite.insertAt(statement, methodInvokedLine - blockStartLine - 2, null)

      }
    }
    true
  }

  override def visit(node: VariableDeclarationStatement): Boolean = {

    if (rewriteMode) {

      logger.info("Instrumenting variable declaration statement within parent : {}", node.toString)

      val ast = node.getAST
      val fragment = node.fragments().get(0).asInstanceOf[VariableDeclarationFragment]
      val listRewrite = astRewrite.getListRewrite(node.getParent, Block.STATEMENTS_PROPERTY)
      val methodInvocation = node.getAST.newMethodInvocation

      val qualifiedName = ast.newSimpleName("logger")
      methodInvocation.setExpression(qualifiedName)
      methodInvocation.setName(ast.newSimpleName("info"))

      val stringLiteral: StringLiteral = ast.newStringLiteral
      stringLiteral.setLiteralValue("Logging a variable declaration and its value".concat(StringUtils.SPACE).concat(fragment.getName.toString).concat(StringUtils.SPACE).concat(": {}"))

      val simpleName: SimpleName = ast.newSimpleName(fragment.getName.toString)

      methodInvocation.arguments.asScala.asInstanceOf[mutable.Buffer[AnyRef]].+=(stringLiteral.asInstanceOf[StringLiteral])
      methodInvocation.arguments.asScala.asInstanceOf[mutable.Buffer[AnyRef]].+=(simpleName.asInstanceOf[SimpleName])

      val statement = node.getAST.newExpressionStatement(methodInvocation)

      CustomASTVisitor.variableDeclarationContextList += VariableDeclarationContext(fragment.getName.toString, node.getParent.getParent, compilationUnit.getLineNumber(node.getStartPosition), fragment.getInitializer)

      listRewrite.insertAfter(statement, node, null)

    }

    super.visit(node)
  }


  override def visit(node: ForStatement): Boolean = {

    if (rewriteMode) {

      logger.info("Instrumenting a for statement")

      val ast = node.getAST
      val listRewrite = astRewrite.getListRewrite(node.getParent, Block.STATEMENTS_PROPERTY)
      val methodInvocation = node.getAST.newMethodInvocation

      val qualifiedName = ast.newSimpleName("logger")
      methodInvocation.setExpression(qualifiedName)
      methodInvocation.setName(ast.newSimpleName("info"))

      val stringLiteral: StringLiteral = ast.newStringLiteral
      stringLiteral.setLiteralValue("Logging a for statement")

      methodInvocation.arguments.asScala.asInstanceOf[mutable.Buffer[AnyRef]].+=(stringLiteral.asInstanceOf[StringLiteral])


      val statement = node.getAST.newExpressionStatement(methodInvocation)


      listRewrite.insertBefore(statement, node, null)

    }

    true
  }


  override def visit(node: WhileStatement): Boolean = {

    if (rewriteMode) {

      logger.info("Instrumenting a while statement")

      val ast = node.getAST
      val listRewrite = astRewrite.getListRewrite(node.getParent, Block.STATEMENTS_PROPERTY)
      val methodInvocation = node.getAST.newMethodInvocation

      val qualifiedName = ast.newSimpleName("logger")
      methodInvocation.setExpression(qualifiedName)
      methodInvocation.setName(ast.newSimpleName("info"))

      val stringLiteral: StringLiteral = ast.newStringLiteral
      stringLiteral.setLiteralValue("Logging a while statement")

      methodInvocation.arguments.asScala.asInstanceOf[mutable.Buffer[AnyRef]].+=(stringLiteral.asInstanceOf[StringLiteral])

      val statement = node.getAST.newExpressionStatement(methodInvocation)

      listRewrite.insertBefore(statement, node, null)

    }

    true
  }

  override def visit(node: DoStatement): Boolean = {

    if (rewriteMode) {

      logger.info("Instrumenting a do statement")

      val ast = node.getAST
      val listRewrite = astRewrite.getListRewrite(node.getParent, Block.STATEMENTS_PROPERTY)
      val methodInvocation = node.getAST.newMethodInvocation

      val qualifiedName = ast.newSimpleName("logger")
      methodInvocation.setExpression(qualifiedName)
      methodInvocation.setName(ast.newSimpleName("info"))

      val stringLiteral: StringLiteral = ast.newStringLiteral
      stringLiteral.setLiteralValue("Logging a do statement")

      methodInvocation.arguments.asScala.asInstanceOf[mutable.Buffer[AnyRef]].+=(stringLiteral.asInstanceOf[StringLiteral])

      val statement = node.getAST.newExpressionStatement(methodInvocation)

      listRewrite.insertBefore(statement, node, null)

    }

    true
  }


  override def visit(node: ReturnStatement): Boolean = {

    if (rewriteMode) {
      logger.info("Instrumenting a return statement")

      val ast = node.getAST
      val listRewrite = astRewrite.getListRewrite(node.getParent, Block.STATEMENTS_PROPERTY)
      val methodInvocation = node.getAST.newMethodInvocation

      val qualifiedName = ast.newSimpleName("logger")
      methodInvocation.setExpression(qualifiedName)
      methodInvocation.setName(ast.newSimpleName("info"))

      val stringLiteral: StringLiteral = ast.newStringLiteral
      stringLiteral.setLiteralValue("Logging a return statement")


      methodInvocation.arguments.asScala.asInstanceOf[mutable.Buffer[AnyRef]].+=(stringLiteral.asInstanceOf[StringLiteral])

      val statement = node.getAST.newExpressionStatement(methodInvocation)


      listRewrite.insertBefore(statement, node, null)

    }

    super.visit(node)

  }

  def getASTRewrite: ASTRewrite = astRewrite

  def getVariableDeclarationContextList: mutable.ListBuffer[VariableDeclarationContext] = CustomASTVisitor.variableDeclarationContextList

}

object CustomASTVisitor {

  private val variableDeclarationContextList = new mutable.ListBuffer[VariableDeclarationContext]()
}

object VisitorUtils {

  def getParentBlock(node: ASTNode): ASTNode = {

    if (node == null) null
    else if (node.getNodeType == ASTNode.BLOCK || node.getNodeType == ASTNode.COMPILATION_UNIT) node
    else getParentBlock(node.getParent)
  }

}

