package biden.tutorialmod.thirst;

import lombok.Getter;
import net.minecraft.nbt.CompoundTag;

/**
 * PlayerThirst
 */
public class PlayerThirst {

    @Getter
    private int thirst;
    private final int MIN_THIRST = 0;
    private final int MAX_THIRST = 10;

    public void addThirst(int increment) {
        this.thirst = Math.min(this.thirst + increment, MAX_THIRST);
    }

    public void subThirst(int decrement) {
        this.thirst = Math.max(this.thirst - decrement, MIN_THIRST);
    }

    public void copyFrom(PlayerThirst source) {
        this.thirst = source.thirst;
    }

    public void saveNBTData(CompoundTag nTag) {
        nTag.putInt("thirst", this.thirst);
    }

    public void loadNBTData(CompoundTag nTag) {
        this.thirst = nTag.getInt("thirst");
    }
}
