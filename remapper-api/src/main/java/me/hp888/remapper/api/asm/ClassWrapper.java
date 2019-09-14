package me.hp888.remapper.api.asm;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

@Data
@AllArgsConstructor
public class ClassWrapper
{
    private ClassNode classNode;

    public byte[] toByteArray() {
        final ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);

        return classWriter.toByteArray();
    }
}