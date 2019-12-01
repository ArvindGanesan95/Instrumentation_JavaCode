import java.io.File
import java.nio.file.{Files, Paths}

import jdk.nashorn.internal.ir.BlockStatement
import org.eclipse.jdt.core.dom.{AST, ASTParser, ASTVisitor, Block, CompilationUnit,
  DoStatement, FieldDeclaration, ForStatement, MethodDeclaration,
  Type, TypeDeclaration, VariableDeclarationFragment, VariableDeclarationStatement,
  WhileStatement, IfStatement}



object Parser{
  var cunit:CompilationUnit=null
  var scopeTable : List[(String,String,String,String,String)] = List()
  def main(args:Array[String]):Unit={
      val cunit:CompilationUnit=createParser();
      val visitor:Visitor=new Visitor
      cunit.accept(visitor)

      visitor.getScopeTable.foreach((a:Any)=>{
        if (a != null) {println(a)}
      })

}
// This table will include the path to a variable, its declaration in the line of the code,
// and each variable will be assigned a unique identifier.
  def createParser():CompilationUnit={
      val parser: ASTParser = ASTParser.newParser(AST.JLS8)
      parser.setKind(ASTParser.K_COMPILATION_UNIT)
      val resource:File = new File("G:\\OOLE Course\\Course Project\\src\\main\\resources\\Snippet.java")
      val sourcePath:java.nio.file.Path  = Paths.get(resource.toURI())
      val sourceString=new String(Files.readAllBytes(sourcePath));
      val source:Array[Char]=sourceString.toCharArray
      parser.setEnvironment(null, null, null, true)
      parser.setUnitName("sample")
      parser.setSource(source)
      parser.setResolveBindings(true)
      parser.setBindingsRecovery(true)
      cunit=parser.createAST(null).asInstanceOf[CompilationUnit]
      cunit
  }
}

import java.util

class Visitor extends ASTVisitor {
  val methods = new util.ArrayList[MethodDeclaration]


  override def visit(typeNote: TypeDeclaration): Boolean = {
    //System.out.println("------------------parent-----------")

    val list:util.List[_] = typeNote.superInterfaceTypes
    import scala.collection.JavaConverters._
    val fields:Array[FieldDeclaration]=typeNote.getFields
    fields.foreach((field:FieldDeclaration)=>{
      val f:util.List[_]=field.fragments()
      val iterator=f.iterator()
      while(iterator.hasNext)
      {
        val g=iterator.next().asInstanceOf[VariableDeclarationFragment]
        val binding=g.resolveBinding()
        var lineNumber= Parser.cunit.getLineNumber(Parser.cunit.findDeclaringNode(binding.getKey)
          .getStartPosition-1)
        var enclosingType="Class"
        if(typeNote.isInterface){
          enclosingType="Interface"
        }

        Parser.scopeTable=Parser.scopeTable:+((binding.getName,enclosingType,binding.getVariableId.toString,"LineNumber:"+lineNumber,"Class/Interface"))

      }

    })

    return  true
  }


  override def visit(v: VariableDeclarationStatement): Boolean = {

    import org.eclipse.jdt.core.dom.VariableDeclarationFragment
    val iter = v.fragments.iterator
    val fragment = iter.next.asInstanceOf[VariableDeclarationFragment]
    val binding = fragment.resolveBinding
    if(v.getParent.isInstanceOf[Block]){
      var lineNumber= Parser.cunit.getLineNumber(Parser.cunit.findDeclaringNode(binding.getKey)
        .getStartPosition-1)

      if(v.getParent.getParent.isInstanceOf[IfStatement]){
        Parser.scopeTable=Parser.scopeTable:+(("VNAME:"+binding.getName,binding.getDeclaringMethod.getName,"ID:"+binding.getVariableId.toString,"LineNumber:"+lineNumber,"IfStatement"))
      }

      //Check if the block statement is of For,Do,While or Method
      if(v.getParent.getParent.isInstanceOf[WhileStatement]){
        Parser.scopeTable=Parser.scopeTable:+(("VNAME:"+binding.getName,"METHOD:"+binding.getDeclaringMethod.getName,"ID:"+binding.getVariableId.toString,"LineNumber:"+lineNumber,"WhileStatement"))
      }
      else if(v.getParent.getParent.isInstanceOf[DoStatement]){
        Parser.scopeTable=Parser.scopeTable:+(("VNAME:"+binding.getName,"METHOD:"+binding.getDeclaringMethod.getName,"ID:"+binding.getVariableId.toString,"LineNumber:"+lineNumber,"DoWhileStatement"))
      }
     else  if(v.getParent.getParent.isInstanceOf[ForStatement]){
        Parser.scopeTable=Parser.scopeTable:+(("VNAME:"+binding.getName,"METHOD:"+binding.getDeclaringMethod.getName,"ID:"+binding.getVariableId.toString,"LineNumber:"+lineNumber,"ForStatement"))
      }
      else if(v.getParent.getParent.isInstanceOf[MethodDeclaration]){
        Parser.scopeTable=Parser.scopeTable:+(("VNAME:"+binding.getName,"METHOD:"+binding.getDeclaringMethod.getName,"ID:"+binding.getVariableId.toString,"LineNumber:"+lineNumber,"MethodDeclaration"))
      }
    }
    true
  }

  def getScopeTable: List[_] = Parser.scopeTable
}