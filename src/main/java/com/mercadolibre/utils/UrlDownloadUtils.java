package com.mercadolibre.utils;

public final class UrlDownloadUtils {

    private static final String DOWNLOAD_URL = "https://852u.adj.st/discount_center_payers/list?adjust_t=ufj9wxn&adjust_deeplink=mercadopago%3A%2F%2Fdiscount_center_payers%2Flist&adjust_label=FLOW";

    public static String buildDownloadUrl(final String flow) {
        return DOWNLOAD_URL.replace("FLOW", flow);
    }
}
