package lol.sylvie.cuteorigins.util;

import com.google.gson.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.phys.Vec3;

public class JsonHelper {
    public static Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static Identifier jsonStringToIdentifier(JsonElement element) {
        return Identifier.parse(element.getAsString());
    }

    public static Vec3 jsonListToVec3d(JsonArray element) {
        return new Vec3(element.get(0).getAsDouble(), element.get(1).getAsDouble(), element.get(2).getAsDouble());
    }

    public static JsonObject readResource(Resource resource) {
        try (InputStream stream = resource.open()) {
            InputStreamReader reader = new InputStreamReader(stream);
            return GSON.fromJson(reader, JsonObject.class);
        } catch (IOException exception) {
            throw new RuntimeException("Failed to load resource!", exception);
        }
    }
}
