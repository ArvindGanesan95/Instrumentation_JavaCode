package debugger

import java.io._
import java.util
import com.sun.jdi._
import com.sun.jdi.connect.{IllegalConnectorArgumentsException, VMStartException}
import com.sun.jdi.event.{BreakpointEvent, ClassPrepareEvent, EventSet}


class Debugger(classToDebug:Class[_],lineNumber:Int,params:String,classPathParams:String) {

  var vm:VirtualMachine = _

  @throws[VMStartException]
  @throws[IllegalConnectorArgumentsException]
  @throws[IOException]
  def getDetails: util.Map[LocalVariable, Value] = {
      /*
      * Prepare connector, set class to debug & launch VM.
      */ val launchingConnector = Bootstrap.virtualMachineManager.defaultConnector
      var isBreakPointHit:Boolean=false
      val env = launchingConnector.defaultArguments
      env.get("main").setValue(classToDebug.getName + params)
      val options = env.get("options")
      options.setValue("-cp \"" + classPathParams + "\"")
      vm = launchingConnector.launch(env)
      /*
      * Request VM to trigger event when HelloWorld class is prepared.
      */ val classPrepareRequest = vm.eventRequestManager.createClassPrepareRequest
      classPrepareRequest.addClassFilter(classToDebug.getName)
      classPrepareRequest.enable()
      var eventSet:EventSet = null
      var visibleVariables:util.Map[LocalVariable,Value] = new util.HashMap[LocalVariable,Value]()
    try while ( { (eventSet = vm.eventQueue.remove()) != null  })
    {
      import scala.collection.JavaConversions._
      for (event <- eventSet) {
        /*
          * If this is ClassPrepareEvent, then set breakpoint
          */ if (event.isInstanceOf[ClassPrepareEvent]) {
          val evt = event.asInstanceOf[ClassPrepareEvent]
          val classType = evt.referenceType.asInstanceOf[ClassType]
          val location = classType.locationsOfLine(lineNumber).get(0)
          val bpReq = vm.eventRequestManager.createBreakpointRequest(location)
          bpReq.enable()
          }
        /*
         * If this is BreakpointEvent, return the variables alive at that breakpoint
         */ if (event.isInstanceOf[BreakpointEvent]) { // disable the breakpoint event
        event.request.disable()
        // Get values of all variables that are visible and print
        val stackFrame = event.asInstanceOf[BreakpointEvent].thread.frame(0)
        visibleVariables = stackFrame.getValues(stackFrame.visibleVariables).asInstanceOf[util.Map[LocalVariable, Value]]
        System.out.println("Local Variables =")
        import scala.collection.JavaConversions._
        for (entry <- visibleVariables.entrySet) {
         System.out.println("	" + entry.getKey.name + " = " + entry.getValue)
          visibleVariables.put(entry.getKey,entry.getValue)
        }
          isBreakPointHit=true
        }
        vm.resume()
      }
      if(isBreakPointHit) return visibleVariables
    }
    catch {
      case e: VMDisconnectedException =>
        System.out.println("VM is now disconnected.")
        return null
      case e: Exception =>
        e.printStackTrace()
        return null
    }
    visibleVariables
  }
}