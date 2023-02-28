package org.newcih.service.agent;

import org.apache.commons.io.IOUtils;
import org.newcih.utils.GaloisLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.ASM9;

public class MethodAdapter extends ClassVisitor {

    private static final GaloisLog logger = GaloisLog.getLogger(MethodAdapter.class);

    public MethodAdapter(String className) {
        super(ASM9);

        try {
            this.cr = new ClassReader(className);
            this.cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
            this.cv = this.cw;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected ClassReader cr;

    protected ClassWriter cw;

    public byte[] transform() {
        cr.accept(this, 0);
        byte[] result = cw.toByteArray();

        if (logger.isDebugEnabled()) {
            try {
                String path =
                        Objects.requireNonNull(GaloisLog.class.getResource("/")).getPath().substring(1).replace("/",
                                File.separator);
                path += getClass().getSimpleName() + ".class";
                FileOutputStream fos = new FileOutputStream(path);
                IOUtils.write(result, fos);
                logger.debug("dump injected class file success <%s>.", path);
            } catch (Throwable e) {
                logger.error("dump injected class file error", e);
            }
        }

        return result;
    }
}
