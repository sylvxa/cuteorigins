package lol.sylvie.cuteorigins.data;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.power.Power;
import lol.sylvie.cuteorigins.util.JsonHelper;
import lol.sylvie.cuteorigins.util.OriginRegistries;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class OriginResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return Identifier.fromNamespaceAndPath(CuteOrigins.MOD_ID, "origin_data");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        CuteOrigins.LOGGER.info("Loading resources...");

        OriginRegistries.POWER_REGISTRY.clearRegistry();
        manager.listResources("power", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            Identifier identifier = identifierFromPath(id);
            JsonObject object = JsonHelper.readResource(resourceRef);
            Power power = Power.fromJson(identifier, object);
            OriginRegistries.POWER_REGISTRY.addPower(power);
        });

        OriginRegistries.ORIGIN_REGISTRY.clearRegistry();
        manager.listResources("origin", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            Identifier identifier = identifierFromPath(id);
            JsonObject object = JsonHelper.readResource(resourceRef);
            Origin origin = Origin.fromJson(identifier, object);
            OriginRegistries.ORIGIN_REGISTRY.addOrigin(origin);
        });

    }

    private Identifier identifierFromPath(Identifier identifier) {
        String path = identifier.getPath();
        return Identifier.fromNamespaceAndPath(identifier.getNamespace(), (path.substring(path.lastIndexOf("/") + 1)).replace(".json", ""));
    }
}
