package lol.sylvie.cuteorigins.origin;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.Identifier;

public class OriginRegistry {
    public HashMap<Identifier, Origin> origins = new HashMap<>();

    public void addOrigin(Origin origin) {
        this.origins.put(origin.identifier(), origin);
    }

    public Origin getOrigin(Identifier identifier) {
        return this.origins.get(identifier);
    }

    public List<Origin> getOrigins() {
        return origins.values().stream().toList();
    }

    public List<Origin> getOriginsAlphabetically() {
        return getOrigins().stream().sorted(Comparator.comparing(o -> o.getName().getString())).toList();
    }

    public void clearRegistry() {
        origins.clear();
    }
}
