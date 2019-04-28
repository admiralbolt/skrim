package avi.mod.skrim.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Random;

public class SetRandomMapData extends LootFunction {

  private static final String[] STRUCTURE_NAMES = {
      "Stronghold",
      "Mansion",
      "Monument",
      "Village",
      "Mineshaft",
      "Temple"
  };

  protected SetRandomMapData(LootCondition[] conditionsIn) {
    super(conditionsIn);
  }

  @Override
  @Nonnull
  public ItemStack apply(@Nonnull ItemStack stack, @Nonnull Random rand, LootContext context) {
    World world = context.getWorld();
    
    Entity entity = context.getKillerPlayer();
    if (entity == null) return stack;


    String destination = STRUCTURE_NAMES[rand.nextInt(5)];
    BlockPos blockpos = world.findNearestStructure(destination, entity.getPosition(), true);

    ItemStack itemstack = ItemMap.setupNewMap(world, (double) blockpos.getX(), (double) blockpos.getZ(), (byte) 2, true, true);
    ItemMap.renderBiomePreviewMap(world, itemstack);
    MapData.addTargetDecoration(itemstack, blockpos, "+", MapDecoration.Type.PLAYER);
    itemstack.setTranslatableName("filled_map." + destination.toLowerCase(Locale.ROOT));

    return itemstack;
  }

  public static class Serializer extends LootFunction.Serializer<SetRandomMapData> {

    protected Serializer() {
      super(new ResourceLocation("set_random_map_data"), SetRandomMapData.class);
    }

    @Override
    public void serialize(JsonObject object, SetRandomMapData functionClazz, JsonSerializationContext serializationContext) {

    }

    public SetRandomMapData deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
                                        LootCondition[] conditionsIn) {
      return new SetRandomMapData(conditionsIn);
    }
  }
}
