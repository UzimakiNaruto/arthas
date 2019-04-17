package demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

public class AsmTest implements Opcodes {

  private static int age = 21;
  private String name = "abc";

  private MyClassLoader loader = new MyClassLoader();

  public static String before() throws IOException {
    return "hello";
  }

  @Test
  @Ignore
  public void t() throws IOException {
    ClassReader cr = new ClassReader(AsmTest.class.getName());
    ClassWriter cw = new ClassWriter(0);
    TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out));
    cr.accept(tcv, 0);
  }

  @Test
  @Ignore
  public void trace() throws IOException, NoSuchMethodException {
    String newClassName = AsmTest.class.getName().replaceAll("\\.", "/") + "A";

    ClassReader cr = new ClassReader(AsmTest.class.getName());
    ClassWriter cw = new ClassWriter(0);
    // TraceClassVisitor trace = new TraceClassVisitor(cw, new PrintWriter(System.out));
    ClassVisitor cv = new ClassVisitor(ASM7, cw) {
      @Override
      public void visit(int version, int access, String name, String signature, String superName,
          String[] interfaces) {
        super.visit(version, access, newClassName, signature, superName, interfaces);
      }

      @Override
      public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
          String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        // if ("before".equals(name)) {
        //   return null;
        // }

        return mv;
      }

      @Override
      public void visitEnd() {
        super.visitEnd();
      }
    };

    cr.accept(cv, 0);

    byte[] bytes = cw.toByteArray();
    Class<?> clazz = loader.defineClass(newClassName.replaceAll("/", "."), bytes);
    try {
      Method m = clazz.getDeclaredMethod("before");
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }

  }

  @Test
  // @Ignore
  public void asm()
      throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    CheckClassAdapter cca = new CheckClassAdapter(cw);
    String name = "demo/F";
    cca.visit(V1_8, ACC_PUBLIC, name, null, "demo/AsmTest", null);

    FieldVisitor fv = cca.visitField(ACC_PUBLIC + ACC_STATIC, "age", "I", null, 3);
    fv.visitEnd();

    MethodVisitor mv = cca.visitMethod(ACC_PUBLIC + ACC_STATIC, "<clinit>", "()V", null, null);
    mv.visitCode();
    // mv.visitInsn(ICONST_3);
    mv.visitLdcInsn(123);
    mv.visitFieldInsn(PUTSTATIC, name, "age", "I");
    mv.visitInsn(RETURN);
    mv.visitMaxs(1,1);
    mv.visitEnd();

    cca.visitEnd();

    byte[] bytes = cw.toByteArray();
    // writeToClass(bytes);

    Class<?> clazz = loader.defineClass(name.replaceAll("/", "."), bytes);

    Method m = clazz.getMethod("before");
    m.setAccessible(true);
    Object value = m.invoke(null);
    System.out.println(value);

    Field age = clazz.getField("age");
    System.out.println(age.getInt(null));
  }

  private static void writeToClass(byte[] bytes) throws IOException {
    String home = System.getProperty("user.home");
    File f = new File(home + "/Desktop/1.class");
    if (!f.exists()) {
      boolean newFile = f.createNewFile();
    }

    try (FileOutputStream out = new FileOutputStream(f)) {
      out.write(bytes);
    }
  }
}
