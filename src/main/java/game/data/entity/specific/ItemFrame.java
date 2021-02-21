package game.data.entity.specific;

import game.data.container.Slot;
import game.data.entity.ObjectEntity;
import game.data.entity.metadata.MetaData_1_13;
import packets.DataTypeProvider;
import se.llbit.nbt.ByteTag;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.IntTag;

import java.util.function.Consumer;

/**
 * Handle item frames as they can be used as decorations
 */
public class ItemFrame extends ObjectEntity {
    int facing;
    private ItemFrameMetaData metaData;

    public ItemFrame() {
        super();
    }

    /**
     * Add additional fields needed to render item frames.
     */
    @Override
    protected void addNbtData(CompoundTag root) {
        super.addNbtData(root);

        root.add("Facing", new IntTag(facing));

        // use math.floor instead of just cast so that negative numbers are handled correctly
        root.add("TileX", new IntTag((int) Math.floor(x)));
        root.add("TileY", new IntTag((int) Math.floor(y)));
        root.add("TileZ", new IntTag((int) Math.floor(z)));

        // prevent floating item frames from popping off
        root.add("Fixed", new ByteTag(1));

        if (metaData != null) {
            metaData.addNbtTags(root);
        }
    }

    @Override
    protected void setData(int data) {
        this.facing = data;
    }

    @Override
    public void parseMetadata(DataTypeProvider provider) {
        if (metaData == null) {
            metaData = new ItemFrameMetaData();
        }
        try {
            metaData.parse(provider);
        } catch (Exception ex) {
            // couldn't parse metadata, whatever
        }
    }
}


class ItemFrameMetaData extends MetaData_1_13 {
    Slot item;
    int rotation;

    @Override
    public void addNbtTags(CompoundTag nbt) {
        if (item != null) {
            nbt.add("Item", item.toNbt());
        }
        nbt.add("ItemRotation", new IntTag(rotation));
    }

    @Override
    public Consumer<DataTypeProvider> getIndexHandler(int i) {
        switch (i) {
            case 7: return provider -> item = provider.readSlot();
            case 8: return provider -> rotation = provider.readVarInt();
        }
        return super.getIndexHandler(i);
    }
}