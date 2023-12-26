package io.eucl.geprices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.eucl.geprices.pricegraph.PriceGraphManager;
import io.eucl.geprices.wikiprices.Timeseries;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;

public class PriceGraphTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static void main(String[] args) {
        final Timeseries timeseries;
        try {
            timeseries = MAPPER.readValue(PriceGraphTest.class.getClassLoader().getResourceAsStream("test.json"), Timeseries.class);
        } catch (
                IOException e) {
            throw new UncheckedIOException(e);
        }

        PriceGraphManager priceGraphManager = new PriceGraphManager();
        priceGraphManager.setDataPoints(timeseries.getData());
        BufferedImage image = priceGraphManager.getImage();

        EventQueue.invokeLater(() -> {
            JLabel label = new JLabel(new ImageIcon(image));

            JPanel panel = new JPanel();
            panel.add(label);

            JFrame frame = new JFrame();
            frame.setSize(image.getWidth(), image.getHeight());
            frame.add(panel);
            frame.setVisible(true);
        });
    }
}
