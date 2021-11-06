package cn.gaohanghang.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @Description 文件工具类
 */
public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    public static void main(String[] args) throws Exception {
    }

    public static void createDirectories(String pathname) {
        File directories = new File(pathname);
        if (directories.exists()) {
            log.info("文件上传根目录已创建");
        } else { // 如果目录不存在就创建目录
            if (directories.mkdirs()) {
                log.info("创建多级目录成功");
            } else {
                log.info("创建多级目录失败");
            }
        }
    }

    /**
     * 采用命令行方式解压文件
     *
     * @param srcFilePath 压缩文件
     * @param destDir     解压结果路径
     * @return
     */
    public static boolean unRar(String srcFilePath, String destDir) {
        boolean bool = false;
        File zipFile = new File(srcFilePath);
        if (!zipFile.exists()) {
            return false;
        }

        File destFile = new File(destDir);
        if (!destFile.exists()) {
            boolean mkdirs = destFile.mkdirs();
            log.info("创建文件夹:{}", destDir);
            log.info("是否创建成功:{}", mkdirs);
        }

        String cmd = String.format("unrar x %s %s", srcFilePath, destDir);
        System.out.println(cmd);
        log.info("解压命令:{}", cmd);
        try {
            Process proc = Runtime.getRuntime().exec(cmd);

            InputStream errorStream = proc.getErrorStream();
            InputStream inputStream = proc.getInputStream();
            //处理InputStream的线程
            new Thread() {
                @Override
                public void run() {
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    try {
                        while ((line = in.readLine()) != null) {
                            System.out.println("info line:" + line);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("[shell exec error]:", e);
                    } finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

            new Thread() {
                @Override
                public void run() {
                    BufferedReader err = new BufferedReader(new InputStreamReader(errorStream));
                    String line = null;
                    try {
                        while ((line = err.readLine()) != null) {
                            System.out.println("error line:" + line);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("[shell exec error]:", e);
                    } finally {
                        try {
                            errorStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

            log.info("waitFor:{}", proc.waitFor());
            if (proc.waitFor() != 0) {
                if (proc.exitValue() == 0) {
                    bool = false;
                }
            } else {
                bool = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("解压:{}", (bool ? "成功" : "失败"));
        System.out.println("解压" + (bool ? "成功" : "失败"));
        return bool;
    }


    public static void unzip(String srcFilePath, String destDirPath) throws IOException {
        try {
            log.info("通过UTF-8解析");
            unzip(srcFilePath, destDirPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            log.info("通过GBK解析");
            unzip(srcFilePath, destDirPath, Charset.forName("GBK"));
        }
    }

    public static void unzip(String srcFilePath, String destDirPath, Charset charset) throws IOException {
        System.out.println("srcFilePath:" + srcFilePath);
        System.out.println("destDirPath:" + destDirPath);

        ZipFile zip = new ZipFile(srcFilePath, charset);//解决中文文件夹乱码
//        ZipFile zip = new ZipFile(srcFilePath, Charset.forName("GBK"));
//        String fileSuffix = zip.getName().substring(zip.getName().lastIndexOf('.'));
        // File pathFile = new File(descDir+name);
        File pathFile = new File(destDirPath);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = entries.nextElement();
            //文件解压多了一个文件加
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (destDirPath + "/" + zipEntryName).replaceAll("\\*", "/");
            if (outPath.contains("__MACOSX")) {
                continue;
            }
//            System.out.println("outPath:" + outPath);
            // 判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }

            // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }

            FileOutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }

        zip.close();

        System.out.println("******************解压完毕********************");

    }

    /**
     * whether it is a tar file
     *
     * @param filePath
     * @return
     */
    public static boolean isTar(String filePath) {
        if (filePath.toLowerCase().lastIndexOf(".tar.gz") != -1 || filePath.toLowerCase().lastIndexOf(".tar") != -1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * untar tar.gz file
     *
     * @param srcFile   source tar.gz file
     * @param outputDir target path
     * @throws IOException
     */
    public static void unTarGz(File srcFile, String outputDir) throws IOException {
        long start = System.currentTimeMillis();
        String path = srcFile.getPath();
        if (!isTar(path)) {
            throw new RuntimeException(srcFile.getPath() + ": not tar file");
        }

        // is file exists
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + ": file does not exist");
        }

        String suffix = srcFile.getName().substring(srcFile.getName().lastIndexOf(".") + 1);
        if ("gz".equals(suffix)) {
            suffix = "tar.gz";
        }

        String fileName = srcFile.getName().replaceAll("." + suffix, "");
        outputDir = String.format("%s/%s", outputDir, fileName);

        TarInputStream tarIn = null;
        try {
            tarIn = new TarInputStream(new GZIPInputStream(
                    new BufferedInputStream(new FileInputStream(srcFile))), 1024 * 2);

            TarEntry entry;
            while ((entry = tarIn.getNextEntry()) != null) {

                if (entry.isDirectory()) {
                    File dir = new File(outputDir + "/" + entry.getName());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                } else {
                    File tmpFile = new File(outputDir + "/" + entry.getName());
                    File dir = new File(tmpFile.getParent() + "/");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    OutputStream out = null;
                    try {
                        out = new FileOutputStream(tmpFile);
                        int length = 0;
                        byte[] b = new byte[2048];
                        while ((length = tarIn.read(b)) != -1) {
                            out.write(b, 0, length);
                        }
                    } catch (IOException ex) {
                        throw ex;
                    } finally {
                        if (out != null) {
                            out.close();
                        }
                    }
                }
            }

            long end = System.currentTimeMillis();

            System.out.println("finished，used：" + (end - start) + " ms");

        } catch (IOException ex) {
            throw new IOException("unTarGz error from ZipUtils", ex);
        } finally {
            try {
                if (tarIn != null) {
                    tarIn.close();
                }
            } catch (IOException ex) {
                throw new IOException("close tarFile exception", ex);
            }
        }
    }

    public static String converUnioncodeToNull(File file) {
        return converUnioncodeToNull(file, "utf-16");
    }

    public static String converUnioncodeToNull(File file, String chartsetName) {
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), chartsetName);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String lineText;
            StringBuffer convertBuffer = new StringBuffer();
            while ((lineText = bufferedReader.readLine()) != null) {
                lineText = lineText.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");
                lineText = lineText.replaceAll(",", "，");
                lineText = lineText.replaceAll("'", "，");
                convertBuffer.append(lineText);
                convertBuffer.append(" ");// 由于XML中Record中有换行
            }
            reader.close();
            return convertBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeFile(String filePath, String content) {
        try {
            File destFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(destFile, true);

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String line = System.getProperty("line.separator");

            bufferedWriter.write(content);
            bufferedWriter.write(line);

            bufferedWriter.flush();
            bufferedWriter.close();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getBasePath(String basePath, String batchNo) {
//        String batchNo = UUID.randomUUID().toString();
//        batchNo = "c82b47d3-e713-4e11-98db-7ec32da101a0";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        log.info("正在解析批次号：{}", batchNo);
        String dataPath = String.format("%s/case/%s/%s", basePath, sdf.format(new Date()), batchNo);
//        log.info("存储路径：{}", dataPath);
        File dataPathFile = new File(dataPath);
        if (!dataPathFile.exists()) {
            dataPathFile.mkdirs();
        }

        return dataPath;
    }


//    public static void filesSort(File[] files) {
//        List fileList = Arrays.asList(files);
//        Collections.sort(fileList, new Comparator<File>() {
//            @Override
//            public int compare(File o1, File o2) {
//                if (o1.isDirectory() && o2.isFile())
//                    return -1;
//                if (o1.isFile() && o2.isDirectory())
//                    return 1;
//                return o1.getName().compareTo(o2.getName());
//            }
//        });
//    }

    public static List<String> filesSort(File[] files) {
        List<File> fileList = Arrays.asList(files);
        List<String> collect = fileList.stream().map(s -> s.getName()).collect(Collectors.toList());

        char[][] chFileNames = new char[files.length][];
        for (int i = 0; i < collect.size(); i++) {
            chFileNames[i] = collect.get(i).toCharArray();
        }

        Arrays.sort(chFileNames, ChsLogicCmp);

        List<String> fileNames = new ArrayList<>();
        for (int i = 0; i < chFileNames.length; i++) {
            fileNames.add(new String(chFileNames[i]));
        }
        return fileNames;
    }


    static Comparator<char[]> ChsLogicCmp = new Comparator<char[]>() {
        class Int {
            public int i;
        }

        public int findDigitEnd(char[] arrChar, Int at) {
            int k = at.i;
            char c = arrChar[k];
            boolean bFirstZero = (c == '0');
            while (k < arrChar.length) {
                c = arrChar[k];
                //first non-digit which is a high chance.
                if (c > '9' || c < '0') {
                    break;
                } else if (bFirstZero && c == '0') {
                    at.i++;
                }
                k++;
            }
            return k;
        }

        @Override
        public int compare(char[] a, char[] b) {
            if (a != null || b != null) {
                Int aNonzeroIndex = new Int();
                Int bNonzeroIndex = new Int();
                int aIndex = 0, bIndex = 0,
                        aComparedUnitTailIndex, bComparedUnitTailIndex;

                while (aIndex < a.length && bIndex < b.length) {
                    //aIndex <
                    aNonzeroIndex.i = aIndex;
                    bNonzeroIndex.i = bIndex;
                    aComparedUnitTailIndex = findDigitEnd(a, aNonzeroIndex);
                    bComparedUnitTailIndex = findDigitEnd(b, bNonzeroIndex);
                    //compare by number
                    if (aComparedUnitTailIndex > aIndex && bComparedUnitTailIndex > bIndex) {
                        int aDigitIndex = aNonzeroIndex.i;
                        int bDigitIndex = bNonzeroIndex.i;
                        int aDigit = aComparedUnitTailIndex - aDigitIndex;
                        int bDigit = bComparedUnitTailIndex - bDigitIndex;
                        //compare by digit
                        if (aDigit != bDigit) {
                            return aDigit - bDigit;
                        }
                        //the number of their digit is same.
                        while (aDigitIndex < aComparedUnitTailIndex) {
                            if (a[aDigitIndex] != b[bDigitIndex]) {
                                return a[aDigitIndex] - b[bDigitIndex];
                            }
                            aDigitIndex++;
                            bDigitIndex++;
                        }
                        //if they are equal compared by number, compare the number of '0' when start with "0"
                        //ps note: paNonZero and pbNonZero can be added the above loop "while", but it is changed meanwhile.
                        //so, the following comparsion is ok.
                        aDigit = aNonzeroIndex.i - aIndex;
                        bDigit = bNonzeroIndex.i - bIndex;
                        if (aDigit != bDigit) {
                            return aDigit - bDigit;
                        }
                        aIndex = aComparedUnitTailIndex;
                        bIndex = bComparedUnitTailIndex;
                    } else {
                        if (a[aIndex] != b[bIndex]) {
                            return a[aIndex] - b[bIndex];
                        }
                        aIndex++;
                        bIndex++;
                    }

                }

            }
            return a.length - b.length;
        }
    };


    public static boolean deleteDir(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }

        if (file.isFile()) {
            boolean delete = file.delete();
            if (!delete) {
                log.error("删除失败：{}", filePath);
            }

            return delete;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File nfile : files) {
                deleteDir(nfile.getAbsolutePath());
            }
        }
        return file.delete();
    }

    private static void sortFiles(List<File> files) {
        //按文件名称排序
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    /**
     * 合并被分割的文件
     *
     * @param dest
     * @param files
     */
    public static String mergeZipFile(String dest, List<File> files) {
        String filename = files.get(0).getName();
        filename = filename.substring(0, filename.indexOf(".zip"));
        String dstFilePath = dest + File.separator + filename;
        if (!dstFilePath.endsWith(".zip")) {
            dstFilePath = String.format("%s.zip", dstFilePath);
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dstFilePath));
            BufferedInputStream bis = null;
            byte bytes[] = new byte[1024 * 1024];
            int len = -1;
            for (int i = 0; i < files.size(); i++) {
                bis = new BufferedInputStream(new FileInputStream(files.get(i)));
                while ((len = bis.read(bytes)) != -1) {
                    bos.write(bytes, 0, len);
                }
                bis.close();
            }
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dstFilePath;
    }

    public static String mergeZipFileToLinux(String dest, List<File> files) {
        String filename = files.get(0).getName();
        if(files.get(0).getName().contains("zip")){
            filename = filename.substring(0, filename.indexOf(".zip"));
        }else{
            filename = filename.substring(0, filename.indexOf(".ZIP"));
        }

        String dstFilePath = dest + File.separator + filename;
        if (!dstFilePath.endsWith(".zip")) {
            dstFilePath = String.format("%s.zip", dstFilePath);
        }
        try {
            //> |等特殊字符无法直接用命令
            ArrayList<String>   arrayList=new ArrayList<>();
            String commandStr = "";
            if(files.get(0).getName().contains("zip")){
                commandStr = String.format("cat %s/*-ATTACHMENT.zip* > %s", dest, dstFilePath);
            }else{
               commandStr = String.format("cat %s/*-ATTACHMENT.ZIP* > %s", dest, dstFilePath);
            }

            arrayList.add("/bin/sh");
            arrayList.add("-c");
            arrayList.add(commandStr);
            System.out.println("commandStr:" + arrayList);
            Process p = Runtime.getRuntime().exec(arrayList.toArray(new String[arrayList.size()]));

            p.waitFor();






//            System.out.println("wf:" + wf);
//            int i = p.exitValue();
//            System.out.println("i:" + i);
//            p.destroy();
//            p = null;
        } catch ( InterruptedException |IOException e) {
            e.printStackTrace();
        }
        return dstFilePath;
    }

    private static boolean isLinux() {
        String OS = System.getProperty("os.name").toLowerCase();
        return OS.indexOf("linux") >= 0;
    }

    private static boolean isMac() {
        String OS = System.getProperty("os.name").toLowerCase();
        return OS.startsWith("Mac OS");
    }

    public static String mergeZipByOs(String dest, List<File> files) {
        if (isLinux()) {
            return mergeZipFileToLinux(dest, files);
        } else if (isMac()) {
            return mergeZipFileToLinux(dest, files);
        } else {
            return mergeZipFile(dest, files);
        }
    }

}
