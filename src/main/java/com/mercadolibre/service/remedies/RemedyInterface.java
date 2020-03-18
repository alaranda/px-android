package com.mercadolibre.service.remedies;

import com.mercadolibre.dto.remedies.RemediesRequest;
import com.mercadolibre.dto.remedies.RemediesResponse;
import com.mercadolibre.px.dto.lib.context.Context;

public interface RemedyInterface {

    public RemediesResponse applyRemedy(final Context context, final RemediesRequest remediesRequest,
                                        final RemediesResponse remediesResponse);
}
