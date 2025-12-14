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
import net.minecraft.world.item.Item;
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
        return tags.stream().anyMatch(tag -> item.getDefaultInstance().is(tag));
    }

    public boolean isAllowedToUse(Item item) {
        if (itemInTagList(whitelistedTags, item) || whitelistedItems.contains(item)) return true;

        if (itemInTagList(blacklistedTags, item)) return false;
        return !blacklistedItems.contains(item);
    }

    private static Tuple<ArrayList<Item>, ArrayList<TagKey<Item>>> loadTagItemList(List<JsonElement> jsonList) {
        ArrayList<Item> items = new ArrayList<>();
        ArrayList<TagKey<Item>> tags = new ArrayList<>();
        for (JsonElement element : jsonList) {
            if (!(element instanceof JsonObject item)) throw new IllegalArgumentException("Item/Tag is not a JSON object (string)");
            Identifier id = JsonHelper.jsonStringToIdentifier(item.get("id"));
            if (item.get("type").getAsString().equalsIgnoreCase("tag")) {
                tags.add(TagKey.create(Registries.ITEM, id));
            } else {
                items.add(BuiltInRegistries.ITEM.getValue(id));
            }
        }
        return new Tuple<>(items, tags);
    }

    public static Effect fromJson(JsonObject object) {
        Tuple<ArrayList<Item>, ArrayList<TagKey<Item>>> whitelist = new Tuple<>(new ArrayList<>(), new ArrayList<>());
        Tuple<ArrayList<Item>, ArrayList<TagKey<Item>>> blacklist = new Tuple<>(new ArrayList<>(), new ArrayList<>());

        if (object.has("whitelist")) whitelist = loadTagItemList(object.getAsJsonArray("whitelist").asList());
        if (object.has("blacklist")) blacklist = loadTagItemList(object.getAsJsonArray("blacklist").asList());

        return new CannotUseEffect(whitelist.getA(), whitelist.getB(), blacklist.getA(), blacklist.getB());
    }
}
