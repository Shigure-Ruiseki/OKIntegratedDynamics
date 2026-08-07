package ruiseki.integrateddynamics.api.block.cable;

/**
 * Interface for cables that can become unreal.
 * This means that for example parts can exist in that block space without the cable being there.
 *
 * @author rubensworks
 */
public interface ICableFakeable {

    /**
     * @return If this cable is a real cable, otherwise it is just a holder block for parts without connections.
     */
    public boolean isRealCable();

    /**
     * @param real If this cable is a real cable, otherwise it is just a holder block for parts without connections.
     */
    public void setRealCable(boolean real);
}
