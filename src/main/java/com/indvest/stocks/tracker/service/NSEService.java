package com.indvest.stocks.tracker.service;

import com.indvest.stocks.tracker.bean.Status;
import com.indvest.stocks.tracker.bean.StatusMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class NSEService {

    public StatusMessage downloadStocksData(String entity) {
        if (StringUtils.isBlank(entity)) {
            return new StatusMessage(Status.INVALID_INPUT, "Require a value for entity");
        }

        return new StatusMessage(Status.SUCCESS, "Downloaded successfully");
    }
}
