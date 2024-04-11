package com.indvest.stocks.tracker.controller;

import com.indvest.stocks.tracker.bean.Response;
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

    @GetMapping("download")
    public Response download(@RequestParam String entity) {
        log.info("Request received for downloading : {}", entity);
        StatusMessage statusMessage = nseService.downloadStocksData(entity.trim());
        log.info("Download of entity: {}, resulted: {}, with message: {}", entity, statusMessage.status(), statusMessage.message());
        return new Response(entity, statusMessage.status(), statusMessage.message());
    }

    @GetMapping("store")
    public Response store(@RequestParam String entity) {
        log.info("Request received for storing : {}", entity);
        StatusMessage statusMessage = nseService.storeStocksData(entity.trim());
        log.info("Storing of entity: {}, resulted: {}, with message: {}", entity, statusMessage.status(), statusMessage.message());
        return new Response(entity, statusMessage.status(), statusMessage.message());
    }

    @GetMapping("load")
    public Response load(@RequestParam String symbol) {
        log.info("Request received for loading symbol : {}", symbol);
        StatusMessage statusMessage = nseService.loadStocksData(symbol.trim());
        log.info("Loading of symbol: {}, resulted: {}, with message: {}", symbol, statusMessage.status(), statusMessage.message());
        return new Response(symbol, statusMessage.status(), statusMessage.message());
    }

    @GetMapping("refresh")
    public Response refresh(@RequestParam String status) {
        log.info("Request received for refreshing instruments with status: {}", status);
        StatusMessage statusMessage = nseService.refreshStocksData(status);
        log.info("Refreshing of instruments resulted: {}, with message: {}", statusMessage.status(), statusMessage.message());
        return new Response("ALL", statusMessage.status(), statusMessage.message());
    }

}
