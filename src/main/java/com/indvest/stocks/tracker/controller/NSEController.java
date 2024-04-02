package com.indvest.stocks.tracker.controller;

import com.indvest.stocks.tracker.bean.DownloadResult;
import com.indvest.stocks.tracker.bean.StatusMessage;
import com.indvest.stocks.tracker.service.NSEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nse/")
public class NSEController {

    @Autowired
    private NSEService nseService;

    @GetMapping("/download")
    public DownloadResult download(@RequestParam String entity) {
        StatusMessage statusMessage = nseService.downloadStocksData(entity);
        return new DownloadResult(entity, statusMessage.status(), statusMessage.message());
    }


}
