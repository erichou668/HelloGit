/**
 * @description	用于管理文件读写的工具类
 *
 * @author  侯少龙
 * @date	2014-10-12
 */

package fetchfootball;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FileWriteUtil {
	
    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            System.out.println("create directory " + destDirName + " fail: exist");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            System.out.println("create directory" + destDirName + " successful");
            return true;
        } else {
            System.out.println("create directory" + destDirName + " fail");
            return false;
        }
    }
    
	public static void writeStringToFile(String filename, String string) throws FileNotFoundException, IOException {
		OutputStream fos = new FileOutputStream(filename);
		OutputStreamWriter out;
		out = new OutputStreamWriter(fos, "UTF-16LE");
		out.write(0xFEFF);
		out.write(string);
		out.close();
	}
}

