package com.harleyoconnor.dtnaturesaura.blocks;

import com.ferreusveritas.dynamictrees.api.registry.TypedRegistry;
import com.ferreusveritas.dynamictrees.blocks.branches.BranchBlock;
import com.ferreusveritas.dynamictrees.blocks.leaves.DynamicLeavesBlock;
import com.ferreusveritas.dynamictrees.blocks.leaves.LeavesProperties;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

/**
 * @author Harley O'Connor
 */
public final class DecayedLeavesProperties extends LeavesProperties {

    public static final TypedRegistry.EntryType<LeavesProperties> TYPE = TypedRegistry.newType(DecayedLeavesProperties::new);

    public DecayedLeavesProperties(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    protected DynamicLeavesBlock createDynamicLeaves(AbstractBlock.Properties properties) {
        return new DynamicDecayedLeavesBlock(this, properties);
    }

    @Override
    public int getRadiusForConnection(BlockState blockState, IBlockReader blockAccess, BlockPos pos, BranchBlock from, Direction side, int fromRadius) {
        final int twigRadius = from.getFamily().getPrimaryThickness();
        return fromRadius == twigRadius || this.connectAnyRadius ? twigRadius : 0;
    }

}
