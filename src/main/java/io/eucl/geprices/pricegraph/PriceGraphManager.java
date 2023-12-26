package io.eucl.geprices.pricegraph;

import io.eucl.geprices.wikiprices.DataPoint;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Singleton
public class PriceGraphManager {

    private static final Dimension GRAPH_DIMENSION = new Dimension(400, 300);

    private static final int GRAPH_IMAGE_TYPE = BufferedImage.TYPE_4BYTE_ABGR;

    private List<DataPoint> dataPoints;

    private NiceScale scale;

    public boolean hasData() {
        return this.dataPoints != null;
    }

    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;

        if (this.dataPoints != null) {
            IntSummaryStatistics priceStatistics = this.dataPoints.stream()
                    .flatMapToInt(dataPoint -> Stream.concat(
                                    Optional.ofNullable(dataPoint.getAvgHighPrice()).stream(),
                                    Optional.ofNullable(dataPoint.getAvgLowPrice()).stream())
                            .mapToInt(i -> i))
                    .summaryStatistics();
            this.scale = new NiceScale(priceStatistics.getMin(), priceStatistics.getMax());
        } else {
            this.scale = null;
        }
    }

    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(GRAPH_DIMENSION.width, GRAPH_DIMENSION.height, GRAPH_IMAGE_TYPE);

        if (this.dataPoints == null || this.dataPoints.size() < 2) {
            return image;
        }

        Graphics2D imageGraphics = image.createGraphics();
        imageGraphics.setColor(Color.WHITE);
        imageGraphics.fillRect(0, 0, image.getWidth(), image.getHeight());

        log.debug("drawing {} data points", this.dataPoints);

        imageGraphics.setColor(Color.BLACK);
        this.drawAxes(imageGraphics);

        imageGraphics.setColor(Color.ORANGE);
        this.drawTimeseries(
                imageGraphics,
                this.dataPoints.stream().map(DataPoint::getAvgHighPrice).collect(Collectors.toList()));

        imageGraphics.setColor(Color.GREEN);
        this.drawTimeseries(
                imageGraphics,
                this.dataPoints.stream().map(DataPoint::getAvgLowPrice).collect(Collectors.toList()));

        return image;
    }

    private void drawAxes(@NonNull Graphics2D graphics) {
        double tick = this.scale.getNiceMin();
        while (tick <= this.scale.getNiceMax()) {
            tick += this.scale.getTickSpacing();

            graphics.drawLine(0, scaleToPixel(tick), GRAPH_DIMENSION.width, scaleToPixel(tick));
        }
    }

    private void drawTimeseries(@NonNull Graphics2D graphics, @NonNull List<Integer> data) {
        int start = GRAPH_DIMENSION.width / 2 - data.size() / 2;

        ListIterator<Integer> dataPointsIter = data.listIterator();

        Integer prev = null;
        int prevIdx = -1;

        while (prev == null && dataPointsIter.hasNext()) {
            Integer curr = dataPointsIter.next();
            if (curr != null) {
                prev = curr;
                prevIdx = dataPointsIter.previousIndex();
            }
        }

        while (dataPointsIter.hasNext()) {
            int currIdx = dataPointsIter.nextIndex();
            Integer curr = dataPointsIter.next();

            if (curr == null) {
                continue;
            }

            graphics.drawLine(start + prevIdx, scaleToPixel(prev), start + currIdx, scaleToPixel(curr));

            prev = curr;
            prevIdx = currIdx;
        }
    }

    private int scaleToPixel(double value) {
        double graphScale = GRAPH_DIMENSION.height / (this.scale.getNiceMax() - this.scale.getNiceMin());
        return (int) ((value - this.scale.getNiceMin()) * graphScale);
    }
}
