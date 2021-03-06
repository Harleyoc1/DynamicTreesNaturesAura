package com.harleyoconnor.dtnaturesaura.effects;

import com.ferreusveritas.dynamictrees.blocks.leaves.DynamicLeavesBlock;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * Replacement for {@link de.ellpeck.naturesaura.chunk.effect.GrassDieEffect}
 * so that it doesn't convert dynamic leaves to non-dynamic decayed leaves.
 *
 * @author Harley O'Connor
 */
public class GrassDieEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "grass_die");

    private int amount;
    private int dist;

    private boolean calcValues(World world, BlockPos pos, Integer spot) {
        if (spot < 0) {
            int aura = IAuraChunk.getAuraInArea(world, pos, 50);
            if (aura < 0) {
                this.amount = Math.min(300, MathHelper.ceil(Math.abs(aura) / 100000F / IAuraChunk.getSpotAmountInArea(world, pos, 50)));
                if (this.amount > 1) {
                    this.dist = MathHelper.clamp(Math.abs(aura) / 75000, 5, 75);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ActiveType isActiveHere(PlayerEntity player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.level, pos, spot))
            return ActiveType.INACTIVE;
        if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > this.dist * this.dist)
            return ActiveType.INACTIVE;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(ModBlocks.DECAYED_LEAVES);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(world, pos, spot))
            return;

        for (int i = this.amount / 2 + world.random.nextInt(this.amount / 2); i >= 0; i--) {
            BlockPos grassPos = new BlockPos(
                    pos.getX() + world.random.nextGaussian() * this.dist,
                    pos.getY() + world.random.nextGaussian() * this.dist,
                    pos.getZ() + world.random.nextGaussian() * this.dist
            );
            if (grassPos.distSqr(pos) <= this.dist * this.dist && world.hasChunkAt(grassPos)) {
                BlockState state = world.getBlockState(grassPos);
                Block block = state.getBlock();

                BlockState newState = null;
                if (block instanceof LeavesBlock && !(block instanceof DynamicLeavesBlock)) {
                    newState = ModBlocks.DECAYED_LEAVES.defaultBlockState();
                } else if (block instanceof GrassBlock) {
                    newState = Blocks.COARSE_DIRT.defaultBlockState();
                } else if (block instanceof BushBlock) {
                    newState = Blocks.AIR.defaultBlockState();
                } else if (block == ModBlocks.NETHER_GRASS) {
                    newState = Blocks.NETHERRACK.defaultBlockState();
                }
                if (newState != null)
                    world.setBlockAndUpdate(grassPos, newState);
            }
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.grassDieEffect.get() && (type.isSimilar(NaturesAuraAPI.TYPE_OVERWORLD) || type.isSimilar(NaturesAuraAPI.TYPE_NETHER));
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
