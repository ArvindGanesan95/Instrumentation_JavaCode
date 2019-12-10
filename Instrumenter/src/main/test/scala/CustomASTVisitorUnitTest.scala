import java.io.{File, FileInputStream}

import com.instrumentation.InstrumentationDriver
import com.instrumentation.visitor.{CustomASTVisitor, VisitorUtils}
import org.apache.commons.io.IOUtils
import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import org.scalatest.FunSuite

class CustomASTVisitorUnitTest extends FunSuite {

  val sourceString: String = IOUtils
    .toString(new FileInputStream(new File("./Instrumenter/src/main/test/scala/code/Application.java")), "UTF-8")

  val compilationUnit = InstrumentationDriver.parse(sourceString.toCharArray)
  val customASTVisitor = new CustomASTVisitor(compilationUnit, ASTRewrite.create(compilationUnit.getAST), true)


  test("The visit method to traverse variable declaration statements should return all such values") {

    compilationUnit.accept(customASTVisitor)
    val list = customASTVisitor.getVariableDeclarationContextList

    assert(list.length === 2)
    assert(list.head.variableName === "a")
    assert(list(1).variableName === "b")
  }

  test("The getParentBlock method should return the enclosing block of a given AST node") {

    compilationUnit.accept(customASTVisitor)
    val list = customASTVisitor.getVariableDeclarationContextList

    val result = VisitorUtils.getParentBlock(list.head.scopeNode)

    assert(result.getNodeType === ASTNode.COMPILATION_UNIT)
  }
}
