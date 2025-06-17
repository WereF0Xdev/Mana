package fox.mods.mana.util;

import fox.mods.mana.ManaMod;

public class RegistryUtils {

    public static String formModRegistryName(String name) {
        String registryName = ManaMod.MODID + ":" + name;
        return registryName;
    }

}
