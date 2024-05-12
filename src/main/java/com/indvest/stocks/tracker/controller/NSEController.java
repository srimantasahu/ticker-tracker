package com.indvest.stocks.tracker.controller;

import com.indvest.stocks.tracker.bean.ResponseBody;
import com.indvest.stocks.tracker.bean.*;
import com.indvest.stocks.tracker.service.NSEService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Response load(@RequestParam List<String> instruments) {
        log.info("Request received for loading instruments : {}", instruments);
        StatusMessage statusMessage = nseService.loadStocksData(instruments);
        log.info("Loading of instruments: {}, resulted: {}, with message: {}", instruments, statusMessage.status(), statusMessage.message());
        return new Response(instruments.toString(), statusMessage.status(), statusMessage.message());
    }

    @GetMapping("refresh")
    public Response refresh(@RequestParam String status) {
        log.info("Request received for refreshing instruments with status: {}", status);
        StatusMessage statusMessage = nseService.refreshStocksData(status);
        log.info("Refreshing of instruments resulted: {}, with message: {}", statusMessage.status(), statusMessage.message());
        return new Response(status, statusMessage.status(), statusMessage.message());
    }

    @GetMapping("reload")
    public Response reload(@RequestParam String type) {
        log.info("Request received for refreshing instruments with status: {}", type);
        StatusMessage statusMessage = nseService.reloadStocksData(type);
        log.info("Refreshing of instruments resulted: {}, with message: {}", statusMessage.status(), statusMessage.message());
        return new Response(type, statusMessage.status(), statusMessage.message());
    }

    @GetMapping("query")
    public ResponseBody query(@RequestParam String industry, @RequestParam String type, @RequestParam String order) {
        log.info("Query received for industry: {}, type: {}, and order by: {}", industry, type, order);
        StatusBody statusBody = nseService.getStocksData(industry, type, order);
        log.info("Querying of instruments resulted: {}, with results count: {}", statusBody.status(), CollectionUtils.size(statusBody.results()));
        return new ResponseBody(new QueryParams(industry, type), statusBody.status(), statusBody.results(), statusBody.message());
    }

    @PostMapping("buynsell")
    public Response buyNSell(@RequestBody BuyNSell buyNSell) {
        log.info("Request received for buyNSell: {}", buyNSell);
        StatusMessage statusMessage = nseService.storeBuyNSell(buyNSell);
        log.info("Request for buyNSell resulted: {}, with message: {}", statusMessage.status(), statusMessage.message());
        return new Response(buyNSell.symbol(), statusMessage.status(), statusMessage.message());
    }

}
