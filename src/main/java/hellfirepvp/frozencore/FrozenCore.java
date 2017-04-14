package hellfirepvp.frozencore;

import hellfirepvp.frozencore.common.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: FrozenCore
 * Created by HellFirePvP
 * Date: 14.04.2017 / 16:26
 */
@Mod(modid = FrozenCore.MODID, name = FrozenCore.NAME, version = FrozenCore.VERSION, acceptedMinecraftVersions="[1.10.2]")
public class FrozenCore {

    public static final String MODID = "frozencore";
    public static final String NAME = "Frozen Core";
    public static final String VERSION = "0.1-alpha";
    public static final String CLIENT_PROXY = "hellfirepvp.frozencore.client.ClientProxy";
    public static final String COMMON_PROXY = "hellfirepvp.frozencore.common.CommonProxy";

    @Mod.Instance(MODID)
    public static FrozenCore instance;

    public static Logger log = LogManager.getLogger(NAME);

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        event.getModMetadata().version = VERSION;

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

}
