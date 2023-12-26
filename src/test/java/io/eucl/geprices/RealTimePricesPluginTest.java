package io.eucl.geprices;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RealTimePricesPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RealTimePricesPlugin.class);
		RuneLite.main(args);
	}
}