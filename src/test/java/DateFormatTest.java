import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class DateFormatTest {

	public static void main(String[] args) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd/HHmmss");
//		System.out.println(sdf.parse("20180101/121202").getTime()*1000_000);
		
		List<String> lines = FileUtils.readLines(new File("D:\\mysqlproxy\\src\\test\\java\\a"),"utf-8");
		for(String line : lines) {
			String[] a = line.split("-");
			System.out.println(a[1]+"---"+sdf.format( new Date(sdf.parse(a[1]).getTime())  ));
//			System.out.println(String.format("select * from freight_track where sim='%s' and time=%s;", a[0],sdf.parse(a[1]).getTime()*1000_000 ));
		}
	}

}
