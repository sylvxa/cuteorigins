package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.resources.Identifier;

public class EnchantmentDiscountEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("enchantment_discount");
    private final int discount;

    protected EnchantmentDiscountEffect(int discount) {
        super(IDENTIFIER, false);
        this.discount = discount;
    }

    public int getDiscount() {
        return discount;
    }

    public static Effect fromJson(JsonObject object) {
        return new EnchantmentDiscountEffect(object.get("discount").getAsInt());
    }
}
