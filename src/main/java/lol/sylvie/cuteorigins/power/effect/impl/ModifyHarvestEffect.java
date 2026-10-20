package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.util.JsonHelper;
import lol.sylvie.cuteorigins.util.TagUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class ModifyHarvestEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("modify_harvest");

    private final List<Block> whitelistedItems;
    private final List<TagKey<Block>> whitelistedTags;

    private final List<Block> blacklistedItems;
    private final List<TagKey<Block>> blacklistedTags;

    protected ModifyHarvestEffect(List<Block> whitelistedBlocks, List<TagKey<Block>> whitelistedTags, List<Block> blacklistedBlocks, List<TagKey<Block>> blacklistedTags) {
        super(IDENTIFIER, false);
        this.whitelistedItems = whitelistedBlocks;
        this.whitelistedTags = whitelistedTags;

        this.blacklistedItems = blacklistedBlocks;
        this.blacklistedTags = blacklistedTags;
    }

    public boolean inWhitelist(Block block) {
        return TagUtil.blockInTagList(whitelistedTags, block) || whitelistedItems.contains(block);
    }

    public boolean inBlackList(Block block) {
        return TagUtil.blockInTagList(blacklistedTags, block) || blacklistedItems.contains(block);
    }


    public static Effect fromJson(JsonObject object) {
        Pair<ArrayList<Block>, ArrayList<TagKey<Block>>> whitelist = new Pair<>(new ArrayList<>(), new ArrayList<>());
        Pair<ArrayList<Block>, ArrayList<TagKey<Block>>> blacklist = new Pair<>(new ArrayList<>(), new ArrayList<>());

        if (object.has("whitelist")) whitelist = TagUtil.loadTagBlockList(object.getAsJsonArray("whitelist").asList());
        if (object.has("blacklist")) blacklist = TagUtil.loadTagBlockList(object.getAsJsonArray("blacklist").asList());

        return new ModifyHarvestEffect(whitelist.getA(), whitelist.getB(), blacklist.getA(), blacklist.getB());
    }
}
