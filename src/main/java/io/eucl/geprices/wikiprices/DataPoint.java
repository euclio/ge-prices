package io.eucl.geprices.wikiprices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

/**
 * Deserialized form of a data point from the real-time prices API.
 */
@Builder
@Data
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataPoint {
    private Instant timestamp;

    private final Integer avgHighPrice;

    private final Integer avgLowPrice;

    private final Integer highPriceVolume;

    private final Integer lowPriceVolume;
}
