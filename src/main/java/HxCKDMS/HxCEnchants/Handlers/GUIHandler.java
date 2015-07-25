package HxCKDMS.HxCEnchants.Handlers;

import HxCKDMS.HxCEnchants.XPInfuser.XPInfuserContainer;
import HxCKDMS.HxCEnchants.XPInfuser.XPInfuserGUI;
import HxCKDMS.HxCEnchants.XPInfuser.XPInfuserTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GUIHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if(tileEntity instanceof XPInfuserTile){
            return new XPInfuserContainer(player, (XPInfuserTile)tileEntity);
        }
        return null;
    }
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if(tileEntity instanceof XPInfuserTile){
            return new XPInfuserGUI(new XPInfuserContainer(player, (XPInfuserTile)tileEntity), player);
        }
        return null;
    }
}