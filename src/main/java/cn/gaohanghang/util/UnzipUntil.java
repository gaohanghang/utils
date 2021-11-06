package cn.gaohanghang.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

/**
 * 解压工具类
 */
public class UnzipUntil {

    public static void main(String[] args) throws IOException {
        /**
         * 解压文件
         */
        File zipFile = new File("C:\\Users\\wangdou\\Desktop\\123");
        is_or_not_a_Dic(zipFile);

    }

    public static void is_or_not_a_Dic(File zipFile) {
        String aimpath = null;
        if (zipFile.isDirectory()) {
            File[] listFiles = zipFile.listFiles();
            for (int i = 0; i < listFiles.length; i++) {


                //循环遍历    ：如果listFiles[i]是目录，则判断目录中是否有压缩包，若有，则解压到相应的位置
                if (listFiles[i].isDirectory()) {
                    is_or_not_a_Dic(listFiles[i]);
                }

                //如果listFiles[i]是文件，确实自拍压缩包，则解压
                if (listFiles[i].toString().endsWith(".zip")) {
                    try {
                        aimpath = listFiles[i].getAbsolutePath().replaceAll(".zip", "");   //解压文件的父目录
                        unZipFiles(listFiles[i], aimpath);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }    //listFiles[i]：需要解压的压缩包   aimpath：解压的的目录（同级目录）
                }
            }
        } else {

            if (zipFile.toString().endsWith(".zip")) {

                aimpath = zipFile.getAbsolutePath().replaceAll(".zip", ""); //解压文件的父目录
                try {
                    unZipFiles(zipFile, aimpath);   //listFiles[i]：需要解压的压缩包   aimpath：解压的的目录（同级目录）
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 解压到指定目录
     *
     * @param zipPath
     * @param descDir
     */
    public static void unZipFiles(String zipPath, String descDir) throws IOException {
        unZipFiles(new File(zipPath), descDir);
    }

    /**
     * 解压文件到指定目录
     *
     * @param zipFile
     * @param descDir
     * @author 今页一点
     */
    @SuppressWarnings("rawtypes")
    public static void unZipFiles(File zipFile, String descDir) throws IOException {

        //创建目录文件
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        @SuppressWarnings("resource")
        ZipFile zip = new ZipFile(zipFile);

        for (Enumeration entries = zip.getEntries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + "/" + zipEntryName).replaceAll("\\*", "/");
            //判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }
            //输出文件路径信息
            System.out.println(outPath);

            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }

            in.close();
            out.close();
        }
        System.out.println("******************解压完毕********************");
    }
}
