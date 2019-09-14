package me.hp888.remapper.visitor;

import me.hp888.remapper.api.Remapper;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author hp888 on 14.09.2019.
 */

public class RemapperClassVisitor extends ClassVisitor
{
    private final Remapper remapper;

    public RemapperClassVisitor(final Remapper remapper, final ClassVisitor classVisitor) {
        super(Opcodes.ASM7, classVisitor);
        this.remapper = remapper;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return new RemapperMethodVisitor(remapper, super.visitMethod(access, name, descriptor, signature, exceptions));
    }
}