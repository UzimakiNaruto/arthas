package demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.TraceClassVisitor;

public class AsmTreeTest implements Opcodes {

  @Test
  public void testClassNode() throws IOException {
    ClassReader cr = new ClassReader(MyClassLoader.class.getName());
    ClassNode cn = new ClassNode(ASM6);
    cr.accept(cn, 0);

    Iterator<MethodNode> it = cn.methods.iterator();
    while (it.hasNext()) {
      MethodNode methodNode = it.next();
      if ("defineClass".equals(methodNode.name) && "(Ljava/lang/String;[B)Ljava/lang/Class;".equals(methodNode.desc)) {
        it.remove();
        break;
      }
    }

    for (MethodNode m : cn.methods) {
      System.out.println(m.name + " " + m.desc);
    }

    TraceClassVisitor p = new TraceClassVisitor(new PrintWriter(System.out));
    cn.accept(p);
  }
}
