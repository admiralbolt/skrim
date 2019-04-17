package avi.mod.skrim.patches;

import java.lang.instrument.Instrumentation;

public class NameCheckerAgent {

  public static final String AUTHORIZATION_AGENT_PARAM = "testagent";

  public static void premain(String agentArgs, Instrumentation inst) {
    if (agentArgs != null &&
        AUTHORIZATION_AGENT_PARAM.equalsIgnoreCase(agentArgs.trim())) {
      inst.addTransformer(new CheckNameTransformer());
    }
  }

  static public boolean check(String name) throws Exception {
    System.out.println("Agent.check : "+ name);
    if(name.equals("Dr.No")) throw new Exception("Wrong Name! "+ name);
    return true;
  }
}