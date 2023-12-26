package io.eucl.geprices.wikiprices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Deserialized form of a Timeseries from the Real-time prices API.
 */
@Builder
@Data
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Timeseries {

    private final List<DataPoint> data;

    private final String error;

    private final String itemId;
}
