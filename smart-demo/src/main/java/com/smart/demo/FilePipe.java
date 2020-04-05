package com.smart.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

public class FilePipe extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public FilePipe() {
        this.setPath("/data/webmagic/");
    }

    public FilePipe(String path) {
        this.setPath(path);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        if (resultItems.getAll() == null || resultItems.getAll().size() == 0) return;

        try {
            Iterator iterator = resultItems.getAll().entrySet().iterator();
            PrintWriter printWriter = null;

            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry) iterator.next();
                if ("bookName_inDescription".equals(entry.getKey())) {
                    Object bookName = entry.getValue();
                    printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.getFile(this.path + bookName.toString().hashCode() + File.separator + "info")), "UTF-8"));
                    printWriter.println(entry.getKey() + ":\t" + entry.getValue());
                } else if ("bookName_inPassages".equals(entry.getKey())) {
                    Object bookName = entry.getValue();
                    entry = (Map.Entry) iterator.next();
                    Object title = entry.getValue();
                    printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.getFile(this.path + bookName.toString().hashCode() + File.separator + title.toString().hashCode())), "UTF-8"));
                    printWriter.println("bookName:\t" + bookName);
                    printWriter.println(entry.getKey() + ":\t" + entry.getValue());
                } else {
                    printWriter.println(entry.getKey() + ":\t" + entry.getValue());
                }
            }

            if (printWriter!=null)
                printWriter.close();
        } catch (IOException var10) {
            this.logger.warn("write file error", var10);
        }
    }
}
