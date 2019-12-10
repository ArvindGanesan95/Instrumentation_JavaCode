import java.io.{File, FileInputStream}

import com.instrumentation.InstrumentationDriver
import org.apache.commons.io.IOUtils
import org.scalatest.FunSuite

class InstrumentationDriverUnitTest extends FunSuite {

  test("The readLineByLine method should return the logging occurrences in the form of an ArrayBuffer") {

    val buffer = InstrumentationDriver.readLineByLine("./Instrumenter/src/main/test/scala/code/Application.java")

    assert(buffer.get.size === 2)
  }

  test("The stripPackageName method should remove the package declaration in the source code") {

    val sourceString: String = IOUtils
      .toString(new FileInputStream(new File("./Instrumenter/src/main/test/scala/code/Application.java")), "UTF-8")

    val strippedSourceString = InstrumentationDriver.stripPackageName(sourceString)

    assert(sourceString.contains("package"))
    assert(!strippedSourceString.contains("package"))
  }
}
