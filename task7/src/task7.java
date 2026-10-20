
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class task7 {

    public static void main(String[] args) throws IOException {

        // 1. 定义文件路径
        String sourcePath = "task7/test-files/myWords.txt";
        String partsDir = "task7/test-files/parts";
        String mergedPath = "task7/test-files/merged.txt";

        // 2. 执行文件分片
        splitFile(sourcePath, partsDir, 100);

        // 3. 合并所有分片
        mergeFile(partsDir, "myWords.txt", mergedPath);

        // 4. 比较原文件与合并后的文件
        long result = Files.mismatch(
                Path.of(sourcePath),
                Path.of(mergedPath)
        );

        if (result == -1) {
            System.out.println("验证成功：原文件与合并文件完全一致！");
        } else {
            System.out.println("验证失败：文件在第 " + result + " 个字节处出现差异！");
        }

    }

    public static void splitFile(
            String sourceFilePath,
            String outputDirPath,
            int chunkSize
    ) throws IOException {

        // 1. 检查分片大小是否合法
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("分片大小必须大于0");
        }

        // 2. 获取源文件
        File sourceFile = new File(sourceFilePath);

        if (!sourceFile.isFile()) {
            throw new IOException("源文件不存在或不是一个文件");
        }

        // 3. 创建输出目录
        File outputDir = new File(outputDirPath);

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // 4. 打开源文件并读取数据
        try (FileInputStream input = new FileInputStream(sourceFile)) {

            byte[] buffer = new byte[chunkSize];
            int partNumber = 1;

            while (true) {

                int totalRead = 0;

                // 尽量填满当前分片
                while (totalRead < buffer.length) {
                    int len = input.read(
                            buffer,
                            totalRead,
                            buffer.length - totalRead
                    );

                    if (len == -1) {
                        break;
                    }

                    totalRead += len;
                }

                // 没有新数据，结束分片
                if (totalRead == 0) {
                    break;
                }

                // 5. 创建分片文件
                String partName =
                        sourceFile.getName() + ".part" + partNumber;

                File partFile = new File(outputDir, partName);

                // 6. 写入实际读取的字节
                try (FileOutputStream output =
                             new FileOutputStream(partFile)) {
                    output.write(buffer, 0, totalRead);
                }

                System.out.println(
                        "已生成分片：" + partFile.getName()
                                + "，大小：" + totalRead + " 字节"
                );

                partNumber++;
            }
        }
    }
    public static void mergeFile(
            String targetDirPath,
            String originalFileName,
            String mergedFilePath
    ) throws IOException {

        // 1. 获取分片所在目录
        File targetDir = new File(targetDirPath);

        if (!targetDir.isDirectory()) {
            throw new IOException("分片目录不存在");
        }

        // 2. 找到属于原始文件的所有分片
        String prefix = originalFileName + ".part";

        File[] partFiles = targetDir.listFiles(
                (dir, name) ->
                        name.startsWith(prefix)
                                && name.substring(prefix.length()).matches("\\d+")
        );

        if (partFiles == null || partFiles.length == 0) {
            throw new IOException("没有找到分片文件");
        }
        Arrays.sort(partFiles, (file1, file2) -> {
            int number1 = Integer.parseInt(
                    file1.getName().substring(prefix.length())
            );

            int number2 = Integer.parseInt(
                    file2.getName().substring(prefix.length())
            );

            return Integer.compare(number1, number2);
        });

        File mergedFile = new File(mergedFilePath);

// 创建输出文件的父目录（如果有必要）
        File parentDir = mergedFile.getParentFile();

        if (parentDir != null && !parentDir.exists()
                && !parentDir.mkdirs()) {
            throw new IOException("无法创建合并文件的输出目录");
        }

// 打开最终合并文件
        try (FileOutputStream output = new FileOutputStream(mergedFile)) {

            // 创建读取数据的缓冲区
            byte[] buffer = new byte[4096];

            // 按照排序后的顺序处理每个分片
            for (File partFile : partFiles) {

                try (FileInputStream input = new FileInputStream(partFile)) {

                    int len;

                    // 读取当前分片，直到读完
                    while ((len = input.read(buffer)) != -1) {

                        // 将实际读取的字节写入合并文件
                        output.write(buffer, 0, len);
                    }
                }
            }
        }
        // 删除所有分片文件
        for (File partFile : partFiles) {
            if (!partFile.delete()) {
                throw new IOException(
                        "分片文件删除失败：" + partFile.getName()
                );
            }
        }

        System.out.println("文件合并完成：" + mergedFilePath);
        System.out.println("所有分片文件已删除");

    }

}
