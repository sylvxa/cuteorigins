package lol.sylvie.cuteorigins.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class TagUtil {
    public static Pair<ArrayList<Block>, ArrayList<TagKey<Block>>> loadTagBlockList(List<JsonElement> jsonList) {
        ArrayList<Block> items = new ArrayList<>();
        ArrayList<TagKey<Block>> tags = new ArrayList<>();
        for (JsonElement element : jsonList) {
            if (!(element instanceof JsonObject item)) throw new IllegalArgumentException("Item/Tag is not a JSON object (string)");
            Identifier id = JsonHelper.jsonStringToIdentifier(item.get("id"));
            if (item.get("type").getAsString().equalsIgnoreCase("tag")) {
                tags.add(TagKey.create(Registries.BLOCK, id));
            } else {
                items.add(BuiltInRegistries.BLOCK.getValue(id));
            }
        }
        return new Pair<>(items, tags);
    }

    public static boolean blockInTagList(List<TagKey<Block>> tags, Block block) {
        return tags.stream().anyMatch(tag -> block.defaultBlockState().is(tag));
    }
}
