import java.io.{File, FileInputStream}
import java.nio.file.Paths

import com.instrumentation.InstrumentationDriver
import org.apache.commons.io.IOUtils
import org.scalatest.FunSuite

class InstrumentationDriverUnitTest extends FunSuite {

  val filePathPrefix = if (System.getProperty("os.name").contains("Mac")) {
    "./"
  } else {
    "../"
  }
  val cwd = new File("").getAbsolutePath
  val filePath=Paths.get(filePathPrefix,"Instrumenter","src","test","scala","code","Application.java");
  test("The readLineByLine method should return the logging occurrences in the form of an ArrayBuffer") {

    val buffer = InstrumentationDriver.readLineByLine(filePath.toString)

    assert(buffer.get.size === 6)
  }

  test("The stripPackageName method should remove the package declaration in the source code") {

    val sourceString: String = IOUtils
      .toString(new FileInputStream(new File(filePathPrefix + "Instrumenter" + File.separator + "src" + File.separator + "test" + File.separator + "scala" + File.separator + "code" + File.separator +  "Application.java")), "UTF-8")

    val strippedSourceString = InstrumentationDriver.stripPackageName(sourceString)

    assert(sourceString.contains("package"))
    assert(!strippedSourceString.contains("package"))

  }
}
