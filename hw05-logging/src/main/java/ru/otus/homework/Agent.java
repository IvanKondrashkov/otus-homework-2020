package ru.otus.homework;

import org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;

public class Agent {
    static Map<String, String> mapMethodsLog = new HashMap<>();
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain");
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className,
                                    Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain,
                                    byte[] classfileBuffer) {

                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
                ClassVisitor cv = new AnnotationScanner(Opcodes.ASM8, cw);
                cr.accept(cv, 0);

                if (className.equals("ru/otus/homework/Logging")) {
                    return addProxyMethod(classfileBuffer);
                }
                return classfileBuffer;
            }
        });
    }

    private static byte[] addProxyMethod(byte[] originalClass) {
        ClassReader cr = new ClassReader(originalClass);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new AnnotationScanner(Opcodes.ASM8, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (mapMethodsLog.containsKey(name)) {
                    return super.visitMethod(access, name + "Log", descriptor, signature, exceptions);
                } else {
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
            }
        };
        cr.accept(cv, Opcodes.ASM8);

        Handle handle = new Handle(
                H_INVOKESTATIC,
                Type.getInternalName(java.lang.invoke.StringConcatFactory.class),
                "makeConcatWithConstants",
                MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class, Object[].class).toMethodDescriptorString(),
                false);

        for (Map.Entry<String, String> pair : mapMethodsLog.entrySet()) {
            String keyName = pair.getKey();
            String valueDesc = pair.getValue();

            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, keyName, valueDesc, null, null);

            Type type = Type.getType(valueDesc);
            int opcodes;

            if (keyName.contains("operation")) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitVarInsn(Opcodes.ALOAD, 1);
                mv.visitInvokeDynamicInsn("makeConcatWithConstants", "(Ljava/lang/String;)Ljava/lang/String;", handle, "executed method: " + keyName + "-param: \u0001");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitVarInsn(Opcodes.ALOAD, 1);

            } else if (keyName.contains("calculation")) {
                opcodes = type.getOpcode(Opcodes.ILOAD);
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitVarInsn(opcodes, 1);
                mv.visitInvokeDynamicInsn("makeConcatWithConstants", "(I)Ljava/lang/String;", handle, "executed method: " + keyName + "-param: \u0001");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                mv.visitVarInsn(opcodes, 0);
                mv.visitVarInsn(opcodes, 1);

            }

            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "ru/otus/homework/Logging", keyName, valueDesc, false);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        byte[] finalClass = cw.toByteArray();

        try (OutputStream out = new FileOutputStream("proxyASM.class")) {
            out.write(finalClass);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return finalClass;
    }

    private static class AnnotationScanner extends ClassVisitor {
        private String nameMethod;
        private String descriptorMethod;

        public AnnotationScanner(int api) {
            super(api);
        }

        public AnnotationScanner(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            this.nameMethod = name;
            this.descriptorMethod = descriptor;
            return new AnnotationScanner.MethodAnnotationScanner();
        }

        class MethodAnnotationScanner extends MethodVisitor {

            public MethodAnnotationScanner() {
                super(Opcodes.ASM8);
            }

            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                if (descriptor.contains("Lannotation/Log;")) {
                    mapMethodsLog.put(nameMethod, descriptorMethod);
                }
                return super.visitAnnotation(descriptor, visible);
            }
        }
    }
}