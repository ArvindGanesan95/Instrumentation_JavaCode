package com.instrumentation.visitor

import org.eclipse.jdt.core.dom.{VariableDeclarationExpression, _}

import scala.collection.mutable._

class CustomASTVisitor extends ASTVisitor {

  override def visit(node: MethodDeclaration): Boolean = {
    CustomASTVisitor.methodDeclarationList += node
    true
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

