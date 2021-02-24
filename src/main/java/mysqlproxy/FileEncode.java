package mysqlproxy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class FileEncode {
	public static void main(String[] args) throws Exception {
		LineIterator li = FileUtils.lineIterator(new File(args[0]) );
		EncryptTestUser test= new EncryptTestUser();
		EncryptUser  online = new EncryptUser();
		String head = li.next();
		int column = Integer.valueOf( args[1] );
		File target = new File(args[0]+".new");
		FileUtils.write(target, head+"\n","utf-8", true);
		List<String> list = new ArrayList<>();
		int j=0;
		a:
		for(;li.hasNext();) {
			j++;
			String line = li.next();
			String[] columns = line.split(",");
			StringBuilder newLine = new StringBuilder();
			for( int i=0;i<columns.length;i++ ) {
				if(i==column) {
					try {
						String nc = test.encrypt( online.decrypt(columns[i]) );
						newLine.append(nc);
					}catch(Exception e) {
						System.out.println(line);
						continue a;
					}

				} else {
					newLine.append(columns[i]);
				}
				if(i!=columns.length-1) {
					newLine.append(",");
				}
			}
			list.add(newLine.toString());
			if(j%100000==0) {
				FileUtils.writeLines(target, list, true);
				list.clear();
			}
		}
		FileUtils.writeLines(target, list, true);
	}
}
