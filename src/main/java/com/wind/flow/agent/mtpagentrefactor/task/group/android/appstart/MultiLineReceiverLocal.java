package com.wind.flow.agent.mtpagentrefactor.task.group.android.appstart;

import cn.hutool.core.io.FileUtil;
import com.android.ddmlib.MultiLineReceiver;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author wunanfang
 */
@Slf4j
public class MultiLineReceiverLocal extends MultiLineReceiver {

    private File file;

    public MultiLineReceiverLocal(File file){
        this.file = file;
    }

    @Override
    public void processNewLines(String[] lines) {
        if (lines == null || lines.length == 0){
            return;
        }
        for (String item : lines){
            FileUtil.appendString(item + System.getProperty("line.separator"), file, StandardCharsets.UTF_8);
        }
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
