package hellfirepvp.frozencore.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: FrozenTransformer
 * Created by HellFirePvP
 * Date: 14.04.2017 / 17:54
 */
public class FrozenTransformer implements IClassTransformer {

    private static final String PATCH_PACKAGE = "hellfirepvp.frozencore.core.patch";

    private static ClassPatch currentPatch = null;

    private Map<String, List<ClassPatch>> availablePatches = new HashMap<>();

    public FrozenTransformer() throws IOException {
        FMLLog.info("[FrozenTransformer] Loading patches...");
        int loaded = loadClassPatches();
        FMLLog.info("[FrozenTransformer] Initialized! Loaded " + loaded + " class patches!");
    }

    private int loadClassPatches() throws IOException {
        ImmutableSet<ClassPath.ClassInfo> classes =
                ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClassesRecursive(PATCH_PACKAGE);
        List<Class> patchClasses = new LinkedList<>();
        for (ClassPath.ClassInfo info : classes) {
            if(info.getName().startsWith(PATCH_PACKAGE)) {
                patchClasses.add(info.load());
            }
        }
        int load = 0;
        for (Class patchClass : patchClasses) {
            if (ClassPatch.class.isAssignableFrom(patchClass) && !Modifier.isAbstract(patchClass.getModifiers())) {
                try {
                    ClassPatch patch = (ClassPatch) patchClass.newInstance();
                    if(!availablePatches.containsKey(patch.getClassName())) {
                        availablePatches.put(patch.getClassName(), new LinkedList<>());
                    }
                    availablePatches.get(patch.getClassName()).add(patch);
                    load++;
                } catch (Exception exc) {
                    throw new IllegalStateException("Could not load ClassPatch: " + patchClass.getSimpleName(), exc);
                }
            }
        }
        return load;
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(basicClass);
        reader.accept(node, 0);

        try {
            if(!availablePatches.isEmpty()) {
                List<ClassPatch> patches = availablePatches.get(transformedName);
                if(patches != null && !patches.isEmpty()) {
                    FMLLog.info("[FrozenTransformer] Transforming " + name + " : " + transformedName + " with " + patches.size() + " patches!");
                    try {
                        for (ClassPatch patch : patches) {
                            currentPatch = patch;
                            patch.transform(node);
                            FMLLog.info("[FrozenTransformer] Applied patch " + patch.getClass().getSimpleName().toUpperCase());
                            currentPatch = null;
                        }
                    } catch (Exception exc) {
                        throw new ASMTransformationException("Applying ClassPatches failed (ClassName: " + name + " - " + transformedName + ") - Rethrowing exception!", exc);
                    }
                }
                availablePatches.remove(transformedName);
            }
        } catch (ASMTransformationException asmException) {
            if(currentPatch != null) {
                FMLLog.warning("Patcher was in active patch: " + currentPatch.getClass().getSimpleName());
            }
            asmException.printStackTrace();
            throw asmException; //Rethrow
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        basicClass = writer.toByteArray();


        return basicClass;
    }

}
