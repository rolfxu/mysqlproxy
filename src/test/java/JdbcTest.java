import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.List;

import com.maxleap.mysqlproxy.parser.SqlParserImpl;

public class JdbcTest {
    public static void main(String[] args) throws ParseException, Exception {
    	List<String> contents = Files.readAllLines( FileSystems.getDefault().getPath("D:\\mysqlproxy\\src\\test\\java\\simple.txt") );
    	
    	StringReader stream1 = new StringReader("select 1");
    	SqlParserImpl sqlParser = new SqlParserImpl(stream1);
    	for(int j=0;j<1;j++) {
    	long s = System.currentTimeMillis();
    	for(int i=0;i<1;i++) {
        	for(String line :contents) {
        		System.out.println(line);
        		if(line.isEmpty()) {
        			continue;
        		}
        		
        		StringReader stream = new StringReader(line);
        		sqlParser.ReInit(stream);
            	System.out.println("--"+sqlParser.SqlStmt());;
            	stream.close();
        	}
    	}
    	System.out.println(System.currentTimeMillis()-s);
    	}
    }
}
