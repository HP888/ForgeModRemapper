package me.hp888.remapper.visitor;

import me.hp888.remapper.api.Remapper;
import me.hp888.remapper.api.mappings.MappingType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author hp888 on 14.09.2019.
 */

public class RemapperMethodVisitor extends MethodVisitor
{
    private final Remapper remapper;

    RemapperMethodVisitor(final Remapper remapper, final MethodVisitor methodVisitor) {
        super(Opcodes.ASM7, methodVisitor);
        this.remapper = remapper;
    }

    @Override
    public void visitLdcInsn(Object value) {
        if (!(value instanceof String) || (!((String) value).startsWith("func_") || (!((String) value).startsWith("field_")))) {
            super.visitLdcInsn(value);
            return;
        }

        final String ldc = (String) value;
        remapper.getMapping(remapper.getMappingType((String) value), ldc).ifPresentOrElse(mappingObject -> {
            remapper.getLogger().info("Renamed " + mappingObject.getObfuscatedName() + " -> " + mappingObject.getOriginalName());
            super.visitLdcInsn(mappingObject.getOriginalName());
        }, () -> {
            remapper.getLogger().warning("Mapping for string \"" + ldc + "\" not found, are you typed correct minecraft version in configuration?");
            super.visitLdcInsn(ldc);
        });
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (!name.startsWith("func_")) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            return;
        }

        remapper.getMapping(MappingType.METHODS, name).ifPresentOrElse(mappingObject -> {
            remapper.getLogger().info("Renamed " + mappingObject.getObfuscatedName() + " -> " + mappingObject.getOriginalName());
            super.visitMethodInsn(opcode, owner, mappingObject.getOriginalName(), descriptor, isInterface);
        }, () -> super.visitMethodInsn(opcode, owner, name, descriptor, isInterface));
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (!name.startsWith("field_")) {
            super.visitFieldInsn(opcode, owner, name, descriptor);
            return;
        }

        remapper.getMapping(MappingType.METHODS, name).ifPresentOrElse(mappingObject -> {
            remapper.getLogger().info("Renamed " + mappingObject.getObfuscatedName() + " -> " + mappingObject.getOriginalName());
            super.visitFieldInsn(opcode, owner, mappingObject.getOriginalName(), descriptor);
        }, () -> super.visitFieldInsn(opcode, owner, name, descriptor));
    }
}