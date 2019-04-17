package demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AsmTest implements Opcodes {

  private static int age = 21;
  private String name = "abc";

  public static String before() throws IOException {
    return "hello";
  }

  @Test
  public void asm()
      throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    // ClassReader cr = new ClassReader(AsmTest.class.getName());
    // cr.accept(new ClassVisitor(ASM7) {
    //   @Override
    //   public void visit(int version, int access, String name, String signature, String superName,
    //       String[] interfaces) {
    //     super.visit(version, access, name, signature, superName, interfaces);
    //   }
    //
    //   @Override
    //   public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
    //       String[] exceptions) {
    //
    //     if ("before".equals(name)) {
    //
    //       MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
    //
    //       if (mv != null) {
    //         System.out.println("can control");
    //       }
    //       return mv;
    //     }
    //
    //     return super.visitMethod(access, name, descriptor, signature, exceptions);
    //   }
    //
    //   @Override
    //   public void visitEnd() {
    //
    //     super.visitEnd();
    //   }
    // }, 0);

    ClassWriter cw = new ClassWriter(2);
    String name = "demo/F";
    cw.visit(V1_8, ACC_PUBLIC, name, null, "demo/AsmTest", null);

    FieldVisitor fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, "age", "I", null, 3);
    fv.visitEnd();

    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<clinit>", "()V", null, null);
    mv.visitCode();
    mv.visitInsn(ICONST_3);
    mv.visitFieldInsn(PUTSTATIC, name, "age", "I");
    mv.visitEnd();
    cw.visitEnd();

    byte[] bytes = cw.toByteArray();
    writeToClass(bytes);

    MyClassLoader loader = new MyClassLoader();
    Class<?> clazz = loader.defineClass(name.replaceAll("/", "."), bytes);
    Method m = clazz.getDeclaredMethod("before");
    m.setAccessible(true);
    Object value = m.invoke(null);
    System.out.println(value);
  }

  private static void writeToClass(byte[] bytes) throws IOException {
    String home = System.getProperty("user.home");
    File f = new File(home + "/Desktop/1.class");
    if (!f.exists()) {
      boolean newFile = f.createNewFile();
    }

    try(FileOutputStream out = new FileOutputStream(f)) {
      out.write(bytes);
    }
  }
}
