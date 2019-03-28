package avi.mod.skrim.patches;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckNameTransformer implements ClassFileTransformer {
  byte[] transformedClassByteCode = null;

  @Override
  public byte[] transform(ClassLoader classLoader, String className,
                          Class<?> classBeingRedefined,
                          ProtectionDomain protectionDomain,
                          byte[] classFileBuffer) throws IllegalClassFormatException {

    byte[] byteCode = classFileBuffer;

    if (className.equals("javaagenttest/NameChecker")) {
      try {
        ClassPool cp = ClassPool.getDefault();
        String curClassName = className.replaceAll("/", ".");

        CtClass curClass = cp.get(curClassName);
        CtMethod checkMethod = null ;
        CtClass strClass = cp.get(String.class.getCanonicalName()) ;
        CtClass[] paramArgs  = new CtClass[] { strClass } ;

        checkMethod = curClass.getDeclaredMethod("check", paramArgs);
        checkMethod.insertBefore("{if (javaagenttest.NameCheckerAgent.check($1) ){ /*System.out.println(\"Transformer\");*/ return false; } }");

        return curClass.toBytecode();
      } catch (NotFoundException | CannotCompileException | IOException ex) {
        Logger.getLogger(CheckNameTransformer.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return byteCode;
  }
}