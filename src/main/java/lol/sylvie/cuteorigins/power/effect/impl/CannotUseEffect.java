package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.util.JsonHelper;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.*;

public class CannotUseEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("cannot_use");

    private final List<Item> whitelistedItems;
    private final List<TagKey<Item>> whitelistedTags;

    private final List<Item> blacklistedItems;
    private final List<TagKey<Item>> blacklistedTags;

    protected CannotUseEffect(List<Item> whitelistedItems, List<TagKey<Item>> whitelistedTags, List<Item> blacklistedItems, List<TagKey<Item>> blacklistedTags) {
        super(IDENTIFIER, false);
        this.whitelistedItems = whitelistedItems;
        this.whitelistedTags = whitelistedTags;

        this.blacklistedItems = blacklistedItems;
        this.blacklistedTags = blacklistedTags;
    }

    private boolean itemInTagList(List<TagKey<Item>> tags, Item item) {
        return tags.stream().anyMatch(tag -> item.getDefaultStack().isIn(tag));
    }

    public boolean isAllowedToUse(Item item) {
        if (itemInTagList(whitelistedTags, item) || whitelistedItems.contains(item)) return true;

        if (itemInTagList(blacklistedTags, item)) return false;
        return !blacklistedItems.contains(item);
    }

    private static Pair<ArrayList<Item>, ArrayList<TagKey<Item>>> loadTagItemList(List<JsonElement> jsonList) {
        ArrayList<Item> items = new ArrayList<>();
        ArrayList<TagKey<Item>> tags = new ArrayList<>();
        for (JsonElement element : jsonList) {
            if (!(element instanceof JsonObject item)) throw new IllegalArgumentException("Item/Tag is not a JSON object (string)");
            Identifier id = JsonHelper.jsonStringToIdentifier(item.get("id"));
            if (item.get("type").getAsString().equalsIgnoreCase("tag")) {
                tags.add(TagKey.of(RegistryKeys.ITEM, id));
            } else {
                items.add(Registries.ITEM.get(id));
            }
        }
        return new Pair<>(items, tags);
    }

    public static Effect fromJson(JsonObject object) {
        Pair<ArrayList<Item>, ArrayList<TagKey<Item>>> whitelist = new Pair<>(new ArrayList<>(), new ArrayList<>());
        Pair<ArrayList<Item>, ArrayList<TagKey<Item>>> blacklist = new Pair<>(new ArrayList<>(), new ArrayList<>());

        if (object.has("whitelist")) whitelist = loadTagItemList(object.getAsJsonArray("whitelist").asList());
        if (object.has("blacklist")) blacklist = loadTagItemList(object.getAsJsonArray("blacklist").asList());

        return new CannotUseEffect(whitelist.getLeft(), whitelist.getRight(), blacklist.getLeft(), blacklist.getRight());
    }
}
