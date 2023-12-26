package io.eucl.geprices;

import io.eucl.geprices.pricegraph.PriceGraphManager;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
public class RealTimePricesOverlay extends Overlay {

    private final PanelComponent panelComponent = new PanelComponent();

    private final PriceGraphManager priceGraphManager;

    @Inject
    public RealTimePricesOverlay(Client client, @NonNull PriceGraphManager priceGraphManager) {
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        this.priceGraphManager = priceGraphManager;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        log.info("rending overlay");

        if (!this.priceGraphManager.hasData()) {
            return null;
        }

        panelComponent.getChildren().clear();

        String overlayTitle = "Real-Time Prices";

        panelComponent.getChildren().add(TitleComponent.builder()
                .text(overlayTitle)
                .color(Color.GREEN)
                .build());

        panelComponent.getChildren().add(new ImageComponent(this.priceGraphManager.getImage()));

        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(overlayTitle) + 30,
                0));

        return panelComponent.render(graphics);
    }
}
