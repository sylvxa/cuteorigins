package lol.sylvie.cuteorigins.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class TextUtil {
    public static Component getIdentifierText(Identifier identifier, String prefix, String suffix) {
        if (suffix != null && !suffix.isEmpty()) suffix = "." + suffix;
        return Component.translatable(prefix + "." + identifier.getNamespace() + "." + identifier.getPath() + suffix);
    }
}
