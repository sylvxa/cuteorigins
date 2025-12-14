package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.resources.Identifier;

public class VillagerGossipEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("villager_gossip");
    private final int reputation;

    protected VillagerGossipEffect(int reputation) {
        super(IDENTIFIER, false);
        this.reputation = reputation;
    }

    public int getReputation() {
        return reputation;
    }

    public static Effect fromJson(JsonObject object) {
        return new VillagerGossipEffect(object.get("reputation").getAsInt());
    }
}
