package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.util.JsonHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import java.util.ArrayList;
import java.util.List;

public class ModifyHarvestEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("modify_harvest");

    private final List<Block> whitelistedItems;
    private final List<TagKey<Block>> whitelistedTags;

    private final List<Block> blacklistedItems;
    private final List<TagKey<Block>> blacklistedTags;

    protected ModifyHarvestEffect(List<Block> whitelistedItems, List<TagKey<Block>> whitelistedTags, List<Block> blacklistedItems, List<TagKey<Block>> blacklistedTags) {
        super(IDENTIFIER, false);
        this.whitelistedItems = whitelistedItems;
        this.whitelistedTags = whitelistedTags;

        this.blacklistedItems = blacklistedItems;
        this.blacklistedTags = blacklistedTags;
    }

    private boolean itemInTagList(List<TagKey<Block>> tags, Block block) {
        return tags.stream().anyMatch(tag -> block.defaultBlockState().is(tag));
    }

    public boolean inWhitelist(Block block) {
        return itemInTagList(whitelistedTags, block) || whitelistedItems.contains(block);
    }

    public boolean inBlackList(Block block) {
        return itemInTagList(blacklistedTags, block) || blacklistedItems.contains(block);
    }

    private static Tuple<ArrayList<Block>, ArrayList<TagKey<Block>>> loadTagItemList(List<JsonElement> jsonList) {
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
        return new Tuple<>(items, tags);
    }

    public static Effect fromJson(JsonObject object) {
        Tuple<ArrayList<Block>, ArrayList<TagKey<Block>>> whitelist = new Tuple<>(new ArrayList<>(), new ArrayList<>());
        Tuple<ArrayList<Block>, ArrayList<TagKey<Block>>> blacklist = new Tuple<>(new ArrayList<>(), new ArrayList<>());

        if (object.has("whitelist")) whitelist = loadTagItemList(object.getAsJsonArray("whitelist").asList());
        if (object.has("blacklist")) blacklist = loadTagItemList(object.getAsJsonArray("blacklist").asList());

        return new ModifyHarvestEffect(whitelist.getA(), whitelist.getB(), blacklist.getA(), blacklist.getB());
    }
}
