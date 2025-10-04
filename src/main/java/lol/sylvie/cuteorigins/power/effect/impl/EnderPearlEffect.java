package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class EnderPearlEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("ender_pearl");

    protected EnderPearlEffect() {
        super(IDENTIFIER, true);
    }

    @Override
    public void onAction(ServerPlayerEntity player) {
        World world = player.getEntityWorld();
        EnderPearlEntity enderPearlEntity = new EnderPearlEntity(world, player, Items.ENDER_PEARL.getDefaultStack());
        enderPearlEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 1.5F, 1.0F);
        world.spawnEntity(enderPearlEntity);
    }

    public static Effect fromJson(JsonObject object) {
        return new EnderPearlEffect();
    }
}
