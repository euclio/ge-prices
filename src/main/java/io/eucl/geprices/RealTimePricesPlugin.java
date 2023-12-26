package io.eucl.geprices;

import com.google.inject.Provides;
import io.eucl.geprices.pricegraph.PriceGraphManager;
import io.eucl.geprices.wikiprices.RealTimePricesClient;
import io.eucl.geprices.wikiprices.Timestep;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import javax.swing.*;

@Slf4j
@PluginDescriptor(
	name = "RealTimePrices"
)
public class RealTimePricesPlugin extends Plugin
{
	/**
	 * {@link Varbits#GE_OFFER_CREATION_TYPE} value when the offer is a sell.
	 */
	private static final int SELL = 1;

	@Inject
	private Client client;

	@Inject
	private RealTimePricesConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private RealTimePricesOverlay overlay;

	@Inject
	RealTimePricesClient realTimePricesClient;

	@Inject
	PriceGraphManager priceGraphManager;

	@Override
	protected void startUp() throws Exception
	{
		this.overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		this.overlayManager.remove(overlay);
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event) {
		if (event.getScriptId() == ScriptID.GE_OFFERS_SETUP_BUILD) {
			int item = client.getVarpValue(VarPlayer.CURRENT_GE_ITEM);

			if (item != -1 && client.getVarbitValue(Varbits.GE_OFFER_CREATION_TYPE) == SELL) {
				log.debug("placed sell offer for item {}", item);

				this.realTimePricesClient.getTimeseries(item, Timestep.FIVE_MINUTES).whenCompleteAsync((result, ex) -> {
					if (result != null) {
						SwingUtilities.invokeLater(() -> {
							priceGraphManager.setDataPoints(result.getData());
						});
					}
				});
			}
		}
	}

	@Provides
	RealTimePricesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RealTimePricesConfig.class);
	}
}
