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
import java.util.ArrayList;
import java.util.List;
import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;

public class Agent {
    static List<String> methodsLog = new ArrayList<>();
    static List<String> paramsLog = new ArrayList<>();
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
                if (methodsLog.contains(name)) {
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

            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();

        for (int i = 0; i < methodsLog.size() ; i++) {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, methodsLog.get(i), paramsLog.get(i), null, null);

            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

            if (paramsLog.get(i).contains("L")) {
                mv.visitVarInsn(Opcodes.ALOAD, 1);
            } else if (paramsLog.get(i).contains("I")) {
                mv.visitVarInsn(Opcodes.ILOAD, 1);
            } else if (paramsLog.get(i).contains("D")) {
                mv.visitVarInsn(Opcodes.DLOAD, 1);
            }

            mv.visitInvokeDynamicInsn("makeConcatWithConstants", paramsLog.get(i).substring(0, paramsLog.get(i).length() - 1) + "Ljava/lang/String;",
                    handle, "executed method: " + methodsLog.get(i) + "-param: \u0001");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv.visitVarInsn(Opcodes.ALOAD, 0);

            if (paramsLog.get(i).contains("L")) {
                mv.visitVarInsn(Opcodes.ALOAD, 1);
            } else if (paramsLog.get(i).contains("I")) {
                mv.visitVarInsn(Opcodes.ILOAD, 1);
            } else if (paramsLog.get(i).contains("D")) {
                mv.visitVarInsn(Opcodes.DLOAD, 1);
            }

            if (methodsLog.get(i).contains("Log")) {
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "ru/otus/homework/Logging",
                        methodsLog.get(i).substring(0, methodsLog.get(i).length() - 3),
                        paramsLog.get(i), false);
            }

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
                    methodsLog.add(nameMethod);
                    paramsLog.add(descriptorMethod);
                }
                return super.visitAnnotation(descriptor, visible);
            }
        }
    }
}