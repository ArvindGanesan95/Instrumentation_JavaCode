package com.instrumentation.utils

import org.eclipse.jdt.core.dom.{ASTNode, Expression}

case class VariableDeclarationContext(variableName: String,
                                       scopeNode: ASTNode,
                                       variableDeclarationLocation: Int,
                                       variableAssignmentExpression: Expression)
