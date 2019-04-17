package demo;

public class MyClassLoader extends ClassLoader {

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    return super.findClass(name);
  }

  public Class<?> defineClass(String className, byte[] bytes) {
    return defineClass(className, bytes, 0, bytes.length);
  }
}
