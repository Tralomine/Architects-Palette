package architectspalette.content.blocks.abyssaline;

import architectspalette.content.blocks.VerticalSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;

import java.util.Random;

import static architectspalette.content.blocks.abyssaline.NewAbyssalineBlock.CHARGED;
import static architectspalette.content.blocks.abyssaline.NewAbyssalineBlock.CHARGE_SOURCE;

public class AbyssalineVerticalSlabBlock extends VerticalSlabBlock implements IAbyssalineChargeable {

	public AbyssalineVerticalSlabBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.defaultBlockState().setValue(CHARGE_SOURCE, Direction.NORTH).setValue(CHARGED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(CHARGED, CHARGE_SOURCE, TYPE, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		context.getLevel().scheduleTick(context.getClickedPos(), this, 1);
		return super.getStateForPlacement(context);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		AbyssalineHelper.abyssalineNeighborUpdate(this, state, worldIn, pos, blockIn, fromPos);
	}
	
	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
		AbyssalineHelper.abyssalineTick(state, worldIn, pos);
	}

	//Interface stuff
	@Override
	public boolean acceptsChargeFrom(BlockState stateIn, Direction faceIn) {
		VerticalSlabType type = stateIn.getValue(TYPE);
		return type == VerticalSlabType.DOUBLE || faceIn != type.direction;
	}

	@Override
	public boolean outputsChargeFrom(BlockState stateIn, Direction faceIn) {
		return IAbyssalineChargeable.super.outputsChargeFrom(stateIn, faceIn) && this.acceptsChargeFrom(stateIn, faceIn);
	}

	// Slabs should never transfer power through the faces that don't collide, so don't provide a state here that can.
	@Override
	public BlockState getStateWithChargeDirection(BlockState stateIn, Direction faceOut) {
		VerticalSlabType type = stateIn.getValue(TYPE);
		if(type.direction == faceOut)
			return stateIn;
		
		return stateIn.setValue(CHARGE_SOURCE, faceOut);
	}

}
