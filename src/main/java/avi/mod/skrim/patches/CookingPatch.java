package avi.mod.skrim.patches;

import avi.mod.skrim.items.artifacts.FireStaff;
import com.ea.agentloader.AgentLoader;
import com.ea.agentloader.ClassPathUtils;
import javassist.*;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.ItemFood;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

public class CookingPatch {

  private static final String ITEM_FOOD_CLASS = "net.minecraft.item.ItemFood";

  private static final String ON_CREATED = "" +
      "public void onCreated(net.minecraft.item.ItemStack stack, net.minecraft.world.World worldIn, net.minecraft.entity.player" +
      ".EntityPlayer playerIn) {" +
      "  System.out.println(\"SIIIIIIIIIIIIIIIIICK.\");  " +
      "}";

  public static void apply() {
    if (SimpleAgent.class.getClassLoader() != ClassLoader.getSystemClassLoader()) {
      System.out.println("SSSSSSSSSSSSSSSSSSSSSS");
      ClassPathUtils.appendToSystemPath(ClassPathUtils.getClassPathFor(SimpleAgent.class));
      ClassPathUtils.appendToSystemPath(ClassPathUtils.getClassPathFor(ItemFood.class));
      ClassPathUtils.appendToSystemPath(ClassPathUtils.getClassPathFor(EntityCreeper.class));
      ClassPathUtils.appendToSystemPath(ClassPathUtils.getClassPathFor(FireStaff.class));
    }
    AgentLoader.loadAgentClass(SimpleAgent.class.getName(), "hello!");
  }

  public static class SimpleAgent {

    public static void agentmain(String agentArguments,
                                 Instrumentation instrumentation) {
      System.out.println("SimpleAgent");
      System.out.println("retransform: " + instrumentation.isRetransformClassesSupported());
      System.out.println("redefine: " + instrumentation.isRedefineClassesSupported());
      TestTransformer transformer = new TestTransformer();
      instrumentation.addTransformer(transformer);
      try {
        instrumentation.retransformClasses(net.minecraft.item.ItemFood.class);
        instrumentation.retransformClasses(FireStaff.class);
      } catch (UnmodifiableClassException e) {
        e.printStackTrace();
      }
    }
  }

  public static class TestTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
      System.out.println(className);
      if (className.equals("avi/mod/skrim/items/artifacts/FireStaff")) {
        try {
          ClassPool cp = ClassPool.getDefault();
          CtClass fire = ClassPool.getDefault().get("avi.mod.skrim.items.artifacts.FireStaff");
          CtMethod m = fire.getDeclaredMethod("onItemRightClick");
          m.insertBefore("System.out.println(\"what the fuck\");");
          System.out.println("AYOOYOYOH");
          return fire.toBytecode();
        } catch (CannotCompileException | IOException | NotFoundException e) {
          e.printStackTrace();
        }
      }

      if (!className.equals("net/minecraft/item/ItemFood")) return classfileBuffer;
      System.out.println("WE FUCKIN GOT THERE");
      try {
        ClassPool cp = ClassPool.getDefault();
        cp.insertClassPath(new ByteArrayClassPath(className, classfileBuffer));
        CtClass itemFood = ClassPool.getDefault().get(ITEM_FOOD_CLASS);
        CtMethod onCreated = CtMethod.make(ON_CREATED, itemFood);
        itemFood.addMethod(onCreated);
        CtMethod getMax = itemFood.getDeclaredMethod("getMaxItemUseDuration");
        getMax.setBody("return 5;");
        System.out.println("ohfuckingboi");
        return itemFood.toBytecode();
      } catch (CannotCompileException | IOException | NotFoundException e) {
        e.printStackTrace();
      }
      return classfileBuffer;
    }
  }

}
