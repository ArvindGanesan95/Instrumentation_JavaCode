package com.instrumentation.visitor

import org.apache.commons.lang3.StringUtils
import org.eclipse.jdt.core.dom.{VariableDeclarationExpression, _}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable._


class CustomASTVisitor extends ASTVisitor {

  override def visit(node: MethodDeclaration): Boolean = {
    CustomASTVisitor.methodDeclarationList += node

    val ast = node.getAST
    val methodInvocation = ast.newMethodInvocation

    val qualifiedName = ast.newQualifiedName(ast.newSimpleName("System"), ast.newSimpleName("out"))
    methodInvocation.setExpression(qualifiedName)
    methodInvocation.setName(ast.newSimpleName("println"))

    val stringLiteral: StringLiteral = ast.newStringLiteral
    stringLiteral.setLiteralValue("Logging from within the" + StringUtils.SPACE + node.getName.toString + StringUtils.SPACE + "method")

    methodInvocation.arguments.asScala.asInstanceOf[mutable.Buffer[AnyRef]].+=(stringLiteral.asInstanceOf[StringLiteral])
    node.getBody.statements.asScala.asInstanceOf[mutable.Buffer[AnyRef]].+=(ast.newExpressionStatement(methodInvocation))

    super.visit(node)
  }

  override def visit(node: MethodInvocation): Boolean = {
    CustomASTVisitor.methodInvocationList += node
    true
  }

  override def visit(node: VariableDeclarationExpression): Boolean = {
    CustomASTVisitor.variableDeclarationExpressionList += node
    true
  }

  def getMethodDeclarationList: ListBuffer[MethodDeclaration] = CustomASTVisitor.methodDeclarationList

  def getMethodInvocationList: ListBuffer[MethodInvocation] = CustomASTVisitor.methodInvocationList

  def getVariableDeclarationExpressionList: ListBuffer[VariableDeclarationExpression] = CustomASTVisitor.variableDeclarationExpressionList

}

object CustomASTVisitor {

  private val methodDeclarationList = ListBuffer[MethodDeclaration]()
  private val methodInvocationList = ListBuffer[MethodInvocation]()
  private val variableDeclarationExpressionList = ListBuffer[VariableDeclarationExpression]()

}

