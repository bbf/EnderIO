package crazypants.enderio.item.darksteel.upgrade;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.item.IHasPlayerRenderer;
import crazypants.enderio.item.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.item.darksteel.IDarkSteelItem;
import crazypants.util.BaublesUtil;

@SideOnly(Side.CLIENT)
public class UpgradeRenderDispatcher implements LayerRenderer<AbstractClientPlayer> {

  public final static UpgradeRenderDispatcher instance = new UpgradeRenderDispatcher(null);

  private final RenderPlayer renderPlayer;

  private UpgradeRenderDispatcher(RenderPlayer renderPlayer) {
    this.renderPlayer = renderPlayer;
  }

  // no WeakHashSet in Java...
  private static final Map<RenderPlayer, Object> injected = new WeakHashMap<RenderPlayer, Object>();

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
    if (!injected.containsKey(event.getRenderer())) {
      event.getRenderer().addLayer(new UpgradeRenderDispatcher(event.getRenderer()));
      injected.put(event.getRenderer(), null);
    }
  }


  @Override
  public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_,
      float p_177141_6_, float p_177141_7_, float scale) {
    for (ItemStack piece : entitylivingbaseIn.inventory.armorInventory) {
      if (piece != null && piece.getItem() instanceof IDarkSteelItem) {
        for (IDarkSteelUpgrade upg : DarkSteelRecipeManager.instance.getUpgrades()) {
          if (upg.hasUpgrade(piece)) {
            IRenderUpgrade render = upg.getRender();
            if (render != null) {
              render.doRenderLayer(renderPlayer, piece, entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_,
                  scale);
            }
          }
        }
      } else if (piece != null && piece.getItem() instanceof IHasPlayerRenderer) {
        IRenderUpgrade render = ((IHasPlayerRenderer) piece.getItem()).getRender();
        if (render != null) {
          render.doRenderLayer(renderPlayer, piece, entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
        }
      }
    }
    IInventory baubles = BaublesUtil.instance().getBaubles(entitylivingbaseIn);
    if (baubles != null) {
      for (int i = 0; i < baubles.getSizeInventory(); i++) {
        ItemStack piece = baubles.getStackInSlot(i);
        if (piece != null && piece.getItem() instanceof IHasPlayerRenderer) {
          IRenderUpgrade render = ((IHasPlayerRenderer) piece.getItem()).getRender();
          if (render != null) {
            render.doRenderLayer(renderPlayer, piece, entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
          }
        }
      }
    }
  }

  @Override
  public boolean shouldCombineTextures() {
    return true;
  }

}