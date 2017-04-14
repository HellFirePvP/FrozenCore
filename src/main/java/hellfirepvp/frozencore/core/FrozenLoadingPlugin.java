package hellfirepvp.frozencore.core;

import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: FrozenLoadingPlugin
 * Created by HellFirePvP
 * Date: 14.04.2017 / 17:20
 */
@IFMLLoadingPlugin.Name(value = "FrozenLoadingPlugin")
@IFMLLoadingPlugin.MCVersion(value = "1.10.2")
@IFMLLoadingPlugin.TransformerExclusions({"hellfirepvp.frozencore.core"})
@IFMLLoadingPlugin.SortingIndex(100)
public class FrozenLoadingPlugin implements IFMLLoadingPlugin, IFMLCallHook {

    @Override
    public Void call() throws Exception {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return "hellfirepvp.frozencore.core.FrozenTransformer";
    }

}
