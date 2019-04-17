package avi.mod.skrim.patches;

import java.lang.instrument.Instrumentation;

public class TestAgent {

  private static Instrumentation instrumentation;

  /**
   * JVM hook to statically load the javaagent at startup.
   * <p>
   * After the Java Virtual Machine (JVM) has initialized, the premain method
   * will be called. Then the real application main method will be called.
   *
   * @param args
   * @param inst
   * @throws Exception
   */
  public static void premain(String args, Instrumentation inst) throws Exception {
    System.out.println("premain method invoked with: " + args + ", inst: " + inst);
    instrumentation = inst;
    // instrumentation.addTransformer(new MyClassFileTransformer());
  }

  /**
   * JVM hook to dynamically load javaagent at runtime.
   * <p>
   * The agent class may have an agentmain method for use when the agent is
   * started after VM startup.
   *
   * @param args
   * @param inst
   * @throws Exception
   */
  public static void agentmain(String args, Instrumentation inst) throws Exception {
    System.out.println("AGENTMAIN method invoked with: " + args + ", inst: " + inst);
    instrumentation = inst;
    // instrumentation.addTransformer(new MyClassFileTransformer());
  }

  /**
   * Programmatic hook to dynamically load javaagent at runtime.
   */
  public static void initialize() {
    if (instrumentation == null) {
      // MyJavaAgentLoader.loadAgent();
    }
  }

}