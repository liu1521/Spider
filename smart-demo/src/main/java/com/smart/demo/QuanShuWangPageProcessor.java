package com.smart.demo;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import java.util.logging.Logger;

public class QuanShuWangPageProcessor implements PageProcessor {

    private Logger logger = Logger.getLogger(this.getClass().toString());

    public static final String URL_LIST = "http://www\\.quanshuwang\\.com/list/\\w+\\.html";

    public static final String URL_DESCRIPTION = "http://www\\.quanshuwang\\.com/\\w+\\.html";

    public static final String URL_CATALOG = "http://www\\.quanshuwang\\.com/book/\\w+/\\w+";

    public static final String URL_PASSAGE = "http://www\\.quanshuwang\\.com/book/\\w+/\\w+/\\w+\\.html";

    private Site site = Site
            .me()
            .setTimeOut(6000)
            .setRetryTimes(3000)
            .setSleepTime(1000)
            .setRetryTimes(3);

    @Override
    public void process(Page page) {
        logger.info(page.getUrl().toString());
        if (page.getUrl().regex(URL_LIST).match()) {
            page.addTargetRequests(page.getHtml().xpath("//ul[@class=\"seeWell\"]").links().regex(URL_DESCRIPTION).all());
            page.addTargetRequests(page.getHtml().links().regex(URL_LIST).all());
            // 描述页
        } else if (page.getUrl().regex(URL_DESCRIPTION).match()){
            page.addTargetRequest(page.getHtml().xpath("//a[@class=\"reader\"").links().regex(URL_CATALOG).get());
            page.putField("bookName_inDescription", page.getHtml().xpath("//div[@class=\"b-info\"]/h1/text()"));
            page.putField("introduce", page.getHtml().xpath("//div[@id=\"waa\"]/text()"));
            page.putField("status",page.getHtml().xpath("//div[@class=\"bookDetail\"]/dl/dd/text()"));
            page.putField("author", page.getHtml().xpath("//dl[@class=\"bookso\"]/dd/text()"));
            // 章节页
        } else if (page.getUrl().regex(URL_PASSAGE).match()) {
            page.putField("bookName_inPassages",
                    page.getHtml().xpath("//em[@class=\"l\"]/text()").replace("《", "").replace("》", ""));
            page.putField("title", page.getHtml().xpath("//strong[@class=\"l\"]/text()"));
            page.putField("data",
                    page.getHtml().xpath("//div[@id=\"content\"]/text()"));
            // 目录页
        } else {
            page.addTargetRequests(page.getHtml().links().regex(URL_PASSAGE).all());
        }
    }

    @Override
    public Site getSite() {
        return site;
    }



    public static void main(String[] args) {

        Spider spider = Spider.create(new QuanShuWangPageProcessor()).addUrl("http://www.quanshuwang.com/book_9055.html");
        spider.addPipeline(new FilePipe("books"))
                .thread(8)
                .run();
    }
}