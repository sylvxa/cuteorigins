package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.util.TagUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class ModifyMiningSpeedEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("modify_mining_speed");

    private final List<Block> affectedBlocks;
    private final List<TagKey<Block>> affectedTags;
    private final float value;

    public ModifyMiningSpeedEffect(List<Block> affectedBlocks, List<TagKey<Block>> affectedTags, float value) {
        super(IDENTIFIER, false);
        this.affectedBlocks = affectedBlocks;
        this.affectedTags = affectedTags;
        this.value = value;
    }

    public boolean blockMatches(Block block) {
        return this.affectedBlocks.stream().anyMatch(matching -> matching.equals(block)) || TagUtil.blockInTagList(this.affectedTags, block);
    }

    public float getValue() {
        return value;
    }

    public static Effect fromJson(JsonObject object) {
        Pair<ArrayList<Block>, ArrayList<TagKey<Block>>> matches = TagUtil.loadTagBlockList(object.getAsJsonArray("matches").asList());

        float value = object.get("value").getAsFloat();

        return new ModifyMiningSpeedEffect(matches.getA(), matches.getB(), value);
    }
}
