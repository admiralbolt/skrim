package avi.mod.skrim.patches;

import javassist.*;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.MethodInfo;

import java.io.IOException;

/**
 * Hey future me, here's what you're looking for:
 * https://web.archive.org/web/20141014195801/http://dhruba.name/2010/02/07/creation-dynamic-loading-and-instrumentation-with-javaagents/
 * https://henning.kropponline.de/2015/08/02/a-java-agent-example-javaagent/
 */

public class TestPatch {

  public static void go() {
    System.out.println("MODIFYING CREEPER");
    try {
      ClassPool pool = ClassPool.getDefault();
      CtClass creeper = pool.get("net.minecraft.entity.monster.EntityCreeper");
      System.out.println("creeper: " + creeper.getName());
      CtMethod m = creeper.getDeclaredMethod("explode");
      System.out.println("boom: " + m.getLongName());
      m.insertBefore("{ System.out.println('YOOOOOOOOO'); }");
      creeper.writeFile();
    } catch (NotFoundException | CannotCompileException | IOException e) {
      e.printStackTrace();
    }
  }
}
