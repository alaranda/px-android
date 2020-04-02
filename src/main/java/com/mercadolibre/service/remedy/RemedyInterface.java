package com.mercadolibre.service.remedy;

import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.px.dto.lib.context.Context;

public interface RemedyInterface {

    public RemediesResponse applyRemedy(final Context context, final RemediesRequest remediesRequest,
                                        final RemediesResponse remediesResponse);
}
