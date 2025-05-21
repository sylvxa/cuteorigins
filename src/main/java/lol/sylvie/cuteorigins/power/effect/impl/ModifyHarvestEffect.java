package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.util.JsonHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

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
        return tags.stream().anyMatch(tag -> block.getDefaultState().isIn(tag));
    }

    public boolean inWhitelist(Block block) {
        return itemInTagList(whitelistedTags, block) || whitelistedItems.contains(block);
    }

    public boolean inBlackList(Block block) {
        return itemInTagList(blacklistedTags, block) || blacklistedItems.contains(block);
    }

    private static Pair<ArrayList<Block>, ArrayList<TagKey<Block>>> loadTagItemList(List<JsonElement> jsonList) {
        ArrayList<Block> items = new ArrayList<>();
        ArrayList<TagKey<Block>> tags = new ArrayList<>();
        for (JsonElement element : jsonList) {
            if (!(element instanceof JsonObject item)) throw new IllegalArgumentException("Item/Tag is not a JSON object (string)");
            Identifier id = JsonHelper.jsonStringToIdentifier(item.get("id"));
            if (item.get("type").getAsString().equalsIgnoreCase("tag")) {
                tags.add(TagKey.of(RegistryKeys.BLOCK, id));
            } else {
                items.add(Registries.BLOCK.get(id));
            }
        }
        return new Pair<>(items, tags);
    }

    public static Effect fromJson(JsonObject object) {
        Pair<ArrayList<Block>, ArrayList<TagKey<Block>>> whitelist = new Pair<>(new ArrayList<>(), new ArrayList<>());
        Pair<ArrayList<Block>, ArrayList<TagKey<Block>>> blacklist = new Pair<>(new ArrayList<>(), new ArrayList<>());

        if (object.has("whitelist")) whitelist = loadTagItemList(object.getAsJsonArray("whitelist").asList());
        if (object.has("blacklist")) blacklist = loadTagItemList(object.getAsJsonArray("blacklist").asList());

        return new ModifyHarvestEffect(whitelist.getLeft(), whitelist.getRight(), blacklist.getLeft(), blacklist.getRight());
    }
}
