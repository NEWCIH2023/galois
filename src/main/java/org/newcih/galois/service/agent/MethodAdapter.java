package org.newcih.galois.service.agent;

import org.apache.commons.io.IOUtils;
import org.newcih.galois.service.watch.ProjectFileManager;
import org.newcih.galois.utils.GaloisLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.FileOutputStream;
import java.util.Map;

import static org.newcih.galois.constants.FileTypeConstant.CLASS_FILE_TYPE;
import static org.objectweb.asm.Opcodes.ASM9;

public class MethodAdapter extends ClassVisitor {

    private static final GaloisLog logger = GaloisLog.getLogger(MethodAdapter.class);
    private static final ProjectFileManager fileManager = ProjectFileManager.getInstance();
    private final String className;
    protected ClassReader cr;
    protected ClassWriter cw;

    public MethodAdapter(String className) {
        super(ASM9);
        this.className = className;

        try {
            this.cr = new ClassReader(className);
            this.cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
            this.cv = this.cw;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void install(Map<String, MethodAdapter> context) {
        if (context == null) {
            return;
        }

        context.put(this.className, this);
    }

    public byte[] transform() {
        cr.accept(this, 0);
        byte[] result = cw.toByteArray();

        if (logger.isDebugEnabled()) {
            try (FileOutputStream fos = new FileOutputStream(
                    fileManager.getClassPath() + getClass().getSimpleName() + CLASS_FILE_TYPE
            )) {
                IOUtils.write(result, fos);
            } catch (Throwable e) {
                logger.error("dump injected class file error", e);
            }
        }

        return result;
    }
}
