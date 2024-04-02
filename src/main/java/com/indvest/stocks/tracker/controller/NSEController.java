package com.indvest.stocks.tracker.controller;

import com.indvest.stocks.tracker.bean.DownloadResult;
import com.indvest.stocks.tracker.bean.StatusMessage;
import com.indvest.stocks.tracker.service.NSEService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nse/")
public class NSEController {
    private static final Logger log = LoggerFactory.getLogger(NSEController.class);

    @Autowired
    private NSEService nseService;

    @GetMapping("/download")
    public DownloadResult download(@RequestParam String entity) {
        log.info("Request received for downloading : {}", entity);
        StatusMessage statusMessage = nseService.downloadStocksData(entity.trim());
        log.info("Download of entity: {}, resulted in {} with message: {}", entity, statusMessage.status(), statusMessage.message());
        return new DownloadResult(entity, statusMessage.status(), statusMessage.message());
    }


}
