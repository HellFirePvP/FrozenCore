package hellfirepvp.frozencore.client.gui;

import com.google.common.collect.Lists;
import hellfirepvp.frozencore.FrozenCore;
import hellfirepvp.frozencore.common.container.ContainerCentrifuge;
import hellfirepvp.frozencore.common.tile.TileCentrifuge;
import hellfirepvp.frozencore.common.util.SimpleSingleFluidCapabilityTank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: GuiContainerCentrifuge
 * Created by HellFirePvP
 * Date: 15.04.2017 / 11:13
 */
public class GuiContainerCentrifuge extends GuiContainer {

    private static final ResourceLocation CENTRIFUGE_GUI_TEXTURE = new ResourceLocation(FrozenCore.MODID, "textures/gui/centrifuge.png");
    private static final Rectangle rctFluid = new Rectangle(27, 11, 11, 60);
    private static final Rectangle rctRF = new Rectangle(9, 7,14, 68);

    private static final Rectangle rctProgressPaste = new Rectangle(62, 32, 24, 17);
    private static final Rectangle rctProgressCopy = new Rectangle(176, 0, 24, 17);

    private final TileCentrifuge owner;

    public GuiContainerCentrifuge(TileCentrifuge centrifuge) {
        super(new ContainerCentrifuge(Minecraft.getMinecraft().thePlayer.inventory, centrifuge));
        this.owner = centrifuge;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if(owner.isInvalid()) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if(rctFluid.contains(mouseX - guiLeft, mouseY - guiTop)) {
            SimpleSingleFluidCapabilityTank tank = owner.getTank();
            if(tank.getTankFluid() == null) {
                drawHoveringText(Lists.newArrayList(
                        String.format("%d / %d", 0, tank.getCapacity())),
                        mouseX - guiLeft, mouseY - guiTop);
            } else {
                drawHoveringText(
                        Lists.newArrayList(tank.getFluid().getLocalizedName(),
                        String.format("%d / %d", tank.getFluidAmount(), tank.getCapacity())),
                        mouseX - guiLeft, mouseY - guiTop);
            }
        }
        if(rctRF.contains(mouseX - guiLeft, mouseY - guiTop)) {
            int max = owner.getMaxEnergy();
            int current = owner.getCurrentEnergy();
            drawHoveringText(Lists.newArrayList(
                    String.format("%d / %d RF", current, max)),
                    mouseX - guiLeft, mouseY - guiTop);
        }

        if(owner.isCrafting() && rctProgressPaste.contains(mouseX - guiLeft, mouseY - guiTop)) {
            float perc = owner.getCurrentCraftingProcess();
            drawHoveringText(Lists.newArrayList(((int) (perc * 100)) + "%"), mouseX - guiLeft, mouseY - guiTop);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CENTRIFUGE_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        SimpleSingleFluidCapabilityTank tank = owner.getTank();
        if(tank.getTankFluid() != null) {
            TextureAtlasSprite tex = mc.getTextureMapBlocks().getAtlasSprite(tank.getTankFluid().getStill().toString());
            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            int c = tank.getTankFluid().getColor();
            float a = (float) ((c >> 24) & 0xFF) / 255.0F;
            float r = (float) ((c >> 16) & 0xFF) / 255.0F;
            float g = (float) ((c >>  8) & 0xFF) / 255.0F;
            float b = (float) ((c >>  0) & 0xFF) / 255.0F;
            GlStateManager.color(r, g, b, a);

            int height = (int) Math.round(rctFluid.getHeight() * (((float) tank.getFluidAmount()) / ((float) tank.getCapacity())));
            this.drawRect(rctFluid.x + guiLeft, rctFluid.y + (rctFluid.height - height) + guiTop, rctFluid.width, height,
                    tex.getMinU(), tex.getMinV(), tex.getMaxU() - tex.getMinU(), tex.getMaxV() - tex.getMinV());
        }

        if(owner.getCurrentEnergy() > 0) {
            float percFilled = ((float) owner.getCurrentEnergy()) / ((float) owner.getMaxEnergy());
            drawGradientRect(rctRF.x + guiLeft, Math.round(rctRF.y + (rctRF.height - (rctRF.height * percFilled))) + guiTop,
                    rctRF.x + guiLeft + rctRF.width, rctRF.y + rctRF.height + guiTop,
                    0xFFDD0000, 0xFFDD0000);
        }

        if(owner.isCrafting()) {
            float perc = owner.getCurrentCraftingProcess();
            int width = Math.round(perc * rctProgressCopy.width);
            this.mc.getTextureManager().bindTexture(CENTRIFUGE_GUI_TEXTURE);
            drawTexturedModalRect(i + rctProgressPaste.x, j + rctProgressPaste.y, 176, 0, width, rctProgressCopy.height);
        }
    }

    protected void drawRect(int offsetX, int offsetY, int width, int height, double u, double v, double uwidth, double vheight) {
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();

        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(u, v + vheight).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(u + uwidth, v + vheight).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(u + uwidth, v).endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(u, v).endVertex();
        tes.draw();
    }

}
