package com.trigger.batch.partitioner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class SplitFilePartitioner implements Partitioner {

    @Value("${batch.files.paths.split.dir}")
    private String splitDir;

    @Override
    public @NonNull Map<String, ExecutionContext> partition(int gridSize) {
        File dir = new File(splitDir);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));

        Map<String, ExecutionContext> map = new HashMap<>();
        for (int i = 0; i < files.length; i++) {
            ExecutionContext context = new ExecutionContext();
            context.putString("filePath", files[i].getAbsolutePath());
            map.put("partition" + i, context);
        }
        return map;
    }
}

