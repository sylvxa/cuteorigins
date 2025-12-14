package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class EnderPearlEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("ender_pearl");

    protected EnderPearlEffect() {
        super(IDENTIFIER, true);
    }

    @Override
    public void onAction(ServerPlayer player) {
        Level world = player.level();
        ThrownEnderpearl enderPearlEntity = new net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl(world, player, Items.ENDER_PEARL.getDefaultInstance());
        enderPearlEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
        world.addFreshEntity(enderPearlEntity);
    }

    public static Effect fromJson(JsonObject object) {
        return new EnderPearlEffect();
    }
}
